package com.avinashpatil.app.automessage.data.repository

import com.avinashpatil.app.automessage.data.dao.ContactDao
import com.avinashpatil.app.automessage.data.entity.ContactEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ContactRepositoryImpl @Inject constructor(
    private val contactDao: ContactDao
) : ContactRepository {
    
    override fun getAllContacts(): Flow<List<ContactEntity>> {
        return contactDao.getAllContacts()
    }
    
    override suspend fun getContactById(contactId: String): ContactEntity? {
        return contactDao.getContactById(contactId)
    }
    
    override suspend fun getContactByPhoneNumber(phone: String): ContactEntity? {
        return contactDao.getContactByPhoneNumber(phone)
    }
    
    override fun searchContacts(query: String): Flow<List<ContactEntity>> {
        return contactDao.searchContacts(query)
    }
    
    override suspend fun insertContact(contact: ContactEntity) {
        contactDao.insertContact(contact)
    }
    
    override suspend fun updateContact(contact: ContactEntity) {
        contactDao.updateContact(contact)
    }
    
    override suspend fun deleteContact(contact: ContactEntity) {
        contactDao.deleteContact(contact)
    }
    
    override suspend fun assignContactToGroup(contactId: String, groupId: Long) {
        contactDao.assignContactToGroup(contactId, groupId)
    }
    
    override suspend fun removeContactFromGroup(contactId: String) {
        contactDao.removeContactFromGroup(contactId)
    }
    
    override suspend fun markAsPriority(contactId: String, isPriority: Boolean) {
        contactDao.updatePriorityStatus(contactId, isPriority)
    }
    
    override suspend fun addToBlacklist(contactId: String, isBlacklisted: Boolean, reason: String?) {
        contactDao.updateBlacklistStatus(contactId, isBlacklisted)
    }
    
    override fun getPriorityContacts(): Flow<List<ContactEntity>> {
        return contactDao.getPriorityContacts()
    }
    
    override fun getBlacklistedContacts(): Flow<List<ContactEntity>> {
        return contactDao.getBlacklistedContacts()
    }
    
    override fun getContactsByGroup(groupId: Long): Flow<List<ContactEntity>> {
        return contactDao.getContactsByGroup(groupId)
    }
    
    override fun getDistinctGroups(): Flow<List<Long>> {
        return contactDao.getDistinctGroups()
    }
    
    override suspend fun getContactCount(): Int {
        return contactDao.getContactCount()
    }
    
    override suspend fun getContactCountByGroup(groupId: Long): Int {
        return contactDao.getContactCountByGroup(groupId)
    }
    
    override suspend fun isPriorityContact(contactId: String): Boolean {
        return contactDao.getContactById(contactId)?.isPriority ?: false
    }
    
    override suspend fun isBlacklistedContact(contactId: String): Boolean {
        return contactDao.getContactById(contactId)?.isBlacklisted ?: false
    }
}