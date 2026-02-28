package com.avinashpatil.app.automessage.service

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

class DailyResetWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            com.avinashpatil.app.automessage.utils.DailyMessageTracker.clearAllHistory(applicationContext)
            android.util.Log.d("DailyResetWorker", "Cleared DailyMessageTracker history")
            Result.success()
        } catch (e: Exception) {
            android.util.Log.e("DailyResetWorker", "Failed to clear daily message tracker", e)
            Result.retry()
        }
    }
}