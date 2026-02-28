package com.avinashpatil.app.automessage.data.repository

import com.avinashpatil.app.automessage.data.entity.ContactEntity
import kotlinx.coroutines.flow.Flow

interface ContactRepository {
    fun getAllContacts(): Flow<List<ContactEntity>>
    suspend fun getContactById(contactId: String): ContactEntity?
    suspend fun getContactByPhoneNumber(phone: String): ContactEntity?
    fun searchContacts(query: String): Flow<List<ContactEntity>>
    suspend fun insertContact(contact: ContactEntity)
    suspend fun updateContact(contact: ContactEntity)
    suspend fun deleteContact(contact: ContactEntity)
    suspend fun assignContactToGroup(contactId: String, groupId: Long)
    suspend fun removeContactFromGroup(contactId: String)
    suspend fun markAsPriority(contactId: String, isPriority: Boolean)
    suspend fun addToBlacklist(contactId: String, isBlacklisted: Boolean, reason: String? = null)
    fun getPriorityContacts(): Flow<List<ContactEntity>>
    fun getBlacklistedContacts(): Flow<List<ContactEntity>>
    fun getContactsByGroup(groupId: Long): Flow<List<ContactEntity>>
    fun getDistinctGroups(): Flow<List<Long>>
    suspend fun getContactCount(): Int
    suspend fun getContactCountByGroup(groupId: Long): Int
    suspend fun isPriorityContact(contactId: String): Boolean
    suspend fun isBlacklistedContact(contactId: String): Boolean
}