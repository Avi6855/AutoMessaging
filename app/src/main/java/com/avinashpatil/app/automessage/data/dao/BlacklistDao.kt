package com.avinashpatil.app.automessage.data.dao

import androidx.room.*
import com.avinashpatil.app.automessage.data.entity.BlacklistEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BlacklistDao {
    
    @Query("SELECT * FROM blacklist ORDER BY addedAt DESC")
    fun getAllBlacklistedContacts(): Flow<List<BlacklistEntity>>
    
    @Query("SELECT * FROM blacklist WHERE contactId = :contactId")
    suspend fun getBlacklistedContact(contactId: String): BlacklistEntity?
    
    @Query("SELECT * FROM blacklist WHERE phoneNumber = :phoneNumber")
    suspend fun getBlacklistedContactByPhone(phoneNumber: String): BlacklistEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBlacklistedContact(blacklist: BlacklistEntity)
    
    @Delete
    suspend fun deleteBlacklistedContact(blacklist: BlacklistEntity)
    
    @Query("DELETE FROM blacklist WHERE contactId = :contactId")
    suspend fun deleteBlacklistedContactById(contactId: String)
    
    @Query("SELECT COUNT(*) FROM blacklist")
    suspend fun getBlacklistedContactCount(): Int
    
    @Query("SELECT COUNT(*) FROM blacklist WHERE contactId = :contactId")
    suspend fun isBlacklistedContact(contactId: String): Int
}