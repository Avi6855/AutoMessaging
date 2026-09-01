package com.avinashpatil.app.automessage.utils

import android.content.Context
import android.content.SharedPreferences

object DuplicatePreventer {
    private const val PREFS_NAME = "auto_message_prefs"
    private const val KEY_LAST_CALL_IDS = "last_call_ids"
    private const val KEY_LAST_SENT_PREFIX = "last_sent_"

    private fun prefs(ctx: Context): SharedPreferences =
        ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun getLastProcessedCallIds(ctx: Context): Set<String> {
        val raw = prefs(ctx).getString(KEY_LAST_CALL_IDS, null) ?: return emptySet()
        return raw.split(",").filter { it.isNotBlank() }.toSet()
    }

    fun getLastProcessedCallId(ctx: Context): String? =
        getLastProcessedCallIds(ctx).lastOrNull()

    fun getLastSentTimestamp(ctx: Context, phoneNumber: String): Long =
        prefs(ctx).getLong(KEY_LAST_SENT_PREFIX + phoneNumber, 0L)

    fun hasSentRecently(ctx: Context, phoneNumber: String, windowMs: Long): Boolean {
        val last = getLastSentTimestamp(ctx, phoneNumber)
        if (last <= 0L) return false
        return (System.currentTimeMillis() - last) < windowMs
    }

    fun shouldProcess(ctx: Context, callId: String, phoneNumber: String, windowMs: Long): Boolean {
        val lastIds = getLastProcessedCallIds(ctx)
        if (lastIds.contains(callId)) return false
        if (hasSentRecently(ctx, phoneNumber, windowMs)) return false
        return true
    }

    fun markProcessed(ctx: Context, callId: String, phoneNumber: String) {
        val ids = getLastProcessedCallIds(ctx).toMutableList()
        ids.add(callId)
        if (ids.size > 20) ids.removeFirst()
        prefs(ctx).edit()
            .putString(KEY_LAST_CALL_IDS, ids.joinToString(","))
            .putLong(KEY_LAST_SENT_PREFIX + phoneNumber, System.currentTimeMillis())
            .apply()
    }

    fun markSent(ctx: Context, phoneNumber: String) {
        prefs(ctx).edit()
            .putLong(KEY_LAST_SENT_PREFIX + phoneNumber, System.currentTimeMillis())
            .apply()
    }
}
