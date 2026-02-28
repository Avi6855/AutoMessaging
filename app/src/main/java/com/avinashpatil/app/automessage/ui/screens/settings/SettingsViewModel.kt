package com.avinashpatil.app.automessage.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.avinashpatil.app.automessage.data.repository.AutoReplyRepository
import com.avinashpatil.app.automessage.data.repository.DataStoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val dataStoreRepository: DataStoreRepository,
    private val autoReplyRepository: AutoReplyRepository
) : ViewModel() {
    
    private val _autoReplyEnabled = MutableStateFlow(false)
    val autoReplyEnabled: StateFlow<Boolean> = _autoReplyEnabled.asStateFlow()
    
    private val _autoReplyDelay = MutableStateFlow(10)
    val autoReplyDelay: StateFlow<Int> = _autoReplyDelay.asStateFlow()
    
    private val _defaultMessageId = MutableStateFlow<Long?>(null)
    val defaultMessageId: StateFlow<Long?> = _defaultMessageId.asStateFlow()
    
    private val _darkMode = MutableStateFlow(false)
    val darkMode: StateFlow<Boolean> = _darkMode.asStateFlow()
    
    private val _isFirstTime = MutableStateFlow(true)
    val isFirstTime: StateFlow<Boolean> = _isFirstTime.asStateFlow()
    private val _notificationSound = MutableStateFlow(true)
    val notificationSound: StateFlow<Boolean> = _notificationSound.asStateFlow()
    
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
        viewModelScope.launch {
            try {
                // Load auto-reply settings
                dataStoreRepository.isAutoReplyEnabled().collect { enabled ->
                    _autoReplyEnabled.value = enabled
                }
                
                dataStoreRepository.getAutoReplyDelay().collect { delay ->
                    _autoReplyDelay.value = delay
                }
                
                dataStoreRepository.getDefaultMessageId().collect { messageId ->
                    _defaultMessageId.value = messageId
                }
                
                // Load appearance settings
                dataStoreRepository.isDarkModeEnabled().collect { darkMode ->
                    _darkMode.value = darkMode
                }
                
                // Load first-time status
                dataStoreRepository.isFirstTimeUser().collect { isFirstTime ->
                    _isFirstTime.value = isFirstTime
                }

                dataStoreRepository.isNotificationSoundEnabled().collect { enabled ->
                    _notificationSound.value = enabled
                }
            } catch (e: Exception) {
                _error.value = "Failed to load settings: ${e.message}"
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
    
    fun setDefaultMessage(messageId: Long) {
        viewModelScope.launch {
            try {
                dataStoreRepository.saveDefaultMessageId(messageId)
                _successMessage.value = "Default message updated"
                clearMessages()
            } catch (e: Exception) {
                _error.value = "Failed to update default message: ${e.message}"
            }
        }
    }
    
    fun setDarkMode(enabled: Boolean) {
        viewModelScope.launch {
            try {
                dataStoreRepository.saveDarkModeEnabled(enabled)
                _successMessage.value = "Dark mode ${if (enabled) "enabled" else "disabled"}"
                clearMessages()
            } catch (e: Exception) {
                _error.value = "Failed to update dark mode: ${e.message}"
            }
        }
    }

    fun setNotificationSound(enabled: Boolean) {
        viewModelScope.launch {
            try {
                dataStoreRepository.saveNotificationSoundEnabled(enabled)
                _successMessage.value = "Notification sound ${if (enabled) "enabled" else "disabled"}"
                clearMessages()
            } catch (e: Exception) {
                _error.value = "Failed to update notification sound: ${e.message}"
            }
        }
    }
    
    fun setFirstTime(isFirstTime: Boolean) {
        viewModelScope.launch {
            try {
                dataStoreRepository.saveFirstTimeUser(isFirstTime)
            } catch (e: Exception) {
                _error.value = "Failed to update first-time status: ${e.message}"
            }
        }
    }
    
    fun clearAllHistory() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                autoReplyRepository.deleteAllAutoReplyLogs()
                _successMessage.value = "All history cleared successfully"
                clearMessages()
            } catch (e: Exception) {
                _error.value = "Failed to clear history: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun clearMessages() {
        _error.value = null
        _successMessage.value = null
    }
}
