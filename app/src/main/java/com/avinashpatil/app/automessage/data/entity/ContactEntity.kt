package com.avinashpatil.app.automessage.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "contacts")
data class ContactEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val phoneNumber: String,
    val photoUri: String? = null,
    val groupId: Long? = null,
    val isPriority: Boolean = false,
    val isBlacklisted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)