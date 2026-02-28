package com.avinashpatil.app.automessage.service

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

/**
 * Periodic worker used as OEM fallback to keep the CallDetectionService alive.
 * It starts the foreground service and lets the service handle its 1-minute poller.
 */
class CallLogPollerWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        return try {
            val intent = Intent(applicationContext, CallDetectionService::class.java)
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    applicationContext.startForegroundService(intent)
                } else {
                    applicationContext.startService(intent)
                }
            } catch (_: Exception) {}
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}