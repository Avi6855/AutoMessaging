package com.avinashpatil.app.automessage.service

import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.avinashpatil.app.automessage.utils.AutoMessagingStateChecker

class CallLogPollerWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    companion object {
        private const val TAG = "CallLogPollerWorker"
    }

    override suspend fun doWork(): Result {
        return try {
            val enabled = AutoMessagingStateChecker.isAutoMessagingEnabled(applicationContext)
            Log.d(TAG, "AUTO_MSG: poller running, autoMessaging=$enabled")

            if (!enabled) {
                Log.d(TAG, "AUTO_MSG: poller skipped because automation disabled")
                return Result.success()
            }

            // Do NOT start FGS from Worker — causes ForegroundServiceDidNotStopInTimeException and Time limit exhausted.
            // Worker itself can do lightweight poll if needed; otherwise just keep WorkManager alive.
            // Optionally set foreground for Worker itself if needed:
            // setForeground(createForegroundInfo())  // requires shortService type if used
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}
