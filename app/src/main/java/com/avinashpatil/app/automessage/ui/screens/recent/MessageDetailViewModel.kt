package com.avinashpatil.app.automessage.ui.screens.recent

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.avinashpatil.app.automessage.data.entity.AutoReplyLogEntity
import com.avinashpatil.app.automessage.data.repository.AutoReplyRepository

@HiltViewModel
class MessageDetailViewModel @Inject constructor(
    private val repository: AutoReplyRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _log = MutableStateFlow<AutoReplyLogEntity?>(null)
    val log: StateFlow<AutoReplyLogEntity?> = _log

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    init {
        val id: Long? = savedStateHandle["logId"]
        if (id != null) {
            loadLog(id)
        } else {
            _error.value = "Invalid log id"
        }
    }

    fun loadLog(id: Long) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _log.value = repository.getAutoReplyLogById(id)
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }
}