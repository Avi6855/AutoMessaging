package com.avinashpatil.app.automessage.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
class SettingsViewModel @Inject constructor(
    private val dataStoreRepository: DataStoreRepository
) : ViewModel() {
    
    private val _isAutoReplyEnabled = MutableStateFlow(false)
    val isAutoReplyEnabled: StateFlow<Boolean> = _isAutoReplyEnabled.asStateFlow()
    
    private val _autoReplyDelay = MutableStateFlow(10)
    val autoReplyDelay: StateFlow<Int> = _autoReplyDelay.asStateFlow()
    
    private val _isDarkModeEnabled = MutableStateFlow(false)
    val isDarkModeEnabled: StateFlow<Boolean> = _isDarkModeEnabled.asStateFlow()
    
    private val _isFirstTimeUser = MutableStateFlow(true)
    val isFirstTimeUser: StateFlow<Boolean> = _isFirstTimeUser.asStateFlow()
    
    private val _defaultMessageId = MutableStateFlow(-1L)
    val defaultMessageId: StateFlow<Long> = _defaultMessageId.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage.asStateFlow()
    
    init {
        loadSettings()
    }
    
    private fun loadSettings() {
        dataStoreRepository.isAutoReplyEnabled()
            .onEach { enabled ->
                _isAutoReplyEnabled.value = enabled
            }
            .catch { e ->
                _error.value = "Failed to load auto-reply setting: ${e.message}"
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
        
        dataStoreRepository.isDarkModeEnabled()
            .onEach { enabled ->
                _isDarkModeEnabled.value = enabled
            }
            .catch { e ->
                _error.value = "Failed to load dark mode setting: ${e.message}"
            }
            .launchIn(viewModelScope)
        
        dataStoreRepository.isFirstTimeUser()
            .onEach { isFirstTime ->
                _isFirstTimeUser.value = isFirstTime
            }
            .catch { e ->
                _error.value = "Failed to load first-time user setting: ${e.message}"
            }
            .launchIn(viewModelScope)
        
        dataStoreRepository.getDefaultMessageId()
            .onEach { messageId ->
                _defaultMessageId.value = messageId
            }
            .catch { e ->
                _error.value = "Failed to load default message ID: ${e.message}"
            }
            .launchIn(viewModelScope)
    }
    
    fun setAutoReplyEnabled(enabled: Boolean) {
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
    
    fun setAutoReplyDelay(delaySeconds: Int) {
        viewModelScope.launch {
            try {
                dataStoreRepository.saveAutoReplyDelay(delaySeconds)
                _autoReplyDelay.value = delaySeconds
                _successMessage.value = "Auto-reply delay set to $delaySeconds seconds"
            } catch (e: Exception) {
                _error.value = "Failed to update auto-reply delay: ${e.message}"
            }
        }
    }
    
    fun setDarkModeEnabled(enabled: Boolean) {
        viewModelScope.launch {
            try {
                dataStoreRepository.saveDarkModeEnabled(enabled)
                _isDarkModeEnabled.value = enabled
                _successMessage.value = if (enabled) "Dark mode enabled" else "Light mode enabled"
            } catch (e: Exception) {
                _error.value = "Failed to update dark mode setting: ${e.message}"
            }
        }
    }
    
    fun setDefaultMessageId(messageId: Long) {
        viewModelScope.launch {
            try {
                dataStoreRepository.saveDefaultMessageId(messageId)
                _defaultMessageId.value = messageId
                _successMessage.value = "Default message updated"
            } catch (e: Exception) {
                _error.value = "Failed to update default message: ${e.message}"
            }
        }
    }
    
    fun setFirstTimeUser(isFirstTime: Boolean) {
        viewModelScope.launch {
            try {
                dataStoreRepository.saveFirstTimeUser(isFirstTime)
                _isFirstTimeUser.value = isFirstTime
            } catch (e: Exception) {
                _error.value = "Failed to update first-time user setting: ${e.message}"
            }
        }
    }
    
    fun clearAllSettings() {
        viewModelScope.launch {
            try {
                dataStoreRepository.clearAllPreferences()
                loadSettings()
                _successMessage.value = "All settings cleared"
            } catch (e: Exception) {
                _error.value = "Failed to clear settings: ${e.message}"
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