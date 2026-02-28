package com.avinashpatil.app.automessage.ui.screens.contacts

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.avinashpatil.app.automessage.data.entity.ContactEntity
import com.avinashpatil.app.automessage.data.repository.ContactRepository
import com.avinashpatil.app.automessage.utils.ContactHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
open class ContactsViewModel @Inject constructor(
    private val contactRepository: ContactRepository
) : ViewModel() {
    private val _contacts = MutableStateFlow<List<ContactEntity>>(emptyList())
    open val contacts: StateFlow<List<ContactEntity>> = _contacts.asStateFlow()

    private val _priorityContacts = MutableStateFlow<List<ContactEntity>>(emptyList())
    open val priorityContacts: StateFlow<List<ContactEntity>> = _priorityContacts.asStateFlow()

    private val _blacklistedContacts = MutableStateFlow<List<ContactEntity>>(emptyList())
    open val blacklistedContacts: StateFlow<List<ContactEntity>> = _blacklistedContacts.asStateFlow()

    private val _searchResults = MutableStateFlow<List<ContactEntity>>(emptyList())
    open val searchResults: StateFlow<List<ContactEntity>> = _searchResults.asStateFlow()

    private val _groups = MutableStateFlow<List<Long>>(emptyList())
    open val groups: StateFlow<List<Long>> = _groups.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage.asStateFlow()

    private var searchJob: Job? = null

    init {
        loadContacts()
        loadPriorityContacts()
        loadBlacklistedContacts()
        loadGroups()
    }

    private fun loadContacts() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                contactRepository.getAllContacts().collect { contacts ->
                    _contacts.value = contacts
                }
            } catch (e: Exception) {
                _error.value = "Failed to load contacts: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun loadPriorityContacts() {
        viewModelScope.launch {
            try {
                contactRepository.getPriorityContacts().collect { contacts ->
                    _priorityContacts.value = contacts
                }
            } catch (e: Exception) {
                _error.value = "Failed to load priority contacts: ${e.message}"
            }
        }
    }

    private fun loadBlacklistedContacts() {
        viewModelScope.launch {
            try {
                contactRepository.getBlacklistedContacts().collect { contacts ->
                    _blacklistedContacts.value = contacts
                }
            } catch (e: Exception) {
                _error.value = "Failed to load blacklisted contacts: ${e.message}"
            }
        }
    }

    private fun loadGroups() {
        viewModelScope.launch {
            try {
                contactRepository.getDistinctGroups().collect { ids ->
                    _groups.value = ids
                }
            } catch (e: Exception) {
                _error.value = "Failed to load groups: ${e.message}"
            }
        }
    }

    fun syncDeviceContacts(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _isLoading.value = true
                val deviceContacts = ContactHelper.getDeviceContacts(context)
                for (dc in deviceContacts) {
                    val normalizedPhone = normalizePhone(dc.phoneNumber)
                    val existing = contactRepository.getContactByPhoneNumber(normalizedPhone)
                    if (existing == null) {
                        val newContact = dc.copy(
                            id = "phone:${normalizedPhone}",
                            phoneNumber = normalizedPhone
                        )
                        contactRepository.insertContact(newContact)
                    } else {
                        val updated = existing.copy(
                            name = dc.name,
                            photoUri = dc.photoUri,
                            updatedAt = System.currentTimeMillis()
                        )
                        contactRepository.updateContact(updated)
                    }
                }
                _successMessage.value = "Contacts synced"
                clearMessages()
            } catch (e: Exception) {
                _error.value = "Contacts sync failed: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun normalizePhone(phone: String): String {
        return phone.filter { it.isDigit() }
    }

    fun searchContacts(query: String) {
        // Cancel any ongoing search to avoid multiple collectors
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            try {
                contactRepository.searchContacts(query).collect { results ->
                    _searchResults.value = results
                }
            } catch (e: Exception) {
                _error.value = "Search failed: ${e.message}"
            }
        }
    }

    fun clearSearchResults() {
        // Clear search and cancel any in-flight search
        _searchResults.value = emptyList()
        searchJob?.cancel()
        searchJob = null
    }

    fun addContact(name: String, phoneNumber: String) {
        viewModelScope.launch {
            try {
                val normalizedPhone = normalizePhone(phoneNumber)
                val contact = ContactEntity(
                    id = UUID.randomUUID().toString(),
                    name = name,
                    phoneNumber = normalizedPhone
                )
                contactRepository.insertContact(contact)
                _successMessage.value = "Contact added successfully"
                clearMessages()
            } catch (e: Exception) {
                _error.value = "Failed to add contact: ${e.message}"
            }
        }
    }

    fun updateContact(contact: ContactEntity) {
        viewModelScope.launch {
            try {
                contactRepository.updateContact(contact)
                _successMessage.value = "Contact updated successfully"
                clearMessages()
            } catch (e: Exception) {
                _error.value = "Failed to update contact: ${e.message}"
            }
        }
    }

    fun deleteContact(contact: ContactEntity) {
        viewModelScope.launch {
            try {
                contactRepository.deleteContact(contact)
                _successMessage.value = "Contact deleted successfully"
                clearMessages()
            } catch (e: Exception) {
                _error.value = "Failed to delete contact: ${e.message}"
            }
        }
    }

    fun togglePriority(contactId: String, isPriority: Boolean) {
        viewModelScope.launch {
            try {
                contactRepository.markAsPriority(contactId, isPriority)
                _successMessage.value = if (isPriority) "Added to priority" else "Removed from priority"
                clearMessages()
            } catch (e: Exception) {
                _error.value = "Failed to update priority: ${e.message}"
            }
        }
    }

    fun toggleBlacklist(contactId: String, isBlacklisted: Boolean, reason: String? = null) {
        viewModelScope.launch {
            try {
                contactRepository.addToBlacklist(contactId, isBlacklisted, reason)
                _successMessage.value = if (isBlacklisted) "Added to blacklist" else "Removed from blacklist"
                clearMessages()
            } catch (e: Exception) {
                _error.value = "Failed to update blacklist: ${e.message}"
            }
        }
    }

    fun assignToGroup(contactId: String, groupId: Long) {
        viewModelScope.launch {
            try {
                val contact = contactRepository.getContactById(contactId)
                contact?.let {
                    val updatedContact = it.copy(groupId = groupId)
                    contactRepository.updateContact(updatedContact)
                    _successMessage.value = "Contact assigned to group"
                    clearMessages()
                }
            } catch (e: Exception) {
                _error.value = "Failed to assign to group: ${e.message}"
            }
        }
    }

    fun removeFromGroup(contactId: String) {
        viewModelScope.launch {
            try {
                val contact = contactRepository.getContactById(contactId)
                contact?.let {
                    val updatedContact = it.copy(groupId = null)
                    contactRepository.updateContact(updatedContact)
                    _successMessage.value = "Contact removed from group"
                    clearMessages()
                }
            } catch (e: Exception) {
                _error.value = "Failed to remove from group: ${e.message}"
            }
        }
    }

    suspend fun getContactCountByGroup(groupId: Long): Int {
        return try {
            contactRepository.getContactCountByGroup(groupId)
        } catch (e: Exception) {
            0
        }
    }

    fun clearMessages() {
        _error.value = null
        _successMessage.value = null
    }
}