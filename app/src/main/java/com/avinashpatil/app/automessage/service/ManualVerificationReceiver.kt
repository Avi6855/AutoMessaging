package com.avinashpatil.app.automessage.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build

/**
 * Receives manual trigger broadcasts to run verification immediately and optionally send a message
 * to a specific phone number.
 */
class ManualVerificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val phone = intent.getStringExtra("phone_number")
        val action = intent.action
        try {
            // Start verification service and trigger a one-off verification
            val verifyIntent = Intent(context, CallVerificationService::class.java).apply {
                this.action = "TRIGGER_VERIFICATION"
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(verifyIntent)
            } else {
                context.startService(verifyIntent)
            }

            // If phone provided, ask CallDetectionService to attempt manual send
            if (!phone.isNullOrBlank()) {
                val manualSend = Intent(context, CallDetectionService::class.java).apply {
                    this.action = "MANUAL_SEND"
                    putExtra("phone_number", phone)
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(manualSend)
                } else {
                    context.startService(manualSend)
                }
            }
        } catch (e: Exception) {
            android.util.Log.e("ManualVerificationReceiver", "Failed to trigger manual verification", e)
        }
    }
}