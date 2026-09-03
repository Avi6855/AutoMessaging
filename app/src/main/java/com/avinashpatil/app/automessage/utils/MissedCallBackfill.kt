package com.avinashpatil.app.automessage.utils

import android.content.Context
import android.provider.CallLog

/**
 * Shared query for missed-call backfill.
 * Returns answered (INCOMING/OUTGOING, duration > 0) calls within [windowMs],
 * oldest-first, capped at [maxResults]. Used by CallDetectionService startup
 * backfill and CallLogPollerWorker so calls missed while the FGS was dead
 * (night/Doze/background-start block) still get replies.
 */
object MissedCallBackfill {
    const val BACKFILL_WINDOW_MS: Long = 12 * 60 * 60_000L
    const val MAX_PER_RUN: Int = 10

    data class AnsweredCall(
        val id: Long,
        val number: String,
        val type: Int,
        val date: Long,
        val durationSec: Int
    )

    fun getAnsweredCallsSince(
        context: Context,
        sinceMs: Long,
        limit: Int = 50
    ): List<AnsweredCall> {
        val out = mutableListOf<AnsweredCall>()
        try {
            val uri = CallLog.Calls.CONTENT_URI
            val projection = arrayOf(
                CallLog.Calls._ID,
                CallLog.Calls.NUMBER,
                CallLog.Calls.TYPE,
                CallLog.Calls.DATE,
                CallLog.Calls.DURATION
            )
            val sortOrder = CallLog.Calls.DATE + " DESC"
            val cursor = context.contentResolver.query(uri, projection, null, null, sortOrder)
            cursor?.use { c ->
                val idIdx = c.getColumnIndexOrThrow(CallLog.Calls._ID)
                val numIdx = c.getColumnIndexOrThrow(CallLog.Calls.NUMBER)
                val typeIdx = c.getColumnIndexOrThrow(CallLog.Calls.TYPE)
                val dateIdx = c.getColumnIndexOrThrow(CallLog.Calls.DATE)
                val durIdx = c.getColumnIndexOrThrow(CallLog.Calls.DURATION)
                var checked = 0
                while (c.moveToNext() && checked < limit) {
                    checked++
                    val date = c.getLong(dateIdx)
                    if (date < sinceMs) break // DESC order: older than window, stop
                    val type = c.getInt(typeIdx)
                    val dur = c.getInt(durIdx)
                    if ((type == CallLog.Calls.INCOMING_TYPE || type == CallLog.Calls.OUTGOING_TYPE) && dur > 0) {
                        val num = c.getString(numIdx) ?: ""
                        if (num.isNotBlank()) {
                            out.add(
                                AnsweredCall(
                                    id = c.getLong(idIdx),
                                    number = num,
                                    type = type,
                                    date = date,
                                    durationSec = dur
                                )
                            )
                        }
                    }
                }
            }
        } catch (_: Exception) {}
        return out.reversed() // oldest-first so morning 6:09 sends before 6:46
    }
}
