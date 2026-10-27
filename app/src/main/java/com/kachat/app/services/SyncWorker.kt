package com.kachat.app.services

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

/**
 * Periodic background refresh (~15 minutes, WorkManager): refreshes the balance and notifies any
 * receive to the wallet's own addresses that landed while the app was closed (iOS wallet
 * 7f789f4, BackgroundTaskManager). The group catch-up it once ran went with the chats (iOS
 * wallet 301ebfb).
 */
@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val walletService: WalletService,
    private val addressActivityNotifier: AddressActivityNotifier,
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            walletService.refreshBalance()
            walletService.refreshSpendingBalance()
            addressActivityNotifier.runPassNow()
            Result.success()
        } catch (e: Exception) {
            Log.w("SyncWorker", "Periodic sync failed, will retry next cycle", e)
            Result.retry()
        }
    }

    companion object {
        const val WORK_NAME = "kachat_periodic_sync"
    }
}
