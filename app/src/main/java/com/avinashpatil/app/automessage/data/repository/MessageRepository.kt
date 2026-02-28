package com.avinashpatil.app.automessage.data.repository

import com.avinashpatil.app.automessage.data.entity.CustomMessageEntity
import kotlinx.coroutines.flow.Flow

interface MessageRepository {
    fun getAllMessages(): Flow<List<CustomMessageEntity>>
    suspend fun getMessageById(messageId: Long): CustomMessageEntity?
    suspend fun getDefaultMessage(): CustomMessageEntity?
    suspend fun getMessageByGroupType(groupType: String): CustomMessageEntity?
    suspend fun insertMessage(message: CustomMessageEntity): Long
    suspend fun updateMessage(message: CustomMessageEntity)
    suspend fun deleteMessage(messageId: Long)
    suspend fun setAsDefault(messageId: Long)
    suspend fun clearDefaultMessages()
    suspend fun getMessageCount(): Int
}