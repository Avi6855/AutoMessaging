package com.avinashpatil.app.automessage.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.avinashpatil.app.automessage.data.entity.GroupEntity
import com.avinashpatil.app.automessage.data.repository.GroupRepository
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
    }
    
    private fun loadGroups() {
        groupRepository.getAllGroups()
            .onEach { groups ->
                _groups.value = groups
            }
            .catch { e ->
                _error.value = "Failed to load groups: ${e.message}"
            }
            .launchIn(viewModelScope)
    }
    
    fun addGroup(name: String, description: String = "") {
        viewModelScope.launch {
            try {
                val group = GroupEntity(
                    name = name,
                    description = description
                )
                groupRepository.insertGroup(group)
                _successMessage.value = "Group '$name' created successfully"
            } catch (e: Exception) {
                _error.value = "Failed to create group: ${e.message}"
            }
        }
    }
    
    fun updateGroup(groupId: Long, name: String, description: String) {
        viewModelScope.launch {
            try {
                val existingGroup = groupRepository.getGroupById(groupId)
                existingGroup?.let {
                    val updatedGroup = it.copy(
                        name = name,
                        description = description
                    )
                    groupRepository.updateGroup(updatedGroup)
                    _successMessage.value = "Group updated successfully"
                } ?: run {
                    _error.value = "Group not found"
                }
            } catch (e: Exception) {
                _error.value = "Failed to update group: ${e.message}"
            }
        }
    }
    
    fun deleteGroup(groupId: Long) {
        viewModelScope.launch {
            try {
                groupRepository.deleteGroup(groupId)
                _successMessage.value = "Group deleted successfully"
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
            } catch (e: Exception) {
                _error.value = "Failed to update group default message: ${e.message}"
            }
        }
    }
    
    fun getGroupById(groupId: Long): GroupEntity? {
        return _groups.value.find { it.id == groupId }
    }
    
    fun getGroupByName(name: String): GroupEntity? {
        return _groups.value.find { it.name.equals(name, ignoreCase = true) }
    }
    
    fun createDefaultGroups() {
        val defaultGroups = listOf(
            GroupEntity(name = "Family", description = "Family members and close relatives"),
            GroupEntity(name = "Work", description = "Colleagues and work-related contacts"),
            GroupEntity(name = "Friends", description = "Friends and social contacts"),
            GroupEntity(name = "VIP", description = "Very important contacts")
        )
        
        viewModelScope.launch {
            try {
                defaultGroups.forEach { group ->
                    val existingGroup = groupRepository.getGroupByName(group.name)
                    if (existingGroup == null) {
                        groupRepository.insertGroup(group)
                    }
                }
                _successMessage.value = "Default groups created"
            } catch (e: Exception) {
                _error.value = "Failed to create default groups: ${e.message}"
            }
        }
    }
    
    fun clearError() {
        _error.value = null
    }
    
    fun clearSuccessMessage() {
        _successMessage.value = null
    }
}