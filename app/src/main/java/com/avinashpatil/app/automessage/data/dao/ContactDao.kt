package com.avinashpatil.app.automessage.data.dao

import androidx.room.*
import com.avinashpatil.app.automessage.data.entity.ContactEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ContactDao {
    
    @Query("SELECT * FROM contacts ORDER BY name ASC")
    fun getAllContacts(): Flow<List<ContactEntity>>
    
    @Query("SELECT * FROM contacts WHERE id = :contactId")
    suspend fun getContactById(contactId: String): ContactEntity?
    
    @Query("SELECT * FROM contacts WHERE phoneNumber = :phone LIMIT 1")
    suspend fun getContactByPhoneNumber(phone: String): ContactEntity?
    
    @Query("SELECT * FROM contacts WHERE name LIKE '%' || :query || '%' OR phoneNumber LIKE '%' || :query || '%'")
    fun searchContacts(query: String): Flow<List<ContactEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContact(contact: ContactEntity)
    
    @Update
    suspend fun updateContact(contact: ContactEntity)
    
    @Delete
    suspend fun deleteContact(contact: ContactEntity)
    
    @Query("UPDATE contacts SET groupId = :groupId WHERE id = :contactId")
    suspend fun assignContactToGroup(contactId: String, groupId: Long)
    
    @Query("UPDATE contacts SET groupId = NULL WHERE id = :contactId")
    suspend fun removeContactFromGroup(contactId: String)
    
    @Query("UPDATE contacts SET isPriority = :isPriority WHERE id = :contactId")
    suspend fun updatePriorityStatus(contactId: String, isPriority: Boolean)
    
    @Query("UPDATE contacts SET isBlacklisted = :isBlacklisted WHERE id = :contactId")
    suspend fun updateBlacklistStatus(contactId: String, isBlacklisted: Boolean)
    
    @Query("SELECT * FROM contacts WHERE isPriority = 1")
    fun getPriorityContacts(): Flow<List<ContactEntity>>
    
    @Query("SELECT * FROM contacts WHERE isBlacklisted = 1")
    fun getBlacklistedContacts(): Flow<List<ContactEntity>>
    
    @Query("SELECT * FROM contacts WHERE groupId = :groupId")
    fun getContactsByGroup(groupId: Long): Flow<List<ContactEntity>>
    
    @Query("SELECT DISTINCT groupId FROM contacts WHERE groupId IS NOT NULL ORDER BY groupId ASC")
    fun getDistinctGroups(): Flow<List<Long>>
    
    @Query("SELECT COUNT(*) FROM contacts")
    suspend fun getContactCount(): Int
    
    @Query("SELECT COUNT(*) FROM contacts WHERE groupId = :groupId")
    suspend fun getContactCountByGroup(groupId: Long): Int
}