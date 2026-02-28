package com.avinashpatil.app.automessage.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "discrepancy_logs")
data class DiscrepancyLogEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val callId: String,
    val phoneNumber: String,
    val contactId: String?,
    val contactName: String?,
    val callType: String,
    val callTimestamp: Long,
    val durationSec: Int,
    val status: String = "UNRESOLVED", // UNRESOLVED, RESOLVED, SENT_MANUALLY
    val createdAt: Long = System.currentTimeMillis(),
    val resolvedAt: Long? = null,
    val notes: String? = null
)