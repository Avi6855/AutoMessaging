package com.avinashpatil.app.automessage.ui.screens.settings

import android.content.Context
import android.os.Build
import android.content.Intent
import android.net.Uri
import android.os.PowerManager
import android.provider.Settings
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.avinashpatil.app.automessage.data.repository.AutoReplyRepository
import com.avinashpatil.app.automessage.data.repository.DataStoreRepository
import com.avinashpatil.app.automessage.service.AutoMessagingManager
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
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
    private val dataStoreRepository: DataStoreRepository,
    private val autoReplyRepository: AutoReplyRepository,
    private val autoMessagingManager: AutoMessagingManager,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _autoMessagingEnabled = MutableStateFlow(true)
    val autoMessagingEnabled: StateFlow<Boolean> = _autoMessagingEnabled.asStateFlow()

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

    private val _isBatteryOptimized = MutableStateFlow(true)
    val isBatteryOptimized: StateFlow<Boolean> = _isBatteryOptimized.asStateFlow()

    init {
        loadSettings()
        checkBatteryOptimization()
    }

    private fun loadSettings() {
        dataStoreRepository.isAutoMessagingEnabled()
            .onEach { enabled ->
                _autoMessagingEnabled.value = enabled
            }
            .catch { }
            .launchIn(viewModelScope)

        dataStoreRepository.isAutoReplyEnabled()
            .onEach { enabled ->
                _autoReplyEnabled.value = enabled
            }
            .catch { }
            .launchIn(viewModelScope)

        dataStoreRepository.getAutoReplyDelay()
            .onEach { delay ->
                _autoReplyDelay.value = delay
            }
            .catch { }
            .launchIn(viewModelScope)

        dataStoreRepository.getDefaultMessageId()
            .onEach { messageId ->
                _defaultMessageId.value = messageId
            }
            .catch { }
            .launchIn(viewModelScope)

        dataStoreRepository.isDarkModeEnabled()
            .onEach { darkMode ->
                _darkMode.value = darkMode
            }
            .catch { }
            .launchIn(viewModelScope)

        dataStoreRepository.isFirstTimeUser()
            .onEach { isFirstTime ->
                _isFirstTime.value = isFirstTime
            }
            .catch { }
            .launchIn(viewModelScope)

        dataStoreRepository.isNotificationSoundEnabled()
            .onEach { enabled ->
                _notificationSound.value = enabled
            }
            .catch { }
            .launchIn(viewModelScope)
    }

    private fun checkBatteryOptimization() {
        try {
            val pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager
            _isBatteryOptimized.value = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                !pm.isIgnoringBatteryOptimizations(context.packageName)
            } else {
                false
            }
        } catch (_: Exception) {
            _isBatteryOptimized.value = false
        }
    }

    fun setAutoMessagingEnabled(enabled: Boolean) {
        viewModelScope.launch {
            try {
                autoMessagingManager.setAutoMessagingEnabled(enabled)
                _autoMessagingEnabled.value = enabled
                _successMessage.value = if (enabled) "Auto messaging enabled" else "Auto messaging disabled"
                clearMessages()
            } catch (e: Exception) {
                _error.value = "Failed to update auto messaging: ${e.message}"
            }
        }
    }

    fun setAutoReplyEnabled(enabled: Boolean) {
        viewModelScope.launch {
            try {
                dataStoreRepository.saveAutoReplyEnabled(enabled)
                _autoReplyEnabled.value = enabled
                _successMessage.value = "Auto-reply ${if (enabled) "enabled" else "disabled"}"
                clearMessages()
            } catch (e: Exception) {
                _error.value = "Failed to update auto-reply: ${e.message}"
            }
        }
    }

    fun setAutoReplyDelay(delay: Int) {
        viewModelScope.launch {
            try {
                dataStoreRepository.saveAutoReplyDelay(delay)
                _autoReplyDelay.value = delay
                _successMessage.value = "Auto-reply delay set to $delay seconds"
                clearMessages()
            } catch (e: Exception) {
                _error.value = "Failed to update delay: ${e.message}"
            }
        }
    }

    fun setDarkMode(enabled: Boolean) {
        viewModelScope.launch {
            try {
                dataStoreRepository.saveDarkModeEnabled(enabled)
                _darkMode.value = enabled
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
                _notificationSound.value = enabled
                _successMessage.value = "Notification sound ${if (enabled) "enabled" else "disabled"}"
                clearMessages()
            } catch (e: Exception) {
                _error.value = "Failed to update notification sound: ${e.message}"
            }
        }
    }

    fun openBatteryOptimizationSettings() {
        try {
            val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
                data = Uri.parse("package:${context.packageName}")
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        } catch (_: Exception) {
            try {
                val intent = Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
            } catch (_: Exception) {}
        }
    }

    fun openAppSettings() {
        try {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.parse("package:${context.packageName}")
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        } catch (_: Exception) {}
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
