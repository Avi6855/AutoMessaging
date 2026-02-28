package com.avinashpatil.app.automessage.ui.screens.messages

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.avinashpatil.app.automessage.data.entity.CustomMessageEntity
import com.avinashpatil.app.automessage.data.repository.MessageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class MessagesViewModel @Inject constructor(
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
        viewModelScope.launch {
            try {
                messageRepository.getAllMessages().collect { list ->
                    _messages.value = list
                }
            } catch (e: Exception) {
                _error.value = "Failed to load messages: ${e.message}"
            }
        }
    }

    private fun loadDefaultMessage() {
        viewModelScope.launch {
            try {
                val message = messageRepository.getDefaultMessage()
                _defaultMessage.value = message
            } catch (e: Exception) {
                _error.value = "Failed to load default message: ${e.message}"
            }
        }
    }

    // Add simple API expected by UI to add a new message
    fun addMessage(title: String, body: String) {
        viewModelScope.launch {
            try {
                val message = CustomMessageEntity(
                    title = title,
                    body = body,
                    groupType = "default",
                    isDefault = false
                )
                messageRepository.insertMessage(message)
                _successMessage.value = "Message added"
                // Refresh list so UI updates
                loadMessages()
            } catch (e: Exception) {
                _error.value = "Failed to add message: ${e.message}"
            }
        }
    }

    fun updateMessage(message: CustomMessageEntity) {
        viewModelScope.launch {
            try {
                messageRepository.updateMessage(message)
                _successMessage.value = "Message updated"
                clearMessages()
            } catch (e: Exception) {
                _error.value = "Failed to update message: ${e.message}"
            }
        }
    }

    fun deleteMessage(message: CustomMessageEntity) {
        viewModelScope.launch {
            try {
                messageRepository.deleteMessage(message.id)
                _successMessage.value = "Message deleted"
                clearMessages()
            } catch (e: Exception) {
                _error.value = "Failed to delete message: ${e.message}"
            }
        }
    }

    fun setDefaultMessage(messageId: Long) {
        viewModelScope.launch {
            try {
                messageRepository.setAsDefault(messageId)
                // Immediately refresh default message state so UI reflects new default
                loadDefaultMessage()
                _successMessage.value = "Default message set successfully"
                clearMessages()
            } catch (e: Exception) {
                _error.value = "Failed to set default message: ${e.message}"
            }
        }
    }

    fun clearMessages() {
        _error.value = null
        _successMessage.value = null
    }
}