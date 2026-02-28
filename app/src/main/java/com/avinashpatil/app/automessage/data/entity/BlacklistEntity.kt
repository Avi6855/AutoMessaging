package com.avinashpatil.app.automessage.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "blacklist")
data class BlacklistEntity(
    @PrimaryKey
    val contactId: String,
    val phoneNumber: String,
    val reason: String? = null,
    val addedAt: Long = System.currentTimeMillis()
)