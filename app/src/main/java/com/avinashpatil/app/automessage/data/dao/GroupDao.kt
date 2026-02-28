package com.avinashpatil.app.automessage.data.dao

import androidx.room.*
import com.avinashpatil.app.automessage.data.entity.GroupEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GroupDao {
    
    @Query("SELECT * FROM groups ORDER BY name ASC")
    fun getAllGroups(): Flow<List<GroupEntity>>
    
    @Query("SELECT * FROM groups WHERE id = :groupId")
    suspend fun getGroupById(groupId: Long): GroupEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGroup(group: GroupEntity): Long
    
    @Update
    suspend fun updateGroup(group: GroupEntity)
    
    @Delete
    suspend fun deleteGroup(group: GroupEntity)
    
    @Query("UPDATE groups SET defaultMessageId = :messageId WHERE id = :groupId")
    suspend fun updateGroupMessage(groupId: Long, messageId: Long)
    
    @Query("SELECT * FROM groups WHERE name = :name")
    suspend fun getGroupByName(name: String): GroupEntity?
    
    @Query("SELECT COUNT(*) FROM groups")
    suspend fun getGroupCount(): Int
}