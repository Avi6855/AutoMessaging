package com.avinashpatil.app.automessage.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Index

@Entity(tableName = "auto_reply_logs", indices = [Index(value = ["phoneNumber", "dayKey"], unique = true)])
data class AutoReplyLogEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val contactId: String,
    val contactName: String,
    val phoneNumber: String,
    val messageText: String,
    val timestamp: Long = System.currentTimeMillis(),
    val dayKey: String,
    val callType: String,
    val isAutoReply: Boolean = true,
    // Delivery tracking fields
    val status: String = "PENDING", // PENDING, SENT, DELIVERED, FAILED
    val attempts: Int = 0,
    val error: String? = null,
    val sentTimestamp: Long? = null,
    val deliveredTimestamp: Long? = null
)