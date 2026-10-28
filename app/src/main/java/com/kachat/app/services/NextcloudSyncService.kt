package com.kachat.app.services

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * The wallet's Nextcloud backup: the Address Book and the Portfolio, nothing else, as two plain
 * JSON files in the KaChat folder (iOS wallet `NextcloudService`, 301ebfb + 59593af + 5bab351;
 * [NextcloudService] holds the account and the WebDAV calls):
 *
 *   * **Back Up Now** and **Automatic Backup** both come to [runBackup]: the Address Book export
 *     file to [NextcloudService.ADDRESS_BOOK_FILE_NAME], then the Portfolio - the server's copy
 *     merged in first ([PortfolioSync.merge], newest edit or deletion wins), so two devices never
 *     overwrite each other - to [NextcloudService.PORTFOLIO_FILE_NAME].
 *   * **Automatic Backup** (off by default, per wallet) uploads [BACKUP_DEBOUNCE_MS] after the
 *     Address Book or the Portfolio changes ([noteBackupDataChanged], called by
 *     [AddressBookManager] and [PortfolioLedgerStore] on every save), and when the app goes to
 *     the background with a change still owed ([autoBackupIfDue], KaChatApplication's onStop).
 *     The owed flag is persisted, so a change made just before the process died still goes up on
 *     the next launch, foreground or [NextcloudAutoSyncWorker] run.
 *   * **Restore** ([restoreBackup]) and a **fresh connection** ([connect], once, by itself) merge
 *     both files back: the Address Book through [AddressBookManager.importExport] (an address not
 *     saved here is added, one saved here takes the backup's version only when that is newer),
 *     the Portfolio through [PortfolioLedgerStore.importFromArchive]. Either file may be missing;
 *     each falls back to its pre-rename "KaChat Wallet …" name.
 *
 * KaChat's chat history (`kachat-backup.json`, the change watcher, the chat restore) is no part of
 * it (iOS 301ebfb), and neither are the shims KaChat's chat code called.
 *
 * Every upload or restore snapshots the active wallet and re-checks it before each write, so a
 * wallet switch mid-way never writes one account's data into another's files.
 */
@Singleton
class NextcloudSyncService @Inject constructor(
    @ApplicationContext private val context: Context,
    private val dataStore: DataStore<Preferences>,
    private val walletManager: WalletManager,
    private val nextcloudService: NextcloudService,
    // Lazy: each of these reaches this class back (lazily) to report its changes.
    private val addressBookManager: dagger.Lazy<AddressBookManager>,
    private val portfolioLedgerStore: dagger.Lazy<PortfolioLedgerStore>,
    private val portfolioRepository: dagger.Lazy<com.kachat.app.repository.PortfolioRepository>,
) {
    companion object {
        private const val TAG = "NextcloudBackup"

        /** Quiet time after a change before the upload, so a burst of edits is one (iOS
         *  `backupDebounce`, 5 s). */
        const val BACKUP_DEBOUNCE_MS = 5_000L

        /** Periodic WorkManager fallback cadence for an owed upload the in-process paths missed. */
        const val PERIODIC_INTERVAL_HOURS = 6L

        const val WORK_NAME = "kachat_nextcloud_auto_sync"

        /** Kept for KaChatApplication's call sites; an upload now runs whenever one is owed. */
        const val AUTO_BACKUP_MIN_INTERVAL_MS = 0L
        const val AUTO_BACKUP_CATCH_UP_INTERVAL_MS = 0L

        // KaChat's chat-sync DataStore keys (per wallet), never written any more: removed on
        // account deletion so nothing of an older build's state is left behind.
        private val LEGACY_KEY_BASES = listOf(
            "nextcloud_auto_sync_last_ms", "nextcloud_auto_sync_pending", "nextcloud_auto_restore_done",
            "nextcloud_auto_sync_etag", "nextcloud_auto_sync_merged_etag",
        )
    }

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO + com.kachat.app.util.StartupSafety.logOnlyHandler("NextcloudSyncService"))

    /** Backups and restores run one at a time: two uploads of the same file at once had Nextcloud
     *  answering the second with HTTP 423 (its write lock), and a restore must not interleave
     *  with an upload. */
    private val backupMutex = Mutex()

    /** The pending upload after a change - cancelled on an account switch or disconnect so one
     *  account's data never uploads while another is active (iOS `backupTask`). */
    @Volatile
    private var debounceJob: Job? = null

    /** When the active wallet's backup last uploaded (epoch ms), null = never. */
    val lastBackupMs: StateFlow<Long?> get() = nextcloudService.lastBackupMs

    init {
        // The periodic fallback job is enqueued exactly while it can do anything: an account
        // connected for the active wallet with Automatic Backup on.
        scope.launch {
            combine(nextcloudService.account, nextcloudService.autoBackupEnabled) { account, auto ->
                account != null && auto
            }.distinctUntilChanged().collect { active ->
                if (active) ensurePeriodicWork() else cancelScheduledWork()
            }
        }
        // A wallet switch, logout or disconnect drops the pending upload; a wallet that owes one
        // (a change made just before the app was closed) gets it scheduled (iOS setCurrentWallet).
        scope.launch {
            var previous: Pair<String, String>? = null
            combine(walletManager.activeAddressFlow, nextcloudService.account, nextcloudService.autoBackupEnabled) { address, account, auto ->
                // The wallet and server a backup would go to, or null while none would run.
                if (address != null && account != null && auto) address to account.server else null
            }.distinctUntilChanged().collect { active ->
                // Another wallet, another server, disconnected or switched off: the upload that
                // was pending belongs to what was active before.
                if (previous != null && active != previous) {
                    debounceJob?.cancel()
                    debounceJob = null
                }
                // Newly active with a change still owed. The toggle's own immediate upload
                // (setAutoBackupEnabled) may already be on its way; it is not replaced.
                if (active != null && active != previous && debounceJob?.isActive != true && nextcloudService.isBackupPending) {
                    scheduleBackup(BACKUP_DEBOUNCE_MS)
                }
                previous = active
            }
        }
    }

    // -------------------------------------------------------------------------
    // Automatic Backup
    // -------------------------------------------------------------------------

    /**
     * The Address Book or Portfolio changed ([AddressBookManager] and [PortfolioLedgerStore] call
     * this on every save): with Automatic Backup on, it uploads a few seconds after the changes
     * settle (iOS `noteBackupDataChanged`).
     */
    fun noteBackupDataChanged() {
        if (!nextcloudService.autoBackupEnabled.value || !nextcloudService.isConnected) return
        nextcloudService.setBackupPending(true)
        scheduleBackup(BACKUP_DEBOUNCE_MS)
    }

    /**
     * Settings' Automatic Backup toggle (iOS `setAutoBackupEnabled`): turning it on uploads right
     * away, turning it off drops a pending upload.
     */
    fun setAutoBackupEnabled(enabled: Boolean) {
        nextcloudService.setAutoBackupEnabled(enabled)
        if (enabled) {
            nextcloudService.setBackupPending(true)
            scheduleBackup(0L)
        } else {
            debounceJob?.cancel()
            debounceJob = null
        }
    }

    private fun scheduleBackup(delayMs: Long) {
        if (!nextcloudService.autoBackupEnabled.value || !nextcloudService.isConnected) return
        val walletAtStart = nextcloudService.activeWalletAddress ?: return
        debounceJob?.cancel()
        debounceJob = scope.launch {
            if (delayMs > 0) delay(delayMs)
            if (nextcloudService.activeWalletAddress != walletAtStart) return@launch
            try {
                runBackup()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                // Kept owed: the next change, background or launch retries.
                Log.w(TAG, "Automatic backup failed: ${e.message}")
            }
        }
    }

    /**
     * Going to the background (KaChatApplication's onStop) or coming back (onStart) with a change
     * still owed: upload it now (iOS `backUpPendingInBackground`). [minIntervalMs] is ignored - a
     * backup is owed or it isn't. Never throws.
     */
    @Suppress("UNUSED_PARAMETER")
    suspend fun autoBackupIfDue(minIntervalMs: Long = AUTO_BACKUP_MIN_INTERVAL_MS) {
        uploadIfOwed()
    }

    /** [NextcloudAutoSyncWorker]'s body: an owed upload the in-process paths missed (the process
     *  died first). No network at all when nothing is owed. Never throws. */
    suspend fun periodicUploadIfNeeded() {
        uploadIfOwed()
    }

    private suspend fun uploadIfOwed() {
        if (!nextcloudService.autoBackupEnabled.value || !nextcloudService.isConnected) return
        if (!nextcloudService.isBackupPending) return
        try {
            runBackup()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.w(TAG, "Automatic backup failed (a later trigger retries): ${e.message}")
        }
    }

    // -------------------------------------------------------------------------
    // Backup and restore
    // -------------------------------------------------------------------------

    /**
     * Uploads this wallet's Address Book to [NextcloudService.ADDRESS_BOOK_FILE_NAME] and its
     * Portfolio to [NextcloudService.PORTFOLIO_FILE_NAME], replacing the previous backup. The
     * Portfolio on the server is merged in first, so another device's edits and deletions
     * survive. The manual Back Up Now and the automatic backup both come here (iOS `runBackup`).
     */
    suspend fun runBackup(): Unit = backupMutex.withLock {
        val walletAtStart = nextcloudService.activeWalletAddress
            ?: throw NextcloudException("Sign in to an account before connecting Nextcloud.")
        val addressBook = withContext(Dispatchers.IO) { addressBookManager.get().exportData() }
        nextcloudService.uploadToKaChatFolder(
            addressBook, NextcloudService.ADDRESS_BOOK_FILE_NAME, "application/json", keepSpaces = true
        )
        if (nextcloudService.activeWalletAddress != walletAtStart) return@withLock

        downloadPortfolioBackup()?.let { remote ->
            if (nextcloudService.activeWalletAddress != walletAtStart) return@withLock
            importPortfolio(walletAtStart, remote)
        }
        val portfolio = PortfolioBackupFile.encode(portfolioLedgerStore.get().syncState(walletAtStart).forArchive())
        if (nextcloudService.activeWalletAddress != walletAtStart) return@withLock
        nextcloudService.uploadToKaChatFolder(
            portfolio, NextcloudService.PORTFOLIO_FILE_NAME, "application/json", keepSpaces = true
        )
        if (nextcloudService.activeWalletAddress != walletAtStart) return@withLock
        nextcloudService.recordBackupFinished()
        Log.i(TAG, "Contacts and Portfolio backed up")
        Unit
    }

    /** What a restore brought in (iOS `RestoreResult`). */
    data class RestoreResult(
        val addressesAdded: Int = 0,
        val addressesUpdated: Int = 0,
        /** The Portfolio backup changed this device's portfolios. */
        val portfolioChanged: Boolean = false,
    ) {
        val addresses: Int get() = addressesAdded + addressesUpdated
        val changedAnything: Boolean get() = addresses > 0 || portfolioChanged
    }

    /**
     * Merges the backup into this wallet (iOS `restoreBackup`):
     * - Address Book ([AddressBookManager.importExport]): an address not saved here is added, one
     *   saved here takes the backup's version only when that is newer.
     * - Portfolio ([PortfolioLedgerStore.importFromArchive]): the newest edit or deletion wins
     *   per portfolio and transaction.
     * Either file may be missing; throws when both are.
     */
    suspend fun restoreBackup(): RestoreResult = backupMutex.withLock {
        val walletAtStart = nextcloudService.activeWalletAddress
            ?: throw NextcloudException("Sign in to an account before connecting Nextcloud.")
        val addressBook = nextcloudService.downloadBackupFile(
            NextcloudService.ADDRESS_BOOK_FILE_NAME, NextcloudService.LEGACY_ADDRESS_BOOK_FILE_NAMES
        )
        val portfolio = downloadPortfolioBackup()
        if (addressBook == null && portfolio == null) throw NextcloudException("No backup was found in the KaChat folder.")
        if (nextcloudService.activeWalletAddress != walletAtStart) throw CancellationException("The active account changed.")

        var result = RestoreResult()
        if (addressBook != null) {
            // As iOS's restoreBackup: an Address Book export that can't be imported (an empty
            // one included) fails the restore, before the Portfolio is merged.
            val imported = withContext(Dispatchers.IO) { addressBookManager.get().importExport(addressBook) }
            result = result.copy(addressesAdded = imported.added, addressesUpdated = imported.updated)
        }
        if (portfolio != null) {
            result = result.copy(portfolioChanged = importPortfolio(walletAtStart, portfolio))
        }
        result
    }

    /** The Portfolio backup on the server, or null when there is none yet. */
    private suspend fun downloadPortfolioBackup(): PortfolioSync? {
        val data = nextcloudService.downloadBackupFile(
            NextcloudService.PORTFOLIO_FILE_NAME, NextcloudService.LEGACY_PORTFOLIO_FILE_NAMES
        ) ?: return null
        return PortfolioBackupFile.decode(data)
    }

    /** Merges [incoming] into [wallet]'s portfolios (iOS `PortfolioViewModel.importFromArchive`);
     *  rows that arrived still waiting for a price get priced, as iOS's reload restarts its
     *  backfill. Returns whether anything here changed. */
    private suspend fun importPortfolio(wallet: String, incoming: PortfolioSync): Boolean {
        val changed = portfolioLedgerStore.get().importFromArchive(wallet, incoming)
        if (changed) runCatching { portfolioRepository.get().startPriceBackfillIfNeeded(wallet) }
        return changed
    }

    // -------------------------------------------------------------------------
    // What the settings page starts (run here, so leaving the page never cuts one off)
    // -------------------------------------------------------------------------

    /**
     * Connects [NextcloudService.connect] and, once connected, restores a backup this account
     * made on another device by itself, once - with a toast saying what came back (iOS `connect`
     * + `restoredToast`). The returned job finishes with the connection; the restore runs on.
     */
    fun connectAsync(serverInput: String, username: String, appPassword: String): Deferred<Unit> = scope.async {
        nextcloudService.connect(serverInput, username, appPassword)
        scope.launch {
            val result = try {
                restoreBackup()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.i(TAG, "No restore on connect: ${e.message}")
                return@launch
            }
            if (result.changedAnything) {
                com.kachat.app.ui.screens.IosToasts.show(restoredToast(result))
            }
        }
        Unit
    }

    /** Back Up Now. */
    fun backUpNowAsync(): Deferred<Unit> = scope.async { runBackup() }

    /** Restore Backup. */
    fun restoreAsync(): Deferred<RestoreResult> = scope.async { restoreBackup() }

    /** The toast after the restore on connect (iOS `restoredToast`). */
    private fun restoredToast(result: RestoreResult): String = when {
        result.addresses > 0 && result.portfolioChanged ->
            context.getString(com.kachat.app.R.string.ncw_restored_addresses_and_portfolio_toast, result.addresses)
        result.addresses > 0 ->
            context.getString(com.kachat.app.R.string.ncw_restored_addresses_toast, result.addresses)
        else -> context.getString(com.kachat.app.R.string.ncw_restored_portfolio_toast)
    }

    // -------------------------------------------------------------------------
    // Account removal + the periodic fallback
    // -------------------------------------------------------------------------

    /**
     * Account deletion: drops [walletAddress]'s pending upload and removes the chat-sync keys an
     * older build kept for it - the sibling of [NextcloudService.purgeStoredState], which drops
     * the credentials, toggle, stamp and owed flag themselves.
     */
    fun purgeStoredState(walletAddress: String) {
        if (nextcloudService.activeWalletAddress == walletAddress) {
            debounceJob?.cancel()
            debounceJob = null
        }
        val suffix = NextcloudService.walletHashSuffix(walletAddress)
        scope.launch {
            dataStore.edit { prefs ->
                for (base in LEGACY_KEY_BASES) {
                    prefs.remove(longPreferencesKey("${base}_$suffix"))
                    prefs.remove(booleanPreferencesKey("${base}_$suffix"))
                    prefs.remove(stringPreferencesKey("${base}_$suffix"))
                }
            }
        }
    }

    private fun ensurePeriodicWork() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresBatteryNotLow(true)
            .build()
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            PeriodicWorkRequestBuilder<NextcloudAutoSyncWorker>(PERIODIC_INTERVAL_HOURS, TimeUnit.HOURS)
                .setConstraints(constraints)
                .build()
        )
    }

    private fun cancelScheduledWork() {
        WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
    }
}
