package com.avinashpatil.app.automessage.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "last_seen_calls")
data class LastSeenCallEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val callId: String,
    val timestamp: Long = System.currentTimeMillis(),
    val contactId: String? = null
)