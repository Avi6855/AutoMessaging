package com.avinashpatil.app.automessage.data.dao

import androidx.room.*
import com.avinashpatil.app.automessage.data.entity.CustomMessageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CustomMessageDao {
    
    @Query("SELECT * FROM custom_messages ORDER BY createdAt DESC")
    fun getAllMessages(): Flow<List<CustomMessageEntity>>
    
    @Query("SELECT * FROM custom_messages WHERE id = :messageId")
    suspend fun getMessageById(messageId: Long): CustomMessageEntity?
    
    @Query("SELECT * FROM custom_messages WHERE isDefault = 1")
    suspend fun getDefaultMessage(): CustomMessageEntity?
    
    @Query("SELECT * FROM custom_messages WHERE groupType = :groupType")
    suspend fun getMessageByGroupType(groupType: String): CustomMessageEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: CustomMessageEntity): Long
    
    @Update
    suspend fun updateMessage(message: CustomMessageEntity)
    
    @Delete
    suspend fun deleteMessage(message: CustomMessageEntity)
    
    @Query("UPDATE custom_messages SET isDefault = 0")
    suspend fun clearDefaultMessages()
    
    @Query("UPDATE custom_messages SET isDefault = 1 WHERE id = :messageId")
    suspend fun setAsDefault(messageId: Long)
    
    @Query("SELECT COUNT(*) FROM custom_messages")
    suspend fun getMessageCount(): Int
}