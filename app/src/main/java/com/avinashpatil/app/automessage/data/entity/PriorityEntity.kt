package com.avinashpatil.app.automessage.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "priority_contacts")
data class PriorityEntity(
    @PrimaryKey
    val contactId: String,
    val contactName: String,
    val addedAt: Long = System.currentTimeMillis()
)