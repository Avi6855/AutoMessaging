package com.avinashpatil.app.automessage.utils

import android.content.Context
import android.content.SharedPreferences

object DuplicatePreventer {
    private const val PREFS_NAME = "auto_message_prefs"
    private const val KEY_LAST_CALL_ID = "last_call_id"
    private const val KEY_LAST_SENT_PREFIX = "last_sent_" // per-phone timestamp

    private fun prefs(ctx: Context): SharedPreferences =
        ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun getLastProcessedCallId(ctx: Context): String? =
        prefs(ctx).getString(KEY_LAST_CALL_ID, null)

    fun getLastSentTimestamp(ctx: Context, phoneNumber: String): Long =
        prefs(ctx).getLong(KEY_LAST_SENT_PREFIX + phoneNumber, 0L)

    fun hasSentRecently(ctx: Context, phoneNumber: String, windowMs: Long): Boolean {
        val last = getLastSentTimestamp(ctx, phoneNumber)
        if (last <= 0L) return false
        return (System.currentTimeMillis() - last) < windowMs
    }

    fun shouldProcess(ctx: Context, callId: String, phoneNumber: String, windowMs: Long): Boolean {
        val lastId = getLastProcessedCallId(ctx)
        if (lastId == callId) return false
        if (hasSentRecently(ctx, phoneNumber, windowMs)) return false
        return true
    }

    fun markProcessed(ctx: Context, callId: String, phoneNumber: String) {
        prefs(ctx).edit()
            .putString(KEY_LAST_CALL_ID, callId)
            .putLong(KEY_LAST_SENT_PREFIX + phoneNumber, System.currentTimeMillis())
            .apply()
    }

    fun markSent(ctx: Context, phoneNumber: String) {
        prefs(ctx).edit()
            .putLong(KEY_LAST_SENT_PREFIX + phoneNumber, System.currentTimeMillis())
            .apply()
    }
}