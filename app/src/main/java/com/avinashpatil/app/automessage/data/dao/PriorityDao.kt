package com.avinashpatil.app.automessage.data.dao

import androidx.room.*
import com.avinashpatil.app.automessage.data.entity.PriorityEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PriorityDao {
    
    @Query("SELECT * FROM priority_contacts ORDER BY addedAt DESC")
    fun getAllPriorityContacts(): Flow<List<PriorityEntity>>
    
    @Query("SELECT * FROM priority_contacts WHERE contactId = :contactId")
    suspend fun getPriorityContact(contactId: String): PriorityEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPriorityContact(priority: PriorityEntity)
    
    @Delete
    suspend fun deletePriorityContact(priority: PriorityEntity)
    
    @Query("DELETE FROM priority_contacts WHERE contactId = :contactId")
    suspend fun deletePriorityContactById(contactId: String)
    
    @Query("SELECT COUNT(*) FROM priority_contacts")
    suspend fun getPriorityContactCount(): Int
    
    @Query("SELECT COUNT(*) FROM priority_contacts WHERE contactId = :contactId")
    suspend fun isPriorityContact(contactId: String): Int
}