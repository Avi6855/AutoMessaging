package com.avinashpatil.app.automessage.ui.screens.recent

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.avinashpatil.app.automessage.data.entity.AutoReplyLogEntity
import com.avinashpatil.app.automessage.data.repository.AutoReplyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecentViewModel @Inject constructor(
    private val autoReplyRepository: AutoReplyRepository
) : ViewModel() {
    
    private val _autoReplyHistory = MutableStateFlow<List<AutoReplyLogEntity>>(emptyList())
    val autoReplyHistory: StateFlow<List<AutoReplyLogEntity>> = _autoReplyHistory.asStateFlow()
    
    private val _searchResults = MutableStateFlow<List<AutoReplyLogEntity>>(emptyList())
    val searchResults: StateFlow<List<AutoReplyLogEntity>> = _searchResults.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage.asStateFlow()
    
    init {
        loadAutoReplyHistory()
    }
    
    private fun loadAutoReplyHistory() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                autoReplyRepository.getAutoReplyHistory()
                    .collect { logs ->
                        _autoReplyHistory.value = logs
                        _isLoading.value = false
                    }
            } catch (e: Exception) {
                _error.value = "Failed to load auto-reply history: ${e.message}"
                _isLoading.value = false
            }
        }
    }
    
    fun searchAutoReplyHistory(query: String) {
        viewModelScope.launch {
            try {
                autoReplyRepository.searchAutoReplyHistory(query)
                    .collect { results ->
                        _searchResults.value = results
                    }
            } catch (e: Exception) {
                _error.value = "Search failed: ${e.message}"
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
                _successMessage.value = "All auto-reply history cleared"
                clearMessages()
            } catch (e: Exception) {
                _error.value = "Failed to clear history: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun getAutoReplyHistoryByDateRange(startTime: Long, endTime: Long) {
        viewModelScope.launch {
            try {
                autoReplyRepository.getAutoReplyHistoryByDateRange(startTime, endTime).collect { logs ->
                    _autoReplyHistory.value = logs
                }
            } catch (e: Exception) {
                _error.value = "Failed to load logs by date range: ${e.message}"
            }
        }
    }
    
    fun getAutoReplyHistoryByContact(contactId: String) {
        viewModelScope.launch {
            try {
                autoReplyRepository.getAutoReplyHistoryByContact(contactId).collect { logs ->
                    _autoReplyHistory.value = logs
                }
            } catch (e: Exception) {
                _error.value = "Failed to load logs by contact: ${e.message}"
            }
        }
    }
    
    fun clearMessages() {
        _error.value = null
        _successMessage.value = null
    }
}