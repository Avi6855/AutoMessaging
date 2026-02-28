package com.avinashpatil.app.automessage.ui.screens.autoreply

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.avinashpatil.app.automessage.data.entity.AutoReplyLogEntity
import com.avinashpatil.app.automessage.data.entity.LastSeenCallEntity
import com.avinashpatil.app.automessage.data.repository.AutoReplyRepository
import com.avinashpatil.app.automessage.data.repository.DataStoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AutoReplyViewModel @Inject constructor(
    private val autoReplyRepository: AutoReplyRepository,
    private val dataStoreRepository: DataStoreRepository
) : ViewModel() {
    
    private val _autoReplyEnabled = MutableStateFlow(false)
    val autoReplyEnabled: StateFlow<Boolean> = _autoReplyEnabled.asStateFlow()
    
    private val _autoReplyDelay = MutableStateFlow(10)
    val autoReplyDelay: StateFlow<Int> = _autoReplyDelay.asStateFlow()
    
    private val _autoReplyLogs = MutableStateFlow<List<AutoReplyLogEntity>>(emptyList())
    val autoReplyLogs: StateFlow<List<AutoReplyLogEntity>> = _autoReplyLogs.asStateFlow()
    
    private val _lastSeenCall = MutableStateFlow<LastSeenCallEntity?>(null)
    val lastSeenCall: StateFlow<LastSeenCallEntity?> = _lastSeenCall.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage.asStateFlow()
    
    init {
        loadAutoReplySettings()
        loadAutoReplyLogs()
        loadLastSeenCall()
    }
    
    private fun loadAutoReplySettings() {
        viewModelScope.launch {
            try {
                dataStoreRepository.isAutoReplyEnabled().collect { enabled ->
                    _autoReplyEnabled.value = enabled
                }
                
                dataStoreRepository.getAutoReplyDelay().collect { delay ->
                    _autoReplyDelay.value = delay
                }
            } catch (e: Exception) {
                _error.value = "Failed to load auto-reply settings: ${e.message}"
            }
        }
    }
    
    private fun loadAutoReplyLogs() {
        viewModelScope.launch {
            try {
                autoReplyRepository.getAutoReplyHistory().collect { logs ->
                    _autoReplyLogs.value = logs
                }
            } catch (e: Exception) {
                _error.value = "Failed to load auto-reply logs: ${e.message}"
            }
        }
    }
    
    private fun loadLastSeenCall() {
        viewModelScope.launch {
            try {
                val lastCall = autoReplyRepository.getLastSeenCall()
                _lastSeenCall.value = lastCall
            } catch (e: Exception) {
                _error.value = "Failed to load last seen call: ${e.message}"
            }
        }
    }
    
    fun setAutoReplyEnabled(enabled: Boolean) {
        viewModelScope.launch {
            try {
                dataStoreRepository.saveAutoReplyEnabled(enabled)
                _successMessage.value = "Auto-reply ${if (enabled) "enabled" else "disabled"}"
                clearMessages()
            } catch (e: Exception) {
                _error.value = "Failed to update auto-reply setting: ${e.message}"
            }
        }
    }
    
    fun setAutoReplyDelay(delay: Int) {
        viewModelScope.launch {
            try {
                dataStoreRepository.saveAutoReplyDelay(delay)
                _successMessage.value = "Auto-reply delay set to $delay seconds"
                clearMessages()
            } catch (e: Exception) {
                _error.value = "Failed to update auto-reply delay: ${e.message}"
            }
        }
    }
    
    fun logAutoReply(
        contactId: String,
        contactName: String,
        phoneNumber: String,
        messageText: String,
        callType: String,
        timestamp: Long = System.currentTimeMillis()
    ) {
        viewModelScope.launch {
            try {
                val ts = timestamp
                val dayKey = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date(ts))
                val log = AutoReplyLogEntity(
                    contactId = contactId,
                    contactName = contactName,
                    phoneNumber = phoneNumber,
                    messageText = messageText,
                    timestamp = ts,
                    dayKey = dayKey,
                    callType = callType,
                    isAutoReply = true
                )
                autoReplyRepository.logAutoReply(log)
                clearMessages()
            } catch (e: Exception) {
                _error.value = "Failed to log auto-reply: ${e.message}"
            }
        }
    }
    
    fun deleteAutoReplyLog(log: AutoReplyLogEntity) {
        viewModelScope.launch {
            try {
                autoReplyRepository.deleteAutoReplyLog(log)
                _successMessage.value = "Auto-reply log deleted successfully"
                clearMessages()
            } catch (e: Exception) {
                _error.value = "Failed to delete log: ${e.message}"
            }
        }
    }
    
    fun deleteAllAutoReplyLogs() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                autoReplyRepository.deleteAllAutoReplyLogs()
                _successMessage.value = "All auto-reply logs deleted successfully"
                clearMessages()
            } catch (e: Exception) {
                _error.value = "Failed to delete all logs: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun updateLastSeenCall(callId: String, contactId: String?) {
        viewModelScope.launch {
            try {
                autoReplyRepository.updateLastSeenCall(callId, contactId)
                clearMessages()
            } catch (e: Exception) {
                _error.value = "Failed to update last seen call: ${e.message}"
            }
        }
    }
    
    suspend fun hasRecentReply(contactId: String): Boolean {
        return try {
            autoReplyRepository.hasRecentReply(contactId)
        } catch (e: Exception) {
            _error.value = "Failed to check recent reply: ${e.message}"
            false
        }
    }
    
    fun clearMessages() {
        _error.value = null
        _successMessage.value = null
    }
}