package com.avinashpatil.app.automessage.ui.screens.groups

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.avinashpatil.app.automessage.data.entity.GroupEntity
import com.avinashpatil.app.automessage.data.repository.GroupRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GroupViewModel @Inject constructor(
    private val groupRepository: GroupRepository
) : ViewModel() {
    
    private val _groups = MutableStateFlow<List<GroupEntity>>(emptyList())
    val groups: StateFlow<List<GroupEntity>> = _groups.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage.asStateFlow()
    
    init {
        loadGroups()
        createDefaultGroups()
    }
    
    private fun loadGroups() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                groupRepository.getAllGroups().collect { groups ->
                    _groups.value = groups
                }
            } catch (e: Exception) {
                _error.value = "Failed to load groups: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    private fun createDefaultGroups() {
        viewModelScope.launch {
            try {
                val existingGroups = groupRepository.getGroupCount()
                if (existingGroups == 0) {
                    // Create default groups
                    val defaultGroups = listOf(
                        GroupEntity(
                            name = "Family",
                            description = "Family members and close relatives"
                        ),
                        GroupEntity(
                            name = "Work",
                            description = "Colleagues and work-related contacts"
                        ),
                        GroupEntity(
                            name = "Friends",
                            description = "Friends and social contacts"
                        ),
                        GroupEntity(
                            name = "VIP",
                            description = "Very important contacts"
                        )
                    )
                    
                    defaultGroups.forEach { group ->
                        groupRepository.insertGroup(group)
                    }
                }
            } catch (e: Exception) {
                _error.value = "Failed to create default groups: ${e.message}"
            }
        }
    }
    
    fun addGroup(name: String, description: String) {
        viewModelScope.launch {
            try {
                val group = GroupEntity(
                    name = name,
                    description = description
                )
                groupRepository.insertGroup(group)
                _successMessage.value = "Group added successfully"
                clearMessages()
            } catch (e: Exception) {
                _error.value = "Failed to add group: ${e.message}"
            }
        }
    }
    
    fun updateGroup(group: GroupEntity) {
        viewModelScope.launch {
            try {
                groupRepository.updateGroup(group)
                _successMessage.value = "Group updated successfully"
                clearMessages()
            } catch (e: Exception) {
                _error.value = "Failed to update group: ${e.message}"
            }
        }
    }
    
    fun deleteGroup(group: GroupEntity) {
        viewModelScope.launch {
            try {
                groupRepository.deleteGroup(group.id)
                _successMessage.value = "Group deleted successfully"
                clearMessages()
            } catch (e: Exception) {
                _error.value = "Failed to delete group: ${e.message}"
            }
        }
    }
    
    fun updateGroupDefaultMessage(groupId: Long, messageId: Long) {
        viewModelScope.launch {
            try {
                groupRepository.updateGroupDefaultMessage(groupId, messageId)
                _successMessage.value = "Group default message updated"
                clearMessages()
            } catch (e: Exception) {
                _error.value = "Failed to update group default message: ${e.message}"
            }
        }
    }
    
    fun getGroupById(groupId: Long) {
        viewModelScope.launch {
            try {
                val group = groupRepository.getGroupById(groupId)
                // Handle the group data as needed
            } catch (e: Exception) {
                _error.value = "Failed to get group: ${e.message}"
            }
        }
    }
    
    fun getGroupByName(name: String) {
        viewModelScope.launch {
            try {
                val group = groupRepository.getGroupByName(name)
                // Handle the group data as needed
            } catch (e: Exception) {
                _error.value = "Failed to get group: ${e.message}"
            }
        }
    }
    
    fun clearMessages() {
        _error.value = null
        _successMessage.value = null
    }
}