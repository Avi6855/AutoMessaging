package com.avinashpatil.app.automessage.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.WorkManager
import java.util.Calendar

class AutoReplyHistoryClearWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            // Clear all auto-reply logs at midnight
            val db = com.avinashpatil.app.automessage.data.database.AutoMessageDatabase.getInstance(applicationContext)
            db.autoReplyLogDao().deleteAllLogs()
            
            // Record last clear time for UI status display and set pending notification flag
            val sharedPrefs = applicationContext.getSharedPreferences("auto_reply_prefs", Context.MODE_PRIVATE)
            sharedPrefs.edit()
                .putLong("last_history_clear_time", System.currentTimeMillis())
                .putBoolean("pending_auto_clear_toast", true)
                .apply()
            
            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }

    companion object {
        const val WORK_NAME = "auto_reply_history_clear"
        
        fun scheduleDailyClear(context: Context) {
            val workRequest = androidx.work.PeriodicWorkRequestBuilder<AutoReplyHistoryClearWorker>(
                24, java.util.concurrent.TimeUnit.HOURS
            )
                .setInitialDelay(calculateInitialDelay(), java.util.concurrent.TimeUnit.MILLISECONDS)
                .build()
            
            androidx.work.WorkManager.getInstance(context)
                .enqueueUniquePeriodicWork(
                    WORK_NAME,
                    androidx.work.ExistingPeriodicWorkPolicy.KEEP,
                    workRequest
                )
        }
        
        private fun calculateInitialDelay(): Long {
            val now = Calendar.getInstance()
            val midnight = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
                add(Calendar.DAY_OF_MONTH, 1) // Next midnight
            }
            
            return midnight.timeInMillis - now.timeInMillis
        }
        
        fun getLastClearTime(context: Context): Long {
            val sharedPrefs = context.getSharedPreferences("auto_reply_prefs", Context.MODE_PRIVATE)
            return sharedPrefs.getLong("last_history_clear_time", 0)
        }

        fun shouldShowToast(context: Context): Boolean {
            val sharedPrefs = context.getSharedPreferences("auto_reply_prefs", Context.MODE_PRIVATE)
            return sharedPrefs.getBoolean("pending_auto_clear_toast", false)
        }

        fun markToastNotified(context: Context) {
            val sharedPrefs = context.getSharedPreferences("auto_reply_prefs", Context.MODE_PRIVATE)
            sharedPrefs.edit().putBoolean("pending_auto_clear_toast", false).apply()
        }
    }
}