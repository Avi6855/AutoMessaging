package com.avinashpatil.app.automessage.ui.screens.groups

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.avinashpatil.app.automessage.data.entity.ContactEntity
import com.avinashpatil.app.automessage.data.entity.CustomMessageEntity
import com.avinashpatil.app.automessage.data.entity.GroupEntity
import com.avinashpatil.app.automessage.data.repository.ContactRepository
import com.avinashpatil.app.automessage.data.repository.GroupRepository
import com.avinashpatil.app.automessage.data.repository.MessageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import android.content.Context
import android.Manifest
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import android.telephony.SmsManager
import android.telephony.SubscriptionManager
import android.os.Build
import android.app.PendingIntent
import android.content.Intent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import android.content.BroadcastReceiver
import android.content.IntentFilter
import java.util.concurrent.atomic.AtomicLong

@HiltViewModel
class GroupDetailsViewModel @Inject constructor(
    private val application: Application,
    private val groupRepository: GroupRepository,
    private val contactRepository: ContactRepository,
    private val messageRepository: MessageRepository
) : ViewModel() {

    private val _group = MutableStateFlow<GroupEntity?>(null)
    val group: StateFlow<GroupEntity?> = _group.asStateFlow()

    private val _contacts = MutableStateFlow<List<ContactEntity>>(emptyList())
    val contacts: StateFlow<List<ContactEntity>> = _contacts.asStateFlow()

    private val _messages = MutableStateFlow<List<CustomMessageEntity>>(emptyList())
    val messages: StateFlow<List<CustomMessageEntity>> = _messages.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage.asStateFlow()

    private val TAG = "GroupDetailsViewModel"
    private val batchIdGen = AtomicLong(System.currentTimeMillis())
    private var smsSentReceiver: BroadcastReceiver? = null
    private var smsDeliveredReceiver: BroadcastReceiver? = null

    fun loadGroupDetails(groupId: Long) {
        // Start loading state but avoid blocking on Flow.collect
        _isLoading.value = true
        viewModelScope.launch {
            try {
                // Load group entity once
                _group.value = groupRepository.getGroupById(groupId)

                // Collect contacts in a separate coroutine to keep UI responsive
                viewModelScope.launch {
                    contactRepository.getContactsByGroup(groupId).collect { list ->
                        _contacts.value = list
                    }
                }

                // Collect messages similarly
                viewModelScope.launch {
                    messageRepository.getAllMessages().collect { list ->
                        _messages.value = list
                    }
                }
            } catch (e: Exception) {
                _error.value = "Failed to load group details: ${e.message}"
            } finally {
                // Mark initial load complete; flows continue updating state
                _isLoading.value = false
            }
        }
    }

    fun updateGroup(groupId: Long, newName: String, newDescription: String) {
        viewModelScope.launch {
            try {
                val currentGroup = _group.value ?: return@launch
                val updatedGroup = currentGroup.copy(
                    name = newName,
                    description = newDescription
                )
                groupRepository.updateGroup(updatedGroup)
                _group.value = updatedGroup
                _successMessage.value = "Group updated successfully"
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

    fun removeContactFromGroup(contactId: String, groupId: Long) {
        viewModelScope.launch {
            try {
                // Remove contact from group
                contactRepository.removeContactFromGroup(contactId)
                // Refresh contacts list
                contactRepository.getContactsByGroup(groupId).collect { contacts ->
                    _contacts.value = contacts
                }
                _successMessage.value = "Contact removed from group"
            } catch (e: Exception) {
                _error.value = "Failed to remove contact: ${e.message}"
            }
        }
    }

    fun sendMessageToGroup(contacts: List<ContactEntity>, message: CustomMessageEntity) {
        viewModelScope.launch {
            try {
                // Permission check
                val hasSms = ContextCompat.checkSelfPermission(
                    application,
                    Manifest.permission.SEND_SMS
                ) == PackageManager.PERMISSION_GRANTED
                if (!hasSms) {
                    _error.value = "Please enable SMS permissions to send messages."
                    return@launch
                }

                // Validate message body
                val content = message.body.trim()
                if (content.isEmpty()) {
                    _error.value = "⚠️ Message content is empty."
                    return@launch
                }

                _isLoading.value = true
                val groupName = _group.value?.name ?: "Unknown Group"

                // Register local receivers for delivery feedback per batch
                val batchId = batchIdGen.incrementAndGet()
                registerLocalSmsReceivers(batchId)

                // Send messages in background
                val successCount = sendMessagesToContacts(contacts, content, batchId)

                if (successCount == contacts.size && contacts.isNotEmpty()) {
                    _successMessage.value = "✅ Message successfully sent to all ${contacts.size} contacts in $groupName!"
                } else if (successCount > 0) {
                    _successMessage.value = "⚠️ Some messages couldn't be sent. $successCount/${contacts.size} messages sent successfully."
                } else {
                    _error.value = "⚠️ Failed to send messages. Please check contact details and permissions."
                }
            } catch (e: Exception) {
                _error.value = "Error sending messages: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private suspend fun sendMessagesToContacts(contacts: List<ContactEntity>, messageContent: String, batchId: Long): Int {
        return withContext(Dispatchers.IO) {
            var successCount = 0
            for (contact in contacts) {
                val phone = contact.phoneNumber?.trim().orEmpty()
                if (phone.isEmpty()) {
                    // Skip invalid numbers
                    continue
                }
                try {
                    sendSmsMessage(phone, messageContent, batchId)
                    successCount++
                } catch (e: Exception) {
                    // Log error but continue with other contacts
                    android.util.Log.e(
                        TAG,
                        "Failed to send message to ${contact.phoneNumber}: ${e.message}"
                    )
                }
            }
            successCount
        }
    }

    private fun sendSmsMessage(phoneNumber: String, message: String, batchId: Long) {
        try {
            val smsManager = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                application.getSystemService(SmsManager::class.java) ?: SmsManager.getDefault()
            } else {
                SmsManager.getDefault()
            }

            val parts = smsManager.divideMessage(message)

            if (parts.size > 1) {
                // Send multipart message
                val sentIntents = ArrayList<PendingIntent>()
                val deliveredIntents = ArrayList<PendingIntent>()

                for (i in parts.indices) {
                    val sentIntent = PendingIntent.getBroadcast(
                        application,
                        (batchId + i).toInt(),
                        Intent("SMS_SENT_GROUP").apply {
                            putExtra("phone", phoneNumber)
                            putExtra("batchId", batchId)
                            putExtra("partIndex", i)
                        },
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    )
                    val deliveredIntent = PendingIntent.getBroadcast(
                        application,
                        (batchId + i + 10_000).toInt(),
                        Intent("SMS_DELIVERED_GROUP").apply {
                            putExtra("phone", phoneNumber)
                            putExtra("batchId", batchId)
                            putExtra("partIndex", i)
                        },
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    )
                    sentIntents.add(sentIntent)
                    deliveredIntents.add(deliveredIntent)
                }

                smsManager.sendMultipartTextMessage(phoneNumber, null, parts, sentIntents, deliveredIntents)
            } else {
                // Send single message
                val sentIntent = PendingIntent.getBroadcast(
                    application,
                    (batchId).toInt(),
                    Intent("SMS_SENT_GROUP").apply {
                        putExtra("phone", phoneNumber)
                        putExtra("batchId", batchId)
                    },
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                val deliveredIntent = PendingIntent.getBroadcast(
                    application,
                    (batchId + 10_000).toInt(),
                    Intent("SMS_DELIVERED_GROUP").apply {
                        putExtra("phone", phoneNumber)
                        putExtra("batchId", batchId)
                    },
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )

                smsManager.sendTextMessage(phoneNumber, null, message, sentIntent, deliveredIntent)
            }
        } catch (e: Exception) {
            throw Exception("Failed to send SMS to $phoneNumber: ${e.message}")
        }
    }

    private fun registerLocalSmsReceivers(batchId: Long) {
        // Unregister any prior receivers to avoid duplicates
        try {
            smsSentReceiver?.let { application.unregisterReceiver(it) }
            smsDeliveredReceiver?.let { application.unregisterReceiver(it) }
        } catch (_: Exception) {}

        smsSentReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val bId = intent?.getLongExtra("batchId", -1L)
                if (bId != batchId) return
                val phone = intent?.getStringExtra("phone") ?: ""
                val result = resultCode
                android.util.Log.d(TAG, "[Batch=$batchId] SMS_SENT_GROUP to $phone result=$result")
            }
        }
        smsDeliveredReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val bId = intent?.getLongExtra("batchId", -1L)
                if (bId != batchId) return
                val phone = intent?.getStringExtra("phone") ?: ""
                val result = resultCode
                android.util.Log.d(TAG, "[Batch=$batchId] SMS_DELIVERED_GROUP to $phone result=$result")
            }
        }

        application.registerReceiver(smsSentReceiver, IntentFilter("SMS_SENT_GROUP"))
        application.registerReceiver(smsDeliveredReceiver, IntentFilter("SMS_DELIVERED_GROUP"))
    }

    fun clearError() {
        _error.value = null
    }

    fun clearSuccessMessage() {
        _successMessage.value = null
    }

    override fun onCleared() {
        super.onCleared()
        try {
            smsSentReceiver?.let { application.unregisterReceiver(it) }
            smsDeliveredReceiver?.let { application.unregisterReceiver(it) }
        } catch (_: Exception) {}
    }
}
