package com.avinashpatil.app.automessage.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.avinashpatil.app.automessage.data.entity.AutoReplyLogEntity
import com.avinashpatil.app.automessage.data.repository.AutoReplyRepository
import com.avinashpatil.app.automessage.data.repository.DataStoreRepository
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
class AutoReplyViewModel @Inject constructor(
    private val autoReplyRepository: AutoReplyRepository,
    private val dataStoreRepository: DataStoreRepository
) : ViewModel() {
    
    private val _isAutoReplyEnabled = MutableStateFlow(false)
    val isAutoReplyEnabled: StateFlow<Boolean> = _isAutoReplyEnabled.asStateFlow()
    
    private val _autoReplyDelay = MutableStateFlow(10)
    val autoReplyDelay: StateFlow<Int> = _autoReplyDelay.asStateFlow()
    
    private val _autoReplyHistory = MutableStateFlow<List<AutoReplyLogEntity>>(emptyList())
    val autoReplyHistory: StateFlow<List<AutoReplyLogEntity>> = _autoReplyHistory.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage.asStateFlow()
    
    init {
        loadAutoReplySettings()
        loadAutoReplyHistory()
    }
    
    private fun loadAutoReplySettings() {
        dataStoreRepository.isAutoReplyEnabled()
            .onEach { enabled ->
                _isAutoReplyEnabled.value = enabled
            }
            .catch { e ->
                _error.value = "Failed to load auto-reply settings: ${e.message}"
            }
            .launchIn(viewModelScope)
        
        dataStoreRepository.getAutoReplyDelay()
            .onEach { delay ->
                _autoReplyDelay.value = delay
            }
            .catch { e ->
                _error.value = "Failed to load auto-reply delay: ${e.message}"
            }
            .launchIn(viewModelScope)
    }
    
    private fun loadAutoReplyHistory() {
        autoReplyRepository.getAutoReplyHistory()
            .onEach { history ->
                _autoReplyHistory.value = history
            }
            .catch { e ->
                _error.value = "Failed to load auto-reply history: ${e.message}"
            }
            .launchIn(viewModelScope)
    }
    
    fun toggleAutoReply(enabled: Boolean) {
        viewModelScope.launch {
            try {
                dataStoreRepository.saveAutoReplyEnabled(enabled)
                _isAutoReplyEnabled.value = enabled
                _successMessage.value = if (enabled) "Auto-reply enabled" else "Auto-reply disabled"
            } catch (e: Exception) {
                _error.value = "Failed to update auto-reply setting: ${e.message}"
            }
        }
    }
    
    fun updateAutoReplyDelay(delaySeconds: Int) {
        viewModelScope.launch {
            try {
                dataStoreRepository.saveAutoReplyDelay(delaySeconds)
                _autoReplyDelay.value = delaySeconds
                _successMessage.value = "Auto-reply delay updated to $delaySeconds seconds"
            } catch (e: Exception) {
                _error.value = "Failed to update auto-reply delay: ${e.message}"
            }
        }
    }
    
    fun logAutoReply(contactId: String, contactName: String, phoneNumber: String, messageText: String, callType: String) {
        viewModelScope.launch {
            try {
                val ts = System.currentTimeMillis()
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
                _successMessage.value = "Auto-reply logged successfully"
            } catch (e: Exception) {
                _error.value = "Failed to log auto-reply: ${e.message}"
            }
        }
    }
    
    fun deleteAutoReplyLog(log: AutoReplyLogEntity) {
        viewModelScope.launch {
            try {
                autoReplyRepository.deleteAutoReplyLog(log)
                _successMessage.value = "Auto-reply log deleted"
            } catch (e: Exception) {
                _error.value = "Failed to delete auto-reply log: ${e.message}"
            }
        }
    }
    
    fun deleteAllAutoReplyLogs() {
        viewModelScope.launch {
            try {
                autoReplyRepository.deleteAllAutoReplyLogs()
                _successMessage.value = "All auto-reply logs deleted"
            } catch (e: Exception) {
                _error.value = "Failed to delete all auto-reply logs: ${e.message}"
            }
        }
    }
    
    suspend fun hasRecentReply(contactId: String): Boolean {
        return autoReplyRepository.hasRecentReply(contactId)
    }
    
    suspend fun getLastSeenCall(): String? {
        return autoReplyRepository.getLastSeenCall()?.callId
    }
    
    suspend fun updateLastSeenCall(callId: String, contactId: String?) {
        autoReplyRepository.updateLastSeenCall(callId, contactId)
    }
    
    fun clearError() {
        _error.value = null
    }
    
    fun clearSuccessMessage() {
        _successMessage.value = null
    }

    // New APIs for delivery tracking and duplicate prevention
    suspend fun logAutoReplyReturnId(log: AutoReplyLogEntity): Long {
        return autoReplyRepository.logAutoReplyReturnId(log)
    }

    suspend fun getLogByPhoneAndDay(phone: String, dayKey: String): AutoReplyLogEntity? {
        return autoReplyRepository.getLogByPhoneAndDay(phone, dayKey)
    }

    suspend fun markLogSent(id: Long, attempts: Int, sentTs: Long = System.currentTimeMillis()) {
        autoReplyRepository.markLogSent(id = id, attempts = attempts, sentTs = sentTs)
    }

    suspend fun markLogDelivered(id: Long, deliveredTs: Long = System.currentTimeMillis()) {
        autoReplyRepository.markLogDelivered(id = id, deliveredTs = deliveredTs)
    }

    suspend fun getAutoReplyLogById(id: Long): AutoReplyLogEntity? {
        return autoReplyRepository.getAutoReplyLogById(id)
    }

    fun updateAllToDelivered() {
        viewModelScope.launch {
            try {
                autoReplyRepository.markAllAsDelivered(System.currentTimeMillis())
                _successMessage.value = "All messages updated to Delivered"
            } catch (e: Exception) {
                _error.value = "Failed to convert all to Delivered: ${e.message}"
            }
        }
    }
}
