package com.avinashpatil.app.automessage.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.avinashpatil.app.automessage.data.database.AutoMessageDatabase
import com.avinashpatil.app.automessage.utils.DailyHistoryClearScheduler
import com.avinashpatil.app.automessage.utils.DailyMessageTracker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class DailyHistoryClearReceiver : BroadcastReceiver() {
    companion object {
        private const val TAG = "DailyHistoryClearReceiver"
    }

    override fun onReceive(context: Context, intent: Intent?) {
        val action = intent?.action
        Log.d(TAG, "onReceive action=$action at ${System.currentTimeMillis()}")

        // Reschedule next day's alarm immediately (exact alarms are one-shot)
        // Do this before clearing so next alarm is not lost even if clear fails
        DailyHistoryClearScheduler.scheduleDailyClear(context)

        // Perform clear off main thread but goAsync to keep receiver alive
        val pendingResult = goAsync()
        CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
            try {
                clearAllStores(context)
                Log.d(TAG, "All history stores cleared at 00:01 IST")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to clear history stores", e)
            } finally {
                pendingResult.finish()
            }
        }
    }

    private suspend fun clearAllStores(context: Context) {
        // 1. Clear auto_reply_logs DB (shown in Auto Messages tab)
        try {
            val db = AutoMessageDatabase.getInstance(context)
            db.autoReplyLogDao().deleteAllLogs()
            Log.d(TAG, "Cleared auto_reply_logs")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to clear auto_reply_logs", e)
        }

        // 2. Clear DailyMessageTracker shared prefs (blocks next-day sends)
        try {
            DailyMessageTracker.clearAllHistory(context)
            Log.d(TAG, "Cleared DailyMessageTracker")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to clear DailyMessageTracker", e)
        }

        // 3. Clear last_seen_calls (dedup)
        try {
            val db = AutoMessageDatabase.getInstance(context)
            db.lastSeenCallDao().deleteAllLastSeenCalls()
            Log.d(TAG, "Cleared last_seen_calls")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to clear last_seen_calls", e)
        }

        // 4. Clear DuplicatePreventer prefs (last_call_id, last_sent_*)
        try {
            val prefs = context.getSharedPreferences("auto_message_prefs", Context.MODE_PRIVATE)
            prefs.edit().clear().apply()
            Log.d(TAG, "Cleared auto_message_prefs (DuplicatePreventer)")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to clear auto_message_prefs", e)
        }

        // 5. Record last clear time and toast flag for UI
        try {
            val sharedPrefs = context.getSharedPreferences("auto_reply_prefs", Context.MODE_PRIVATE)
            sharedPrefs.edit()
                .putLong("last_history_clear_time", System.currentTimeMillis())
                .putBoolean("pending_auto_clear_toast", true)
                .apply()
            Log.d(TAG, "Updated last_history_clear_time")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to update prefs", e)
        }
    }
}
