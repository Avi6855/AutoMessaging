package com.avinashpatil.app.automessage.utils

import android.content.Context
import android.provider.CallLog

object CallLogHelper {
    data class CallEntry(
        val id: Long,
        val number: String,
        val type: Int,
        val date: Long,
        val durationSec: Int
    )

    fun getAnsweredCallsSince(context: Context, sinceMillis: Long): List<CallEntry> {
        val uri = CallLog.Calls.CONTENT_URI
        val projection = arrayOf(
            CallLog.Calls._ID,
            CallLog.Calls.NUMBER,
            CallLog.Calls.TYPE,
            CallLog.Calls.DATE,
            CallLog.Calls.DURATION
        )
        val selection = "${CallLog.Calls.DATE} >= ? AND ${CallLog.Calls.DURATION} > 0 AND (${CallLog.Calls.TYPE} = ${CallLog.Calls.INCOMING_TYPE} OR ${CallLog.Calls.TYPE} = ${CallLog.Calls.OUTGOING_TYPE})"
        val selectionArgs = arrayOf(sinceMillis.toString())
        val sortOrder = CallLog.Calls.DATE + " DESC"
        val list = mutableListOf<CallEntry>()
        val cursor = context.contentResolver.query(uri, projection, selection, selectionArgs, sortOrder)
        cursor?.use { c ->
            val idIdx = c.getColumnIndexOrThrow(CallLog.Calls._ID)
            val numberIdx = c.getColumnIndexOrThrow(CallLog.Calls.NUMBER)
            val typeIdx = c.getColumnIndexOrThrow(CallLog.Calls.TYPE)
            val dateIdx = c.getColumnIndexOrThrow(CallLog.Calls.DATE)
            val durIdx = c.getColumnIndexOrThrow(CallLog.Calls.DURATION)
            while (c.moveToNext()) {
                list.add(
                    CallEntry(
                        id = c.getLong(idIdx),
                        number = c.getString(numberIdx) ?: "",
                        type = c.getInt(typeIdx),
                        date = c.getLong(dateIdx),
                        durationSec = c.getInt(durIdx)
                    )
                )
            }
        }
        return list
    }

    fun getLatestCallTimestamp(context: Context): Long {
        val uri = CallLog.Calls.CONTENT_URI
        val projection = arrayOf(CallLog.Calls.DATE)
        val sortOrder = CallLog.Calls.DATE + " DESC"
        val cursor = context.contentResolver.query(uri, projection, null, null, sortOrder)
        var ts = 0L
        cursor?.use { c ->
            if (c.moveToFirst()) {
                val dateIdx = c.getColumnIndexOrThrow(CallLog.Calls.DATE)
                ts = c.getLong(dateIdx)
            }
        }
        return ts
    }
}