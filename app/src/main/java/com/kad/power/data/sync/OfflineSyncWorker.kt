package com.kad.power.data.sync

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.kad.power.domain.repository.SolarRepository

class OfflineSyncWorker(
    appContext: Context,
    workerParams: WorkerParameters,
    private val repository: SolarRepository
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val syncResult = repository.syncPendingInquiries()
            if (syncResult.isSuccess) {
                Result.success()
            } else {
                Result.retry()
            }
        } catch (e: Exception) {
            Result.failure()
        }
    }
}
