package com.kachat.app.services

import android.content.Context
import android.net.Uri
import android.util.Log
import android.util.Xml
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.FormBody
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONObject
import org.xmlpull.v1.XmlPullParser
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/** A connected Nextcloud account (server + login + app password). */
data class NextcloudAccount(
    val server: String,
    val username: String,
    val appPassword: String,
    /** Where KaChat's "Send from Nextcloud" chat picker starts browsing - null means the files
     *  root. The wallet has no setting for it (iOS wallet 301ebfb); kept for the dormant chat code. */
    val startFolder: String? = null,
) {
    val displayName: String
        get() {
            val host = server.toHttpUrlOrNull()?.host ?: server
            return "$username@$host"
        }
}

/** One entry from a WebDAV folder listing. [path] is relative to the user's files root, e.g. "Photos/cat.jpg". */
data class NextcloudFile(
    val path: String,
    val name: String,
    val isDirectory: Boolean,
    val contentType: String?,
    val size: Long?,
    val modifiedMs: Long?
) {
    /** Content-Type first, file extension as fallback — servers without a mimetype mapping for
     *  HEIC/MOV and friends report `application/octet-stream`, which would otherwise hide real
     *  media from the picker entirely. */
    val isImage: Boolean
        get() = contentType?.startsWith("image/") == true || extension in IMAGE_EXTENSIONS

    val isVideo: Boolean
        get() = contentType?.startsWith("video/") == true || extension in VIDEO_EXTENSIONS

    private val extension: String get() = path.substringAfterLast('.', "").lowercase()

    private companion object {
        val IMAGE_EXTENSIONS = setOf("jpg", "jpeg", "png", "gif", "webp", "avif", "heic", "heif", "bmp", "tiff")
        val VIDEO_EXTENSIONS = setOf("mov", "mp4", "m4v", "webm", "mkv", "avi")
    }
}

/**
 * The one ordering every Nextcloud listing surface uses: phone-gallery order.
 *
 * Folders stay grouped ahead of files (so a folder never lands in the middle of the thumbnail
 * grid), and within each group entries run newest-first by `getlastmodified`. Entries whose date
 * the server omitted or that failed to parse sort last rather than interleaving randomly, and name
 * is the tiebreak so equal timestamps stay deterministic.
 *
 * Applied once in [NextcloudService.listFolder]; screens only filter, never re-sort.
 */
private val NEWEST_FIRST: Comparator<NextcloudFile> =
    compareByDescending<NextcloudFile> { it.isDirectory }
        .thenByDescending { it.modifiedMs ?: Long.MIN_VALUE }
        .thenBy { it.name.lowercase() }

fun List<NextcloudFile>.sortedNewestFirst(): List<NextcloudFile> = sortedWith(NEWEST_FIRST)

/** URL + Authorization header value for a server-generated thumbnail, ready to hand to Coil. */
data class NextcloudThumbnailRequest(val url: String, val authorization: String)

/**
 * What went wrong talking to Nextcloud, in words the person can act on (iOS `NextcloudError`).
 * Deliberately NOT an IOException: [com.kachat.app.util.UserFacingError] shows an app-authored
 * exception's own message, where an IOException would read as a generic network error - and
 * "Nextcloud rejected the username or app password." is exactly what the reader needs to see.
 */
open class NextcloudException(message: String) : Exception(message)

/** A non-2xx answer (iOS `NextcloudError.httpError`): [code] lets a caller tell a 404 apart. */
class NextcloudHttpException(val code: Int) : NextcloudException("Nextcloud returned HTTP $code.")

/**
 * The user's own Nextcloud server, for one thing: backing up the Address Book and the Portfolio
 * to the KaChat folder (iOS wallet `NextcloudService`, 301ebfb + 59593af + 5bab351). Connect with
 * an app password; the backup is two plain JSON files there:
 * - [ADDRESS_BOOK_FILE_NAME]: the Address Book export file ([AddressBookManager.exportData], with
 *   photos), so any KaChat app's Address Book import can read it.
 * - [PORTFOLIO_FILE_NAME]: every portfolio with its transactions and fees, plus what was deleted
 *   ([PortfolioBackupFile]), so two devices merge without bringing a deletion back.
 * KaChat's chat backup (`kachat-backup.json`) may share the folder; the wallet never reads,
 * writes or deletes it. A restore falls back to the pre-rename [LEGACY_ADDRESS_BOOK_FILE_NAME] /
 * [LEGACY_PORTFOLIO_FILE_NAME]. When and what to upload - Automatic Backup, Back Up Now, Restore,
 * the restore on a fresh connection - is [NextcloudSyncService]'s; this class holds the account,
 * the per-wallet settings and the WebDAV calls. Import / Export in the Address Book and the
 * Portfolio also reach the KaChat folder ([uploadToKaChatFolder], [downloadFile], [listFolder]).
 *
 * Credentials live in their own `EncryptedSharedPreferences` file (same secure-prefs pattern as
 * [ColdStorageManager]). Everything is scoped to the ACTIVE WALLET ACCOUNT, matching iOS: every
 * stored key (credentials, toggle, last-backup stamp, pending flag) carries the active wallet's
 * 8-byte SHA256 hash suffix (iOS's `KeychainService.walletHashSuffix`), and the service follows
 * [WalletManager.activeAddressFlow] so an account switch/logout/delete swaps the whole state and
 * one account's login never leaks into another.
 *
 * The chat-only parts KaChat used (media share links, the chat archive) stay so the dormant chat
 * code still compiles, but nothing in the wallet's Nextcloud flow calls them.
 */
@Singleton
class NextcloudService @Inject constructor(
    @ApplicationContext private val context: Context,
    private val walletManager: WalletManager
) {
    companion object {
        private const val TAG = "NextcloudService"

        private const val SECURE_PREFS_NAME = "nextcloud_secure_prefs"
        // Base names only: the active wallet's hash suffix is appended via `scopedKey`. The bare
        // names are the LEGACY pre-per-account entries, read once by the migration then deleted.
        private const val PREF_SERVER = "server"
        private const val PREF_USERNAME = "username"
        private const val PREF_APP_PASSWORD = "app_password"
        private const val PREF_START_FOLDER = "start_folder"
        /** Settings > Storage > Nextcloud > "Automatic Backup", per wallet. Default off: a
         *  connection is not a request to start uploading (iOS `kachat_nextcloud_auto_backup`). */
        private const val PREF_WALLET_AUTO_BACKUP = "wallet_auto_backup"
        /** When this wallet's backup last uploaded (epoch ms). */
        private const val PREF_WALLET_LAST_BACKUP_MS = "wallet_last_backup_ms"
        /** The Address Book or Portfolio changed since the last successful upload. Persisted, so
         *  a kill before the upload still backs up on the next background or launch. */
        private const val PREF_WALLET_PENDING_BACKUP = "wallet_pending_backup"

        // KaChat's chat-sync keys, never read any more: kept so a disconnect or account removal
        // still clears a value an older build stored (iOS keeps KaChat's in `storedKeys` too).
        private const val PREF_BACKUP_FOLDER = "backup_folder"
        private const val PREF_AUTO_BACKUP_ENABLED = "auto_backup_enabled"
        private const val PREF_LAST_AUTO_BACKUP_MS = "last_auto_backup_ms"
        private const val PREF_MEDIA_SEND_ENABLED = "media_send_enabled"
        private const val PREF_TALK_CALLS = "talk_calls_available"

        /** Every per-wallet key base — the unit `disconnect`/`purgeStoredState`/migration act on. */
        private val ALL_PREF_BASES = listOf(
            PREF_SERVER, PREF_USERNAME, PREF_APP_PASSWORD, PREF_START_FOLDER,
            PREF_WALLET_AUTO_BACKUP, PREF_WALLET_LAST_BACKUP_MS, PREF_WALLET_PENDING_BACKUP,
            PREF_BACKUP_FOLDER, PREF_AUTO_BACKUP_ENABLED, PREF_LAST_AUTO_BACKUP_MS,
            PREF_MEDIA_SEND_ENABLED, PREF_TALK_CALLS
        )

        /** First 8 bytes of SHA256(walletAddress) as hex — byte-identical to iOS's
         *  `KeychainService.walletHashSuffix` and desktop's per-account scheme. */
        fun walletHashSuffix(walletAddress: String): String =
            java.security.MessageDigest.getInstance("SHA-256")
                .digest(walletAddress.toByteArray(Charsets.UTF_8))
                .take(8)
                .joinToString("") { "%02x".format(it) }

        /** The KaChat folder at the files root: the backup, and Address Book / Portfolio exports. */
        const val BACKUP_FOLDER_NAME = "KaChat"
        /** Where KaChat's "Send Media via Nextcloud" uploads land (the dormant chat code only). */
        const val MEDIA_FOLDER_PATH = "$BACKUP_FOLDER_NAME/Media"

        /** The wallet's own backup files (iOS 5bab351). */
        const val ADDRESS_BOOK_FILE_NAME = "KaPay Address Book.json"
        const val PORTFOLIO_FILE_NAME = "KaPay Portfolio.json"
        /** The names the backup had while the app was called KaChat Wallet (until 2026-10-27).
         *  Read when there is no backup under the new name yet; the next backup writes the new one. */
        const val LEGACY_ADDRESS_BOOK_FILE_NAME = "KaChat Wallet Address Book.json"
        const val LEGACY_PORTFOLIO_FILE_NAME = "KaChat Wallet Portfolio.json"

        /** A backup file is read whole; this bounds it (iOS `maxBytes: 50_000_000`). */
        const val BACKUP_MAX_BYTES = 50_000_000L

        /** Normalizes user input ("mycloud.duckdns.org", trailing slashes, an accidental
         *  "/index.php" suffix) into a clean base URL, defaulting to https. Null if it doesn't
         *  parse as a URL at all, or if it explicitly asks for http:// — credentials must never
         *  travel unencrypted, so plaintext HTTP is rejected outright (the settings form shows
         *  the same rule inline before this is ever reached). */
        fun normalizeServer(input: String): String? {
            var raw = input.trim()
            if (raw.isEmpty()) return null
            if (raw.lowercase().startsWith("http://")) return null
            if (!raw.lowercase().startsWith("https://")) {
                raw = "https://$raw"
            }
            raw = raw.trimEnd('/')
            if (raw.lowercase().endsWith("/index.php")) raw = raw.dropLast("/index.php".length)
            val url = raw.toHttpUrlOrNull() ?: return null
            return if (url.host.isEmpty()) null else raw
        }

        /**
         * KaChat's chat-archive verify step (NEXTCLOUD_SYNC.md §4.6, iOS d57019a): true when an
         * upload must be reported as cut off. The wallet's backup never calls it (its two files
         * are small and replaced whole); kept, pure, for NextcloudServiceTest.
         */
        internal fun isUploadCutOff(sentBytes: Long, storedBytes: Long?, putEtag: String?, currentEtag: String?): Boolean {
            if (storedBytes == null || storedBytes == sentBytes) return false
            return putEtag == null || currentEtag == putEtag
        }

        /** WebDAV's getlastmodified is RFC 1123 ("Mon, 11 Aug 2026 20:14:07 GMT"). */
        private fun rfc1123Formatter() = SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.US).apply {
            timeZone = TimeZone.getTimeZone("GMT")
        }

        private const val BAD_CREDENTIALS = "Nextcloud rejected the username or app password."
        private const val MALFORMED = "Unexpected response from the Nextcloud server."
    }

    // The same encrypted file as always. Built at startup (NextcloudSyncService): where the
    // Keystore refuses to open it (seen on hardened ROMs), Nextcloud shows as not connected for
    // this session and the file - the saved account included - is left as it is for the next
    // launch, rather than the app failing to open.
    private val prefs = com.kachat.app.util.StartupSafety.encryptedPrefsOrInMemory(
        context,
        SECURE_PREFS_NAME,
        "NextcloudService"
    )

    // A longer read timeout than the default: Nextcloud can sit silent for a while storing a
    // backup with many photos in it.
    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(120, TimeUnit.SECONDS)
        .writeTimeout(120, TimeUnit.SECONDS)
        .build()

    private val _account = MutableStateFlow<NextcloudAccount?>(null)
    val account: StateFlow<NextcloudAccount?> = _account.asStateFlow()

    /** Settings > Storage > Nextcloud > "Automatic Backup", per wallet account. Default off. */
    private val _autoBackupEnabled = MutableStateFlow(false)
    val autoBackupEnabled: StateFlow<Boolean> = _autoBackupEnabled.asStateFlow()

    /** When the active wallet's backup last uploaded (epoch ms), null = never. */
    private val _lastBackupMs = MutableStateFlow<Long?>(null)
    val lastBackupMs: StateFlow<Long?> = _lastBackupMs.asStateFlow()

    /** KaChat's Nextcloud Talk calls. The wallet has no calls (iOS wallet a11a6d7, Android's
     *  CallService is gone), so the server is never asked; still read by the diagnostics export. */
    private val _talkCallsAvailable = MutableStateFlow(false)
    val talkCallsAvailable: StateFlow<Boolean> = _talkCallsAvailable.asStateFlow()
    private val _talkAvailabilityReason = MutableStateFlow("not used: KaPay has no calls")
    val talkAvailabilityReason: StateFlow<String> = _talkAvailabilityReason.asStateFlow()

    /** The active wallet's address — every credential/settings read and write is scoped to it.
     *  Null (signed out / no wallet yet / testnet) presents as disconnected and persists nothing. */
    @Volatile
    private var currentWalletAddress: String? = null

    /** Cached per-wallet suffix (8-byte SHA256 hex, see [walletHashSuffix]) for the pref keys. */
    @Volatile
    private var currentSuffix: String? = null

    private val serviceScope = CoroutineScope(
        SupervisorJob() + Dispatchers.Main.immediate + com.kachat.app.util.StartupSafety.logOnlyHandler("NextcloudService")
    )

    init {
        // Load the current wallet's state synchronously (the settings screens may compose before
        // the collector's first dispatch), then follow every switch/logout/delete after that.
        setCurrentWallet(walletManager.activeAddressFlow.value, force = true)
        serviceScope.launch {
            walletManager.activeAddressFlow.collect { setCurrentWallet(it) }
        }
    }

    val isConnected: Boolean get() = _account.value != null

    /** The wallet this service is scoped to (null signed out or on testnet) - what a backup or
     *  restore snapshots at its start and re-checks before every write. */
    val activeWalletAddress: String? get() = currentWalletAddress

    /**
     * Off for the whole testnet launch (iOS af8ec68): the backup files are one per account across
     * both networks, so testnet could otherwise write testnet data over the mainnet backup. With
     * no account nothing backs up or restores, and every Nextcloud option hides as if
     * disconnected. The mainnet login is untouched and comes back on the next mainnet launch.
     */
    val isOffForTestnet: Boolean get() = com.kachat.app.util.KaspaNetwork.isTestnet

    /** Where the backup and the Address Book / Portfolio exports go: always the KaChat folder at
     *  the files root (iOS `backupFolderName`; there is no folder choice in the wallet). */
    val backupFolderPath: String get() = BACKUP_FOLDER_NAME

    // -------------------------------------------------------------------------
    // Wallet scoping
    // -------------------------------------------------------------------------

    /** The active wallet's pref key for [base], or null when signed out (in which case nothing
     *  is read or written). */
    private fun scopedKey(base: String): String? = currentSuffix?.let { "${base}_$it" }

    private fun scopedKey(base: String, suffix: String): String = "${base}_$suffix"

    /**
     * Points the service at [requestedAddress]'s stored Nextcloud state, or clears everything for
     * null. Driven by [WalletManager.activeAddressFlow], which fires on every wallet load,
     * account switch, logout and delete ([NextcloudSyncService] cancels any pending backup when
     * the wallet changes).
     */
    private fun setCurrentWallet(requestedAddress: String?, force: Boolean = false) {
        val walletAddress = if (isOffForTestnet) null else requestedAddress
        if (!force && walletAddress == currentWalletAddress) return

        currentWalletAddress = walletAddress
        currentSuffix = walletAddress?.let { walletHashSuffix(it) }

        if (walletAddress == null) {
            _account.value = null
            _autoBackupEnabled.value = false
            _lastBackupMs.value = null
            return
        }

        migrateLegacyGlobalStateIfNeeded()

        _account.value = loadAccount()
        _autoBackupEnabled.value = _account.value != null &&
            (scopedKey(PREF_WALLET_AUTO_BACKUP)?.let { prefs.getBoolean(it, false) } ?: false)
        _lastBackupMs.value = scopedKey(PREF_WALLET_LAST_BACKUP_MS)?.let { prefs.getLong(it, 0L) }?.takeIf { it > 0 }
    }

    /**
     * One-time migration off the pre-per-wallet storage: the single global credential entry
     * moves to the active wallet's scoped keys — the account that was actually using the login
     * keeps it — then the global entries are deleted so no other account ever sees them again.
     */
    private fun migrateLegacyGlobalStateIfNeeded() {
        val suffix = currentSuffix ?: return
        val legacyServer = prefs.getString(PREF_SERVER, null)
        val legacyUsername = prefs.getString(PREF_USERNAME, null)
        val legacyPassword = prefs.getString(PREF_APP_PASSWORD, null)
        val anyLegacy = ALL_PREF_BASES.any { prefs.contains(it) }
        if (!anyLegacy) return

        val editor = prefs.edit()
        if (legacyServer != null && legacyUsername != null && legacyPassword != null &&
            prefs.getString(scopedKey(PREF_SERVER, suffix), null) == null
        ) {
            editor.putString(scopedKey(PREF_SERVER, suffix), legacyServer)
            editor.putString(scopedKey(PREF_USERNAME, suffix), legacyUsername)
            editor.putString(scopedKey(PREF_APP_PASSWORD, suffix), legacyPassword)
            prefs.getString(PREF_START_FOLDER, null)?.let { editor.putString(scopedKey(PREF_START_FOLDER, suffix), it) }
        }
        for (base in ALL_PREF_BASES) editor.remove(base)
        editor.apply()
        Log.i(TAG, "Migrated the global Nextcloud login to the active wallet's per-account storage")
    }

    /**
     * Deletes a wallet's stored Nextcloud login and settings outright — used when that account
     * is removed from this device entirely. Storage only; if the wallet is still the active one,
     * the in-memory state clears too.
     */
    fun purgeStoredState(walletAddress: String) {
        val suffix = walletHashSuffix(walletAddress)
        val editor = prefs.edit()
        for (base in ALL_PREF_BASES) editor.remove(scopedKey(base, suffix))
        editor.apply()
        if (walletAddress == currentWalletAddress) {
            _account.value = null
            _autoBackupEnabled.value = false
            _lastBackupMs.value = null
        }
    }

    private fun loadAccount(): NextcloudAccount? {
        val server = scopedKey(PREF_SERVER)?.let { prefs.getString(it, null) } ?: return null
        val username = scopedKey(PREF_USERNAME)?.let { prefs.getString(it, null) } ?: return null
        val appPassword = scopedKey(PREF_APP_PASSWORD)?.let { prefs.getString(it, null) } ?: return null
        return NextcloudAccount(
            server = server,
            username = username,
            appPassword = appPassword,
            startFolder = scopedKey(PREF_START_FOLDER)?.let { prefs.getString(it, null) },
        )
    }

    private fun persistAccount(account: NextcloudAccount) {
        val suffix = currentSuffix ?: return
        prefs.edit()
            .putString(scopedKey(PREF_SERVER, suffix), account.server)
            .putString(scopedKey(PREF_USERNAME, suffix), account.username)
            .putString(scopedKey(PREF_APP_PASSWORD, suffix), account.appPassword)
            .apply {
                if (account.startFolder != null) putString(scopedKey(PREF_START_FOLDER, suffix), account.startFolder)
                else remove(scopedKey(PREF_START_FOLDER, suffix))
            }
            .apply()
        _account.value = account
    }

    // -------------------------------------------------------------------------
    // Connection
    // -------------------------------------------------------------------------

    /**
     * Verifies the credentials against the OCS user endpoint (the cheapest authenticated call),
     * then persists them. Throws with a user-facing message — a 401 says exactly what's wrong.
     * The restore a fresh connection runs once by itself is [NextcloudSyncService.connect]'s.
     */
    suspend fun connect(serverInput: String, username: String, appPassword: String) {
        if (isOffForTestnet) throw NextcloudException(context.getString(com.kachat.app.R.string.nextcloud_off_on_testnet))
        if (currentWalletAddress == null) throw NextcloudException("Sign in to an account before connecting Nextcloud.")
        if (serverInput.trim().lowercase().startsWith("http://")) {
            throw NextcloudException("Use https. Unencrypted connections are not supported.")
        }
        val server = normalizeServer(serverInput)
            ?: throw NextcloudException("That doesn't look like a valid server URL.")
        val user = username.trim()
        val password = appPassword.trim()
        if (user.isEmpty() || password.isEmpty()) throw NextcloudException(BAD_CREDENTIALS)
        val candidate = NextcloudAccount(server = server, username = user, appPassword = password)

        withContext(Dispatchers.IO) {
            val request = Request.Builder()
                .url("$server/ocs/v2.php/cloud/user?format=json")
                .header("Authorization", basicAuth(candidate))
                .header("OCS-APIRequest", "true")
                .header("Accept", "application/json")
                .build()
            client.newCall(request).execute().use { response ->
                if (response.code == 401) throw NextcloudException(BAD_CREDENTIALS)
                if (!response.isSuccessful) throw NextcloudHttpException(response.code)
                val body = response.body?.string() ?: throw NextcloudException(MALFORMED)
                // ocs.data existing at all is the success signal; its contents aren't needed.
                runCatching { JSONObject(body).getJSONObject("ocs").getJSONObject("data") }.getOrNull()
                    ?: throw NextcloudException(MALFORMED)
            }
        }
        persistAccount(candidate)
        // A connection is not a request to start uploading: Automatic Backup stays as it was
        // stored (off unless this wallet turned it on before).
        _autoBackupEnabled.value = scopedKey(PREF_WALLET_AUTO_BACKUP)?.let { prefs.getBoolean(it, false) } ?: false
    }

    /**
     * Forgets this wallet's Nextcloud login and settings. The app password the user made for
     * KaPay is revoked on the server first, while the credentials still exist to authenticate the
     * request - best effort, the local copy goes whatever the server says (iOS `disconnect`).
     * The backup itself stays on the server.
     */
    fun disconnect() {
        _account.value?.let { account ->
            serviceScope.launch(Dispatchers.IO) { revokeAppPassword(account) }
        }
        val editor = prefs.edit()
        // Only the active wallet's entries — other accounts' logins stay untouched.
        currentSuffix?.let { suffix ->
            for (base in ALL_PREF_BASES) editor.remove(scopedKey(base, suffix))
        }
        // Belt and braces: if a legacy global entry somehow still exists, remove it too so
        // disconnect can never appear to "come back" via migration.
        for (base in ALL_PREF_BASES) editor.remove(base)
        editor.apply()
        _account.value = null
        _autoBackupEnabled.value = false
        _lastBackupMs.value = null
    }

    /** Revokes [account]'s app password on its server (iOS `revokeAppPassword`), so a
     *  disconnected or deleted wallet leaves nothing that can still log in. Best effort. */
    private fun revokeAppPassword(account: NextcloudAccount) {
        try {
            val request = Request.Builder()
                .url("${account.server.trimEnd('/')}/ocs/v2.php/core/apppassword")
                .delete()
                .header("Authorization", basicAuth(account))
                .header("OCS-APIRequest", "true")
                .build()
            client.newCall(request).execute().use { response ->
                Log.i(TAG, "App password revoke answered HTTP ${response.code}")
            }
        } catch (e: Exception) {
            Log.w(TAG, "App password revoke failed: ${e.message}")
        }
    }

    /** Persists the chat picker's start folder (null/"" = files root). The wallet's settings
     *  never offer it; kept for the dormant chat code. */
    fun setStartFolder(path: String?) {
        val current = _account.value ?: return
        persistAccount(current.copy(startFolder = path?.trim()?.takeIf { it.isNotEmpty() }))
    }

    // -------------------------------------------------------------------------
    // Automatic Backup settings (per wallet; NextcloudSyncService acts on them)
    // -------------------------------------------------------------------------

    /** Stores the Automatic Backup toggle. [NextcloudSyncService.setAutoBackupEnabled] is the
     *  setter the screen uses: it also schedules or cancels the upload. */
    fun setAutoBackupEnabled(enabled: Boolean) {
        val key = scopedKey(PREF_WALLET_AUTO_BACKUP) ?: return
        prefs.edit().putBoolean(key, enabled).apply()
        _autoBackupEnabled.value = enabled
    }

    /** Whether the active wallet owes the server an upload. */
    val isBackupPending: Boolean
        get() = scopedKey(PREF_WALLET_PENDING_BACKUP)?.let { prefs.getBoolean(it, false) } ?: false

    fun setBackupPending(pending: Boolean) {
        val key = scopedKey(PREF_WALLET_PENDING_BACKUP) ?: return
        prefs.edit().putBoolean(key, pending).apply()
    }

    /** A backup finished for the active wallet: nothing is owed, and it is stamped now. */
    fun recordBackupFinished() {
        val now = System.currentTimeMillis()
        val editor = prefs.edit()
        scopedKey(PREF_WALLET_PENDING_BACKUP)?.let { editor.putBoolean(it, false) }
        scopedKey(PREF_WALLET_LAST_BACKUP_MS)?.let { editor.putLong(it, now) }
        editor.apply()
        _lastBackupMs.value = now
    }

    // -------------------------------------------------------------------------
    // The backup files
    // -------------------------------------------------------------------------

    /** The backup files' server-side metadata (a null file = not backed up yet, or unreadable). */
    data class BackupInfo(
        val addressBook: NextcloudFile? = null,
        val portfolio: NextcloudFile? = null,
    ) {
        val isEmpty: Boolean get() = addressBook == null && portfolio == null
    }

    /** What the KaChat folder holds of the backup, the new names first (iOS `fetchBackupInfo`).
     *  A missing folder lists as a 404, which also just means "no backup yet". */
    suspend fun fetchWalletBackupInfo(): BackupInfo {
        val listing = runCatching { listFolder(BACKUP_FOLDER_NAME) }.getOrNull() ?: return BackupInfo()
        fun file(name: String) = listing.firstOrNull { it.name == name && !it.isDirectory }
        return BackupInfo(
            addressBook = file(ADDRESS_BOOK_FILE_NAME) ?: file(LEGACY_ADDRESS_BOOK_FILE_NAME),
            portfolio = file(PORTFOLIO_FILE_NAME) ?: file(LEGACY_PORTFOLIO_FILE_NAME),
        )
    }

    /** A backup file under [name], else under the name it had before the rename ([legacy]); null
     *  when neither is there (iOS `downloadBackupFile`). Any other failure throws. */
    suspend fun downloadBackupFile(name: String, legacy: String): ByteArray? {
        for (candidate in listOf(name, legacy)) {
            try {
                return downloadFile("$BACKUP_FOLDER_NAME/$candidate", maxBytes = BACKUP_MAX_BYTES)
            } catch (e: NextcloudHttpException) {
                if (e.code != 404) throw e
            }
        }
        return null
    }

    /**
     * Deletes the Address Book and Portfolio backups (both names each) from the user's Nextcloud.
     * Only ever on the user's say-so - the Delete Account option that names it. KaChat's chat
     * backup in the same folder is left alone. A 404 is the state being asked for.
     */
    suspend fun deleteRemoteBackup(): Unit = withContext(Dispatchers.IO) {
        val account = requireAccount()
        for (name in listOf(ADDRESS_BOOK_FILE_NAME, PORTFOLIO_FILE_NAME, LEGACY_ADDRESS_BOOK_FILE_NAME, LEGACY_PORTFOLIO_FILE_NAME)) {
            val request = Request.Builder()
                .url(davUrl(account, "$BACKUP_FOLDER_NAME/$name"))
                .delete()
                .header("Authorization", basicAuth(account))
                .build()
            client.newCall(request).execute().use { response ->
                if (response.code == 401) throw NextcloudException(BAD_CREDENTIALS)
                if (!response.isSuccessful && response.code != 404) throw NextcloudHttpException(response.code)
            }
        }
        Log.i(TAG, "Backup deleted")
    }

    // -------------------------------------------------------------------------
    // KaChat's chat archive: never part of the wallet (iOS wallet 301ebfb)
    // -------------------------------------------------------------------------

    /** KaChat's chat-history backup. The wallet never uploads chats or touches
     *  `kachat-backup.json`; kept only so the dormant chat code compiles. Always refuses. */
    @Suppress("UNUSED_PARAMETER")
    suspend fun runBackup(buildJson: suspend (String?) -> String): String? =
        throw NextcloudException("KaPay backs up only the Address Book and the Portfolio.")

    /** KaChat's chat archive metadata: there is none for the wallet. */
    suspend fun fetchBackupInfo(): NextcloudFile? = null

    /** KaChat's chat archive download: the wallet has none to restore. */
    @Suppress("UNUSED_PARAMETER")
    suspend fun downloadBackup(onProgress: ((receivedBytes: Long, totalBytes: Long?) -> Unit)? = null): String =
        throw NextcloudException("No backup was found in the KaChat folder.")

    // -------------------------------------------------------------------------
    // WebDAV plumbing
    // -------------------------------------------------------------------------

    private fun basicAuth(account: NextcloudAccount): String =
        "Basic " + android.util.Base64.encodeToString(
            "${account.username}:${account.appPassword}".toByteArray(Charsets.UTF_8),
            android.util.Base64.NO_WRAP
        )

    private fun requireAccount(): NextcloudAccount =
        _account.value ?: throw NextcloudException(BAD_CREDENTIALS)

    /** WebDAV URL for a path relative to the user's files root, each segment percent-encoded. */
    private fun davUrl(account: NextcloudAccount, relativePath: String): HttpUrl {
        val base = account.server.toHttpUrlOrNull()
            ?: throw NextcloudException("That doesn't look like a valid server URL.")
        val builder = base.newBuilder()
        for (part in "remote.php/dav/files/${account.username}/$relativePath".split("/")) {
            if (part.isNotEmpty()) builder.addPathSegment(part)
        }
        return builder.build()
    }

    /**
     * Uploads [bytes] as [filename] into the KaChat folder, creating it if it isn't there yet,
     * and returns the stored path. A file of the same name is replaced. [keepSpaces] keeps a name
     * the user gave (a portfolio's, "KaChat Address Book", the backup's "KaPay …") readable in
     * Nextcloud instead of turning its spaces into underscores (iOS 87b2a0b).
     */
    suspend fun uploadToKaChatFolder(
        bytes: ByteArray,
        filename: String,
        contentType: String,
        keepSpaces: Boolean = false,
    ): String = withContext(Dispatchers.IO) {
        val account = requireAccount()
        val folder = BACKUP_FOLDER_NAME
        // Level by level: MKCOL is not recursive, and 405 means the level already exists.
        var level = ""
        for (part in folder.split("/").filter { it.isNotEmpty() }) {
            level = if (level.isEmpty()) part else "$level/$part"
            val mkcol = Request.Builder().url(davUrl(account, level)).method("MKCOL", null)
                .header("Authorization", basicAuth(account)).build()
            client.newCall(mkcol).execute().use { response ->
                if (response.code == 401) throw NextcloudException(BAD_CREDENTIALS)
                if (!response.isSuccessful && response.code != 405) throw NextcloudHttpException(response.code)
            }
        }
        // iOS sanitizedFilename: letters, marks and digits of any script stay (CharacterSet
        // .alphanumerics), plus "._-" and, with keepSpaces, the space.
        val disallowed = if (keepSpaces) Regex("[^\\p{L}\\p{M}\\p{N}._ -]") else Regex("[^\\p{L}\\p{M}\\p{N}._-]")
        val storedName = filename.replace(disallowed, "_").takeIf { it.isNotBlank() } ?: "file"
        val path = "$folder/$storedName"
        val mediaType = contentType.toMediaTypeOrNull() ?: "application/octet-stream".toMediaType()
        val put = Request.Builder().url(davUrl(account, path)).put(bytes.toRequestBody(mediaType))
            .header("Authorization", basicAuth(account)).build()
        client.newCall(put).execute().use { response ->
            if (response.code == 401) throw NextcloudException(BAD_CREDENTIALS)
            if (!response.isSuccessful) throw NextcloudHttpException(response.code)
        }
        path
    }

    /** A file's bytes, for importing it. Every failure throws so the caller can say what went
     *  wrong ([NextcloudHttpException] carries the status); capped at [maxBytes]. */
    suspend fun downloadFile(relativePath: String, maxBytes: Long = 10_000_000L): ByteArray = withContext(Dispatchers.IO) {
        val account = requireAccount()
        val get = Request.Builder().url(davUrl(account, relativePath)).header("Authorization", basicAuth(account)).build()
        client.newCall(get).execute().use { response ->
            if (response.code == 401) throw NextcloudException(BAD_CREDENTIALS)
            if (!response.isSuccessful) throw NextcloudHttpException(response.code)
            val body = response.body ?: throw NextcloudException(MALFORMED)
            if (body.contentLength() > maxBytes) throw NextcloudException("That file is too large to import.")
            val bytes = body.bytes()
            if (bytes.size > maxBytes) throw NextcloudException("That file is too large to import.")
            bytes
        }
    }

    /** Lists one folder (non-recursive) of the connected account's files via a Depth-1 PROPFIND
     *  - the import file pickers' data source. */
    suspend fun listFolder(relativePath: String = ""): List<NextcloudFile> = withContext(Dispatchers.IO) {
        val account = requireAccount()
        val davBasePath = "/remote.php/dav/files/${account.username}"
        // Normalized form of the listed folder, for the parser's self-entry exclusion below.
        val listedPath = relativePath.split("/").filter { it.isNotEmpty() }.joinToString("/")

        val body = """
            <?xml version="1.0"?>
            <d:propfind xmlns:d="DAV:">
              <d:prop><d:displayname/><d:resourcetype/><d:getcontenttype/><d:getcontentlength/><d:getlastmodified/></d:prop>
            </d:propfind>
        """.trimIndent().toRequestBody("application/xml".toMediaType())

        val request = Request.Builder()
            .url(davUrl(account, relativePath))
            .method("PROPFIND", body)
            .header("Depth", "1")
            .header("Authorization", basicAuth(account))
            .build()

        client.newCall(request).execute().use { response ->
            if (response.code == 401) throw NextcloudException(BAD_CREDENTIALS)
            if (response.code != 207) throw NextcloudHttpException(response.code)
            val xml = response.body?.string() ?: throw NextcloudException(MALFORMED)
            parseMultistatus(xml, davBasePath, listedPath)
        }
    }

    /**
     * Minimal WebDAV `multistatus` parser for folder listings. Namespace-aware, matching on local
     * names only (servers vary between `d:` and `D:` prefixes).
     *
     * A Depth-1 PROPFIND's multistatus includes the listed folder ITSELF as one of its responses —
     * without excluding it, every folder appears to contain itself (an infinite "Photos inside
     * Photos" loop when browsing). The exclusion must compare full relative paths, not just check
     * "is this the root": listing "a/b" includes an entry whose path is "a/b" at any depth.
     */
    private fun parseMultistatus(xml: String, davBasePath: String, listedPath: String): List<NextcloudFile> {
        val results = mutableListOf<NextcloudFile>()
        val parser = Xml.newPullParser().apply {
            setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, true)
            setInput(xml.reader())
        }
        val dateFormatter = rfc1123Formatter()

        var inResponse = false
        var href = ""
        var displayName: String? = null
        var contentType: String? = null
        var contentLength: Long? = null
        var modifiedMs: Long? = null
        var isCollection = false
        var text = StringBuilder()

        fun appendCurrent() {
            val decoded = Uri.decode(href)
            val baseIndex = decoded.indexOf(davBasePath)
            if (baseIndex < 0) return
            val relative = decoded.substring(baseIndex + davBasePath.length).trim('/')
            if (relative.isEmpty() || relative == listedPath) return // the listed folder itself
            val fallbackName = relative.substringAfterLast('/')
            results.add(
                NextcloudFile(
                    path = relative,
                    name = displayName ?: fallbackName,
                    isDirectory = isCollection,
                    contentType = contentType,
                    size = contentLength,
                    modifiedMs = modifiedMs
                )
            )
        }

        var event = parser.eventType
        while (event != XmlPullParser.END_DOCUMENT) {
            when (event) {
                XmlPullParser.START_TAG -> {
                    text = StringBuilder()
                    when (parser.name) {
                        "response" -> {
                            inResponse = true
                            href = ""
                            displayName = null
                            contentType = null
                            contentLength = null
                            modifiedMs = null
                            isCollection = false
                        }
                        "collection" -> isCollection = true
                    }
                }
                XmlPullParser.TEXT -> text.append(parser.text)
                XmlPullParser.END_TAG -> {
                    val trimmed = text.toString().trim()
                    when (parser.name) {
                        "href" -> if (inResponse && href.isEmpty()) href = trimmed
                        "displayname" -> if (inResponse && displayName == null && trimmed.isNotEmpty()) displayName = trimmed
                        "getcontenttype" -> if (inResponse && trimmed.isNotEmpty()) contentType = trimmed
                        "getcontentlength" -> if (inResponse) contentLength = trimmed.toLongOrNull()
                        "getlastmodified" -> if (inResponse && trimmed.isNotEmpty()) {
                            modifiedMs = runCatching { dateFormatter.parse(trimmed)?.time }.getOrNull()
                        }
                        "response" -> {
                            inResponse = false
                            appendCurrent()
                        }
                    }
                }
            }
            event = parser.next()
        }
        return results.sortedNewestFirst()
    }

    // -------------------------------------------------------------------------
    // KaChat's chat media (the dormant chat code only: share links, thumbnails)
    // -------------------------------------------------------------------------

    /**
     * Server-generated square thumbnail via Nextcloud's authenticated `core/preview` endpoint
     * (`a=1` keeps aspect by cropping), as a URL + Authorization header for Coil's own loader/cache
     * to fetch.
     */
    fun thumbnailRequest(path: String, size: Int = 256): NextcloudThumbnailRequest? {
        val account = _account.value ?: return null
        val base = account.server.toHttpUrlOrNull() ?: return null
        val url = base.newBuilder()
            .addPathSegments("index.php/core/preview.png")
            .addQueryParameter("file", "/$path")
            .addQueryParameter("x", size.toString())
            .addQueryParameter("y", size.toString())
            .addQueryParameter("a", "1")
            .build()
        return NextcloudThumbnailRequest(url = url.toString(), authorization = basicAuth(account))
    }

    /**
     * Creates a public link share (shareType 3) for [relativePath] and returns its `/s/TOKEN`
     * URL. If the file already has a public link (creating again can fail on some configs), the
     * existing link is reused.
     */
    suspend fun createPublicShareLink(relativePath: String): String = withContext(Dispatchers.IO) {
        val account = requireAccount()
        val request = Request.Builder()
            .url("${account.server}/ocs/v2.php/apps/files_sharing/api/v1/shares?format=json")
            .post(
                FormBody.Builder()
                    .add("path", "/$relativePath")
                    .add("shareType", "3")
                    .build()
            )
            .header("Authorization", basicAuth(account))
            .header("OCS-APIRequest", "true")
            .build()

        client.newCall(request).execute().use { response ->
            if (response.code == 401) throw NextcloudException(BAD_CREDENTIALS)
            if (response.isSuccessful) {
                val body = response.body?.string()
                val url = body?.let { shareUrlFromOcsObject(it) }
                if (url != null) return@withContext url
            }
        }
        existingPublicShareLink(account, relativePath) ?: throw NextcloudException(MALFORMED)
    }

    private fun existingPublicShareLink(account: NextcloudAccount, relativePath: String): String? {
        val base = account.server.toHttpUrlOrNull() ?: return null
        val url = base.newBuilder()
            .addPathSegments("ocs/v2.php/apps/files_sharing/api/v1/shares")
            .addQueryParameter("format", "json")
            .addQueryParameter("path", "/$relativePath")
            .build()
        val request = Request.Builder()
            .url(url)
            .header("Authorization", basicAuth(account))
            .header("OCS-APIRequest", "true")
            .build()
        return try {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) return null
                val body = response.body?.string() ?: return null
                val list = JSONObject(body).getJSONObject("ocs").optJSONArray("data") ?: return null
                for (i in 0 until list.length()) {
                    val share = list.optJSONObject(i) ?: continue
                    if (share.optInt("share_type", -1) == 3) {
                        val shareUrl = share.optString("url").takeIf { it.isNotEmpty() }
                        if (shareUrl != null) return shareUrl
                    }
                }
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    private fun shareUrlFromOcsObject(body: String): String? = runCatching {
        JSONObject(body).getJSONObject("ocs").getJSONObject("data").optString("url").takeIf { it.isNotEmpty() }
    }.getOrNull()

    /** Uploads one media file to `KaChat/Media/` and returns a public share link for it - KaChat's
     *  "send via Nextcloud" (the dormant chat code only). Throws on any failure. */
    suspend fun uploadMediaAndShare(bytes: ByteArray, filename: String, contentType: String): String {
        val mediaType = contentType.toMediaTypeOrNull() ?: "application/octet-stream".toMediaType()
        return uploadMediaAndShare(bytes.toRequestBody(mediaType), filename)
    }

    /** The same upload-and-share, streaming [body] from wherever it lives. */
    suspend fun uploadMediaAndShare(body: okhttp3.RequestBody, filename: String): String {
        val relativePath = withContext(Dispatchers.IO) {
            val account = requireAccount()
            for (folder in listOf(BACKUP_FOLDER_NAME, MEDIA_FOLDER_PATH)) {
                val mkcol = Request.Builder()
                    .url(davUrl(account, folder))
                    .method("MKCOL", null)
                    .header("Authorization", basicAuth(account))
                    .build()
                client.newCall(mkcol).execute().use { response ->
                    if (response.code == 401) throw NextcloudException(BAD_CREDENTIALS)
                    if (!response.isSuccessful && response.code != 405) throw NextcloudHttpException(response.code)
                }
            }
            val sanitized = filename.replace(Regex("[^A-Za-z0-9._-]"), "_").takeIf { it.isNotBlank() } ?: "file"
            val uniqueName = "${java.util.UUID.randomUUID().toString().replace("-", "").take(8)}_$sanitized"
            val path = "$MEDIA_FOLDER_PATH/$uniqueName"
            val put = Request.Builder()
                .url(davUrl(account, path))
                .put(body)
                .header("Authorization", basicAuth(account))
                .build()
            client.newCall(put).execute().use { response ->
                if (response.code == 401) throw NextcloudException(BAD_CREDENTIALS)
                if (!response.isSuccessful) throw NextcloudHttpException(response.code)
            }
            path
        }
        return createPublicShareLink(relativePath)
    }
}
