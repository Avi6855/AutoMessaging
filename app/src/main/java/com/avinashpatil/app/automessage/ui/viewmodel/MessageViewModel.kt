package com.avinashpatil.app.automessage.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.avinashpatil.app.automessage.data.entity.CustomMessageEntity
import com.avinashpatil.app.automessage.data.repository.MessageRepository
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
class MessageViewModel @Inject constructor(
    private val messageRepository: MessageRepository
) : ViewModel() {
    
    private val _messages = MutableStateFlow<List<CustomMessageEntity>>(emptyList())
    val messages: StateFlow<List<CustomMessageEntity>> = _messages.asStateFlow()
    
    private val _defaultMessage = MutableStateFlow<CustomMessageEntity?>(null)
    val defaultMessage: StateFlow<CustomMessageEntity?> = _defaultMessage.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage.asStateFlow()
    
    init {
        loadMessages()
        loadDefaultMessage()
    }
    
    private fun loadMessages() {
        messageRepository.getAllMessages()
            .onEach { messages ->
                _messages.value = messages
            }
            .catch { e ->
                _error.value = "Failed to load messages: ${e.message}"
            }
            .launchIn(viewModelScope)
    }
    
    private fun loadDefaultMessage() {
        viewModelScope.launch {
            try {
                _defaultMessage.value = messageRepository.getDefaultMessage()
            } catch (e: Exception) {
                _error.value = "Failed to load default message: ${e.message}"
            }
        }
    }
    
    fun addMessage(title: String, body: String, groupType: String = "default", isDefault: Boolean = false) {
        viewModelScope.launch {
            try {
                val message = CustomMessageEntity(
                    title = title,
                    body = body,
                    groupType = groupType,
                    isDefault = isDefault
                )
                val messageId = messageRepository.insertMessage(message)
                
                if (isDefault) {
                    messageRepository.setAsDefault(messageId)
                    _defaultMessage.value = message.copy(id = messageId)
                }
                
                _successMessage.value = "Message added successfully"
            } catch (e: Exception) {
                _error.value = "Failed to add message: ${e.message}"
            }
        }
    }
    
    fun updateMessage(messageId: Long, title: String, body: String, groupType: String) {
        viewModelScope.launch {
            try {
                val existingMessage = messageRepository.getMessageById(messageId)
                existingMessage?.let {
                    val updatedMessage = it.copy(
                        title = title,
                        body = body,
                        groupType = groupType
                    )
                    messageRepository.updateMessage(updatedMessage)
                    _successMessage.value = "Message updated successfully"
                } ?: run {
                    _error.value = "Message not found"
                }
            } catch (e: Exception) {
                _error.value = "Failed to update message: ${e.message}"
            }
        }
    }
    
    fun deleteMessage(messageId: Long) {
        viewModelScope.launch {
            try {
                messageRepository.deleteMessage(messageId)
                _successMessage.value = "Message deleted successfully"
            } catch (e: Exception) {
                _error.value = "Failed to delete message: ${e.message}"
            }
        }
    }
    
    fun setAsDefault(messageId: Long) {
        viewModelScope.launch {
            try {
                messageRepository.setAsDefault(messageId)
                loadDefaultMessage()
                _successMessage.value = "Default message set successfully"
            } catch (e: Exception) {
                _error.value = "Failed to set default message: ${e.message}"
            }
        }
    }
    
    fun getMessageByGroupType(groupType: String): CustomMessageEntity? {
        return _messages.value.find { it.groupType == groupType }
    }
    
    fun clearError() {
        _error.value = null
    }
    
    fun clearSuccessMessage() {
        _successMessage.value = null
    }
}