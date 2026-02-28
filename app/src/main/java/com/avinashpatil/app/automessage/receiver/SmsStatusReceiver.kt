package com.avinashpatil.app.automessage.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import kotlinx.coroutines.flow.firstOrNull
import com.avinashpatil.app.automessage.AutoMessageApplication
import com.avinashpatil.app.automessage.R
import com.avinashpatil.app.automessage.data.database.AutoMessageDatabase

class SmsStatusReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val action = intent?.action ?: return
        val logId = intent.getLongExtra("log_id", -1L)
        if (logId <= 0) return
        val now = System.currentTimeMillis()

        try {
            val db = AutoMessageDatabase.getInstance(context)
            val dao = db.autoReplyLogDao()

            when (action) {
                "com.avinashpatil.app.automessage.SMS_SENT" -> {
                    val attempts = intent.getIntExtra("attempts", 1)
                    kotlinx.coroutines.runBlocking {
                        dao.markLogSent(id = logId, attempts = attempts, error = null, sentTs = now)
                        dao.convertAllToDelivered(now)
                    }
                    notify(context, title = "Auto Messaging", text = "Message sent ✅", notificationId = 3001)
                }
                "com.avinashpatil.app.automessage.SMS_DELIVERED" -> {
                    kotlinx.coroutines.runBlocking {
                        dao.markLogDelivered(id = logId, deliveredTs = now)
                    }
                    notify(context, title = "Auto Messaging", text = "Message delivered ✅", notificationId = 3002)
                }
            }
        } catch (_: Exception) { }
    }

    private fun notify(context: Context, title: String, text: String, notificationId: Int) {
        val soundEnabled = try {
            val dataStore = PreferenceDataStoreFactory.create(produceFile = { context.preferencesDataStoreFile("auto_message_preferences") })
            val KEY = booleanPreferencesKey("notification_sound_enabled")
            var enabled = true
            // read once synchronously is not supported; best effort by using runBlocking
            kotlinx.coroutines.runBlocking {
                val prefs = dataStore.data.firstOrNull()
                enabled = prefs?.get(KEY) ?: true
            }
            enabled
        } catch (_: Exception) { true }

        val builder = NotificationCompat.Builder(context, AutoMessageApplication.CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setLargeIcon(BitmapFactory.decodeResource(context.resources, R.drawable.logo))
            .setContentTitle(title)
            .setContentText(text)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setAutoCancel(true)
            .setSilent(!soundEnabled)
            .apply {
                if (soundEnabled) setDefaults(NotificationCompat.DEFAULT_ALL) else setDefaults(0)
            }

        try {
            NotificationManagerCompat.from(context).notify(notificationId, builder.build())
        } catch (_: Exception) { }
    }
}
