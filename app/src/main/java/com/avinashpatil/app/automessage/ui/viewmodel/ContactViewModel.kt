package com.avinashpatil.app.automessage.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.avinashpatil.app.automessage.data.entity.ContactEntity
import com.avinashpatil.app.automessage.data.repository.ContactRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ContactViewModel @Inject constructor(
    private val contactRepository: ContactRepository
) : ViewModel() {
    
    private val _contacts = MutableStateFlow<List<ContactEntity>>(emptyList())
    val contacts: StateFlow<List<ContactEntity>> = _contacts.asStateFlow()
    
    private val _priorityContacts = MutableStateFlow<List<ContactEntity>>(emptyList())
    val priorityContacts: StateFlow<List<ContactEntity>> = _priorityContacts.asStateFlow()
    
    private val _blacklistedContacts = MutableStateFlow<List<ContactEntity>>(emptyList())
    val blacklistedContacts: StateFlow<List<ContactEntity>> = _blacklistedContacts.asStateFlow()
    
    private val _searchResults = MutableStateFlow<List<ContactEntity>>(emptyList())
    val searchResults: StateFlow<List<ContactEntity>> = _searchResults.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    init {
        loadContacts()
        loadPriorityContacts()
        loadBlacklistedContacts()
    }
    
    private fun loadContacts() {
        contactRepository.getAllContacts()
            .onEach { contacts ->
                _contacts.value = contacts
            }
            .catch { e ->
                _error.value = "Failed to load contacts: ${e.message}"
            }
            .launchIn(viewModelScope)
    }
    
    private fun loadPriorityContacts() {
        contactRepository.getPriorityContacts()
            .onEach { contacts ->
                _priorityContacts.value = contacts
            }
            .catch { e ->
                _error.value = "Failed to load priority contacts: ${e.message}"
            }
            .launchIn(viewModelScope)
    }
    
    private fun loadBlacklistedContacts() {
        contactRepository.getBlacklistedContacts()
            .onEach { contacts ->
                _blacklistedContacts.value = contacts
            }
            .catch { e ->
                _error.value = "Failed to load blacklisted contacts: ${e.message}"
            }
            .launchIn(viewModelScope)
    }
    
    fun searchContacts(query: String) {
        if (query.isBlank()) {
            _searchResults.value = emptyList()
            return
        }
        
        contactRepository.searchContacts(query)
            .onEach { results ->
                _searchResults.value = results
            }
            .catch { e ->
                _error.value = "Search failed: ${e.message}"
            }
            .launchIn(viewModelScope)
    }
    
    fun addContact(contact: ContactEntity) {
        viewModelScope.launch {
            try {
                contactRepository.insertContact(contact)
            } catch (e: Exception) {
                _error.value = "Failed to add contact: ${e.message}"
            }
        }
    }
    
    fun updateContact(contact: ContactEntity) {
        viewModelScope.launch {
            try {
                contactRepository.updateContact(contact)
            } catch (e: Exception) {
                _error.value = "Failed to update contact: ${e.message}"
            }
        }
    }
    
    fun deleteContact(contact: ContactEntity) {
        viewModelScope.launch {
            try {
                contactRepository.deleteContact(contact)
            } catch (e: Exception) {
                _error.value = "Failed to delete contact: ${e.message}"
            }
        }
    }
    
    fun togglePriority(contactId: String, isPriority: Boolean) {
        viewModelScope.launch {
            try {
                contactRepository.markAsPriority(contactId, isPriority)
            } catch (e: Exception) {
                _error.value = "Failed to update priority status: ${e.message}"
            }
        }
    }
    
    fun toggleBlacklist(contactId: String, isBlacklisted: Boolean, reason: String? = null) {
        viewModelScope.launch {
            try {
                contactRepository.addToBlacklist(contactId, isBlacklisted, reason)
            } catch (e: Exception) {
                _error.value = "Failed to update blacklist status: ${e.message}"
            }
        }
    }
    
    fun assignToGroup(contactId: String, groupId: Long) {
        viewModelScope.launch {
            try {
                contactRepository.assignContactToGroup(contactId, groupId)
            } catch (e: Exception) {
                _error.value = "Failed to assign to group: ${e.message}"
            }
        }
    }
    
    fun clearError() {
        _error.value = null
    }
}