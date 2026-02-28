package com.avinashpatil.app.automessage

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.avinashpatil.app.automessage.service.CallLogPollerWorker
import com.avinashpatil.app.automessage.workers.AutoReplyHistoryClearWorker
import dagger.hilt.android.HiltAndroidApp
import java.util.concurrent.TimeUnit

@HiltAndroidApp
class AutoMessageApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        scheduleAutoReplyHistoryClear()
        scheduleCallLogPoller()
    }
    
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Auto Message Service",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notification channel for Auto Message background service"
                setShowBadge(true)
            }
            
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
    
    private fun scheduleAutoReplyHistoryClear() {
        AutoReplyHistoryClearWorker.scheduleDailyClear(this)
    }
    
    private fun scheduleCallLogPoller() {
        val constraints = Constraints.Builder().build()
        val request = PeriodicWorkRequestBuilder<CallLogPollerWorker>(15, TimeUnit.MINUTES)
            .setConstraints(constraints)
            .addTag("CallLogPollerWork")
            .build()
        
        WorkManager.getInstance(this)
            .enqueueUniquePeriodicWork(
                "CallLogPollerWork",
                ExistingPeriodicWorkPolicy.KEEP,
                request
            )
    }
    
    companion object {
        const val CHANNEL_ID = "auto_message_channel"
        const val CHANNEL_NAME = "Auto Message"
    }
}
