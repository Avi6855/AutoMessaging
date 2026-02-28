package com.avinashpatil.app.automessage.ui.screens.recent

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.Scaffold
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavHostController
import com.avinashpatil.app.automessage.data.entity.AutoReplyLogEntity
import com.avinashpatil.app.automessage.ui.components.GlassCard
import com.avinashpatil.app.automessage.ui.components.StandardTopAppBar
import com.avinashpatil.app.automessage.ui.screens.messages.MessagesViewModel
import com.avinashpatil.app.automessage.ui.viewmodel.AutoReplyViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.launch

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.compose.material.icons.automirrored.filled.CallMade
import androidx.compose.material.icons.automirrored.filled.CallMissed
import androidx.compose.material.icons.automirrored.filled.CallReceived

import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TextField
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.core.tween
import androidx.compose.animation.Crossfade
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.ui.graphics.Brush
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.ui.draw.shadow
import com.avinashpatil.app.automessage.ui.theme.NeoLightBackground
import com.avinashpatil.app.automessage.ui.theme.NeoSurface
import com.avinashpatil.app.automessage.ui.theme.NeoPrimaryText
import com.avinashpatil.app.automessage.ui.theme.NeoSecondaryText
import com.avinashpatil.app.automessage.ui.theme.NeoAccent
import com.avinashpatil.app.automessage.ui.theme.NeumorphicSurface
import com.avinashpatil.app.automessage.ui.theme.NeumorphicCard
import com.avinashpatil.app.automessage.ui.theme.NeumorphicButton
import com.avinashpatil.app.automessage.ui.theme.NeumorphicTab
import com.avinashpatil.app.automessage.ui.theme.NeumorphicSearchField
import com.avinashpatil.app.automessage.ui.theme.NeumorphicBadge
import com.avinashpatil.app.automessage.ui.theme.NeumorphicSwitch
import androidx.compose.material.icons.filled.MoreVert
import com.avinashpatil.app.automessage.workers.AutoReplyHistoryClearWorker

private fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
    return sdf.format(Date(timestamp))
}

private fun formatDateOnly(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    return sdf.format(Date(timestamp))
}

private fun formatTime(timestamp: Long): String {
    val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
    return sdf.format(Date(timestamp))
}

private fun formatTimeAgo(timestamp: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - timestamp
    
    val seconds = diff / 1000
    val minutes = seconds / 60
    val hours = minutes / 60
    val days = hours / 24
    
    return when {
        days > 0 -> "${days} day${if (days > 1) "s" else ""} ago"
        hours > 0 -> "${hours} hour${if (hours > 1) "s" else ""} ago"
        minutes > 0 -> "${minutes} minute${if (minutes > 1) "s" else ""} ago"
        else -> "just now"
    }
}

@androidx.compose.ui.tooling.preview.Preview
@Composable
fun RecentScreenPreview() {
    val navController = rememberNavController()
    RecentScreen(navController = navController)
}

@Composable
private fun GlassSnackbar(message: String) {
    NeumorphicCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Info, 
                contentDescription = null, 
                tint = NeoAccent
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = message, 
                style = MaterialTheme.typography.bodyMedium,
                color = NeoPrimaryText
            )
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview
@Composable
private fun GlassSnackbarPreview() {
    GlassSnackbar(message = "This is a sample message")
}

@androidx.compose.ui.tooling.preview.Preview
@Composable
private fun CallLogsListPreview() {
    val logs = listOf(
        DeviceCallLog(
            id = 1,
            name = "John Doe",
            number = "123-456-7890",
            type = android.provider.CallLog.Calls.INCOMING_TYPE,
            date = System.currentTimeMillis(),
            durationSec = 60
        ),
        DeviceCallLog(
            id = 2,
            name = "Jane Smith",
            number = "098-765-4321",
            type = android.provider.CallLog.Calls.OUTGOING_TYPE,
            date = System.currentTimeMillis() - 86400000,
            durationSec = 120
        ),
        DeviceCallLog(
            id = 3,
            name = "Missed Call",
            number = "555-555-5555",
            type = android.provider.CallLog.Calls.MISSED_TYPE,
            date = System.currentTimeMillis() - 172800000,
            durationSec = 0
        )
    )
    CallLogsList(logs = logs, highlightedIds = setOf(1L))
}

@androidx.compose.ui.tooling.preview.Preview
@Composable
private fun EmptyStatePreview() {
    EmptyState()
}

@androidx.compose.ui.tooling.preview.Preview
@Composable
private fun AutoReplyHistoryListPreview() {
    val history = listOf(
        AutoReplyLogEntity(
            id = 1,
            contactId = "1",
            contactName = "John Doe",
            phoneNumber = "123-456-7890",
            messageText = "Sorry, I missed your call. I will call you back later.",
            timestamp = System.currentTimeMillis(),
            dayKey = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()),
            callType = "INCOMING",
            isAutoReply = true,
            status = "DELIVERED"
        ),
        AutoReplyLogEntity(
            id = 2,
            contactId = "2",
            contactName = "Jane Smith",
            phoneNumber = "098-765-4321",
            messageText = "Can't talk right now. What's up?",
            timestamp = System.currentTimeMillis() - 3600000,
            dayKey = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()),
            callType = "INCOMING",
            isAutoReply = true,
            status = "SENT"
        ),
        AutoReplyLogEntity(
            id = 3,
            contactId = "3",
            contactName = "Unknown Number",
            phoneNumber = "555-555-5555",
            messageText = "This is an automated message. Please do not reply.",
            timestamp = System.currentTimeMillis() - 7200000,
            dayKey = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()),
            callType = "INCOMING",
            isAutoReply = true,
            status = "FAILED"
        ),
         AutoReplyLogEntity(
            id = 4,
            contactId = "4",
            contactName = "Pending Message",
            phoneNumber = "111-222-3333",
            messageText = "I will get back to you shortly.",
            timestamp = System.currentTimeMillis() - 10800000,
            dayKey = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()),
            callType = "INCOMING",
            isAutoReply = true,
            status = "PENDING"
        )
    )
    AutoReplyHistoryList(
        history = history,
        onDeleteItem = {},
        onClickItem = {}
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecentScreen(
    viewModel: RecentViewModel = hiltViewModel(),
    navController: NavHostController
) {
    val autoReplyHistory by viewModel.autoReplyHistory.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val nc = navController
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var confirmDeleteLog by remember { mutableStateOf<AutoReplyLogEntity?>(null) }

    // Wire default message and auto-reply logger
    val messagesViewModel: MessagesViewModel = hiltViewModel()
    val autoReplyViewModel: AutoReplyViewModel = hiltViewModel()
    val defaultMessage by messagesViewModel.defaultMessage.collectAsState()
    val isAutoReplyEnabled by autoReplyViewModel.isAutoReplyEnabled.collectAsState()
    // Collect error/success to show snackbars
    val error by viewModel.error.collectAsState()
    val successMessage by viewModel.successMessage.collectAsState()
    
    // Get last auto-clear time
    val context = androidx.compose.ui.platform.LocalContext.current
    val lastClearTime by remember { 
        mutableStateOf(AutoReplyHistoryClearWorker.getLastClearTime(context))
    }

    // Show one-time toast after midnight auto-clear
    var showAutoClearToast by remember { mutableStateOf(false) }
    androidx.compose.runtime.LaunchedEffect(Unit) {
        if (AutoReplyHistoryClearWorker.shouldShowToast(context)) {
            showAutoClearToast = true
        }
    }

    // Permission & call logs state
    var hasCallLogPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CALL_LOG) == PackageManager.PERMISSION_GRANTED
        )
    }
    var hasPhoneStatePermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED
        )
    }
    var hasSmsPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED
        )
    }

    // Tabs & search state
    var selectedTab by remember { mutableStateOf(0) } // 0: All Call Logs, 1: Auto Messages
    var isSearching by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var debouncedQuery by remember { mutableStateOf("") }
    var hasNotificationPermission by remember {
        mutableStateOf(
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                androidx.core.content.ContextCompat.checkSelfPermission(
                    context,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) == android.content.pm.PackageManager.PERMISSION_GRANTED
            } else true
        )
    }
    val requestNotificationPermission = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasNotificationPermission = granted
    }

    // Debounce search query updates for smoother filtering
    androidx.compose.runtime.LaunchedEffect(searchQuery) {
        kotlinx.coroutines.delay(300)
        debouncedQuery = searchQuery.trim()
    }

    // Show snackbars for error/success
    androidx.compose.runtime.LaunchedEffect(error) {
        error?.let {
            scope.launch { snackbarHostState.showSnackbar(it) }
            viewModel.clearMessages()
        }
    }
    androidx.compose.runtime.LaunchedEffect(successMessage) {
        successMessage?.let {
            scope.launch { snackbarHostState.showSnackbar(it) }
            viewModel.clearMessages()
        }
    }


    val requiredPermissions = arrayOf(
        Manifest.permission.READ_CALL_LOG,
        Manifest.permission.READ_PHONE_STATE,
        Manifest.permission.SEND_SMS
    )
    // Optional improvements for certain OEMs (Android 10–14), may be restricted on 15
    val optionalSmsPermissions = arrayOf(
        Manifest.permission.RECEIVE_SMS,
        Manifest.permission.READ_SMS
    )

    var deviceCallLogs by remember { mutableStateOf<List<DeviceCallLog>>(emptyList()) }
    var loadDeviceCallLogsLambda: (() -> Unit)? = null

    val requestPermissionsLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { results ->
        hasCallLogPermission = results[Manifest.permission.READ_CALL_LOG] == true
        hasPhoneStatePermission = results[Manifest.permission.READ_PHONE_STATE] == true
        hasSmsPermission = results[Manifest.permission.SEND_SMS] == true
        // Optional: supporting permissions status
        val readSmsGranted = results[Manifest.permission.READ_SMS] == true
        val receiveSmsGranted = results[Manifest.permission.RECEIVE_SMS] == true
        if (hasCallLogPermission && hasPhoneStatePermission && hasSmsPermission) {
            try {
                android.widget.Toast.makeText(context, "✅ SMS permission granted successfully.", android.widget.Toast.LENGTH_SHORT).show()
            } catch (_: Exception) { }
        }
        if (hasCallLogPermission) {
            loadDeviceCallLogsLambda?.invoke()
        }
    }

    // moved DeviceCallLog to top-level

    loadDeviceCallLogsLambda = {
        try {
            val uri = android.provider.CallLog.Calls.CONTENT_URI
            val projection = arrayOf(
                android.provider.CallLog.Calls._ID,
                android.provider.CallLog.Calls.CACHED_NAME,
                android.provider.CallLog.Calls.NUMBER,
                android.provider.CallLog.Calls.TYPE,
                android.provider.CallLog.Calls.DATE,
                android.provider.CallLog.Calls.DURATION
            )
            val sortOrder = android.provider.CallLog.Calls.DATE + " DESC"
            val cursor = context.contentResolver.query(uri, projection, null, null, sortOrder)
            val logs = mutableListOf<DeviceCallLog>()
            cursor?.use { c ->
                val idIdx = c.getColumnIndexOrThrow(android.provider.CallLog.Calls._ID)
                val nameIdx = c.getColumnIndexOrThrow(android.provider.CallLog.Calls.CACHED_NAME)
                val numberIdx = c.getColumnIndexOrThrow(android.provider.CallLog.Calls.NUMBER)
                val typeIdx = c.getColumnIndexOrThrow(android.provider.CallLog.Calls.TYPE)
                val dateIdx = c.getColumnIndexOrThrow(android.provider.CallLog.Calls.DATE)
                val durIdx = c.getColumnIndexOrThrow(android.provider.CallLog.Calls.DURATION)
                while (c.moveToNext()) {
                    logs.add(
                        DeviceCallLog(
                            id = c.getLong(idIdx),
                            name = c.getString(nameIdx),
                            number = c.getString(numberIdx) ?: "",
                            type = c.getInt(typeIdx),
                            date = c.getLong(dateIdx),
                            durationSec = c.getInt(durIdx)
                        )
                    )
                }
            }
            deviceCallLogs = logs
        } catch (_: Exception) {
            // ignore for UI resilience
        }
    }

    // Highlight newly added answered calls
    var highlightedIds by remember { mutableStateOf<Set<Long>>(emptySet()) }

    // Instant call log fetching on screen load
    androidx.compose.runtime.LaunchedEffect(Unit) {
        if (hasCallLogPermission) {
            loadDeviceCallLogsLambda?.invoke()
        }
    }

    fun sendSms(number: String, text: String, contactName: String?, callType: String) {
        try {
            val smsManager: android.telephony.SmsManager = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                context.getSystemService(android.telephony.SmsManager::class.java) ?: android.telephony.SmsManager.getDefault()
            } else {
                android.telephony.SmsManager.getDefault()
            }

            val ts = System.currentTimeMillis()
            val dayKey = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date(ts))
            // Prevent duplicates by unique phone+dayKey
            var logId = -1L
            var attempts = 1
            val preLog = com.avinashpatil.app.automessage.data.entity.AutoReplyLogEntity(
                contactId = number,
                contactName = contactName ?: number,
                phoneNumber = number,
                messageText = text,
                timestamp = ts,
                dayKey = dayKey,
                callType = callType,
                isAutoReply = true,
                status = "PENDING",
                attempts = 0,
                error = null,
                sentTimestamp = null,
                deliveredTimestamp = null
            )
            try {
                // Use ViewModel repo helpers for logging
                // Note: running in UI thread; lightweight Room insert OK, otherwise offload to scope
                kotlinx.coroutines.runBlocking {
                    logId = autoReplyViewModel.logAutoReplyReturnId(preLog)
                }
            } catch (e: Exception) {
                if (e is android.database.sqlite.SQLiteConstraintException) {
                    // Duplicate for phone+dayKey detected; respect existing record
                    val existing = kotlinx.coroutines.runBlocking { autoReplyViewModel.getLogByPhoneAndDay(number, dayKey) }
                    if (existing != null) {
                        when (existing.status) {
                            "DELIVERED", "SENT", "PENDING" -> return
                            "FAILED" -> {
                                attempts = (existing.attempts + 1).coerceAtLeast(1)
                                logId = existing.id
                            }
                            else -> return
                        }
                    } else {
                        return
                    }
                } else {
                    return
                }
            }

            val parts = smsManager.divideMessage(text)
            val ACTION_SMS_SENT = "com.avinashpatil.app.automessage.SMS_SENT"
            val ACTION_SMS_DELIVERED = "com.avinashpatil.app.automessage.SMS_DELIVERED"
            if (parts.size > 1) {
                val sentIntents = java.util.ArrayList<android.app.PendingIntent>()
                val deliverIntents = java.util.ArrayList<android.app.PendingIntent>()
                for (i in parts.indices) {
                    val sentIntent = android.app.PendingIntent.getBroadcast(
                        context,
                        i,
                        android.content.Intent(ACTION_SMS_SENT).apply {
                            setPackage(context.packageName)
                            putExtra("log_id", logId)
                            putExtra("attempts", attempts)
                            putExtra("phone", number)
                        },
                        android.app.PendingIntent.FLAG_UPDATE_CURRENT or android.app.PendingIntent.FLAG_IMMUTABLE
                    )
                    val deliveredIntent = android.app.PendingIntent.getBroadcast(
                        context,
                        i,
                        android.content.Intent(ACTION_SMS_DELIVERED).apply {
                            setPackage(context.packageName)
                            putExtra("log_id", logId)
                            putExtra("phone", number)
                        },
                        android.app.PendingIntent.FLAG_UPDATE_CURRENT or android.app.PendingIntent.FLAG_IMMUTABLE
                    )
                    sentIntents.add(sentIntent)
                    deliverIntents.add(deliveredIntent)
                }
                smsManager.sendMultipartTextMessage(number, null, parts, sentIntents, deliverIntents)
            } else {
                val sentIntent = android.app.PendingIntent.getBroadcast(
                    context,
                    0,
                    android.content.Intent(ACTION_SMS_SENT).apply {
                        setPackage(context.packageName)
                        putExtra("log_id", logId)
                        putExtra("attempts", attempts)
                        putExtra("phone", number)
                    },
                    android.app.PendingIntent.FLAG_UPDATE_CURRENT or android.app.PendingIntent.FLAG_IMMUTABLE
                )
                val deliveredIntent = android.app.PendingIntent.getBroadcast(
                    context,
                    0,
                    android.content.Intent(ACTION_SMS_DELIVERED).apply {
                        setPackage(context.packageName)
                        putExtra("log_id", logId)
                        putExtra("phone", number)
                    },
                    android.app.PendingIntent.FLAG_UPDATE_CURRENT or android.app.PendingIntent.FLAG_IMMUTABLE
                )
                smsManager.sendTextMessage(number, null, text, sentIntent, deliveredIntent)
            }
        } catch (_: Exception) {
            // swallow to avoid crashing UI; background service receivers handle failures
        }
    }

    // Register ContentObserver to detect new answered calls and auto-send default message
    val handler: android.os.Handler = remember { android.os.Handler(android.os.Looper.getMainLooper()) }
    val callLogObserver: android.database.ContentObserver = remember(handler, defaultMessage, hasSmsPermission) {
        object : android.database.ContentObserver(handler) {
            override fun onChange(selfChange: Boolean) {
                if (!hasCallLogPermission || !hasPhoneStatePermission || !hasSmsPermission) {
                    val msg = buildString {
                        if (!hasCallLogPermission) append("Missing READ_CALL_LOG. ")
                        if (!hasPhoneStatePermission) append("Missing READ_PHONE_STATE. ")
                        if (!hasSmsPermission) append("Missing SEND_SMS. ")
                        append("Please grant required permissions.")
                    }
                    scope.launch { snackbarHostState.showSnackbar(msg) }
                    return
                }
                
                // Refresh call logs list immediately when new calls are detected
                loadDeviceCallLogsLambda?.invoke()
                val uri = android.provider.CallLog.Calls.CONTENT_URI
                val projection = arrayOf(
                    android.provider.CallLog.Calls._ID,
                    android.provider.CallLog.Calls.CACHED_NAME,
                    android.provider.CallLog.Calls.NUMBER,
                    android.provider.CallLog.Calls.TYPE,
                    android.provider.CallLog.Calls.DATE,
                    android.provider.CallLog.Calls.DURATION
                )
                val sortOrder = android.provider.CallLog.Calls.DATE + " DESC"
                val cursor = context.contentResolver.query(uri, projection, null, null, sortOrder)
                cursor?.use { c ->
                    if (c.moveToFirst()) {
                        val id = c.getLong(c.getColumnIndexOrThrow(android.provider.CallLog.Calls._ID))
                        val name = c.getString(c.getColumnIndexOrThrow(android.provider.CallLog.Calls.CACHED_NAME))
                        val number = c.getString(c.getColumnIndexOrThrow(android.provider.CallLog.Calls.NUMBER)) ?: ""
                        val type = c.getInt(c.getColumnIndexOrThrow(android.provider.CallLog.Calls.TYPE))
                        val duration = c.getInt(c.getColumnIndexOrThrow(android.provider.CallLog.Calls.DURATION))
                        val isAnswered = ((type == android.provider.CallLog.Calls.INCOMING_TYPE || type == android.provider.CallLog.Calls.OUTGOING_TYPE) && duration > 0)
                        val isMissed = type == android.provider.CallLog.Calls.MISSED_TYPE
                        
                        // Handle missed calls - send auto message immediately
                        if (isMissed && defaultMessage != null && number.isNotBlank()) {
                            scope.launch {
                                val idStr = id.toString()
                                val canProcess = com.avinashpatil.app.automessage.utils.DuplicatePreventer.shouldProcess(
                                    context,
                                    callId = idStr,
                                    phoneNumber = number,
                                    windowMs = 30_000
                                )
                                if (canProcess) {
                                    sendSms(number, defaultMessage!!.body, name ?: number, "MISSED")
                                    com.avinashpatil.app.automessage.utils.DuplicatePreventer.markProcessed(context, idStr, number)
                                    autoReplyViewModel.updateLastSeenCall(idStr, idStr)
                                    highlightedIds = highlightedIds + id
                                    android.widget.Toast.makeText(context, "Auto message sent to ${name ?: number} for missed call.", android.widget.Toast.LENGTH_SHORT).show()
                                    kotlinx.coroutines.delay(2000)
                                    highlightedIds = highlightedIds - id
                                }
                            }
                        }
                        // Handle answered calls - schedule daily auto message (once per day)
                        else if (isAnswered && defaultMessage != null && number.isNotBlank()) {
                            scope.launch {
                                val idStr = id.toString()
                                // Check if we've already sent a message to this contact today
                                val hasSentToday = com.avinashpatil.app.automessage.utils.DailyMessageTracker.hasSentToday(context, number)
                                if (!hasSentToday) {
                                    val canProcess = com.avinashpatil.app.automessage.utils.DuplicatePreventer.shouldProcess(
                                        context,
                                        callId = idStr,
                                        phoneNumber = number,
                                        windowMs = 30_000
                                    )
                                    if (canProcess) {
                                        val callTypeStr = if (type == android.provider.CallLog.Calls.INCOMING_TYPE) "INCOMING" else "OUTGOING"
                                        sendSms(number, defaultMessage!!.body, name ?: number, callTypeStr)
                                        com.avinashpatil.app.automessage.utils.DuplicatePreventer.markProcessed(context, idStr, number)
                                        com.avinashpatil.app.automessage.utils.DailyMessageTracker.markSentToday(context, number)
                                        autoReplyViewModel.updateLastSeenCall(idStr, idStr)
                                        highlightedIds = highlightedIds + id
                                        android.widget.Toast.makeText(context, "Daily auto message sent to ${name ?: number}.", android.widget.Toast.LENGTH_SHORT).show()
                                        kotlinx.coroutines.delay(2000)
                                        highlightedIds = highlightedIds - id
                                    }
                                }
                            }
                        } else if (defaultMessage == null) {
                            scope.launch { snackbarHostState.showSnackbar("Please set a default message to auto-send.") }
                        }
                    }
                }
            }
        }
    }

    // Manage observer lifecycle with permissions and tab selection
    androidx.compose.runtime.DisposableEffect(selectedTab, hasCallLogPermission) {
        if (selectedTab == 0 && hasCallLogPermission) {
            try { context.contentResolver.registerContentObserver(android.provider.CallLog.Calls.CONTENT_URI, true, callLogObserver) } catch (_: Exception) {}
        }
        onDispose {
            try { context.contentResolver.unregisterContentObserver(callLogObserver) } catch (_: Exception) {}
        }
    }

    // Popup state for in-app notifications
    var popupLog by remember { mutableStateOf<AutoReplyLogEntity?>(null) }
    var popupType by remember { mutableStateOf("DELIVERED") }

    // Show popup when SMS delivered successfully (real-time UI feedback)
    val deliveredReceiver = remember {
        object : android.content.BroadcastReceiver() {
            override fun onReceive(c: android.content.Context?, intent: android.content.Intent?) {
                val result = resultCode
                if (result == android.app.Activity.RESULT_OK) {
                    val logId = intent?.getLongExtra("log_id", -1L) ?: -1L
                    if (logId > 0) {
                        // Mark delivered in DB to ensure UI updates even if service isn't running
                        scope.launch { autoReplyViewModel.markLogDelivered(logId) }
                        scope.launch {
                            try {
                                val log = autoReplyViewModel.getAutoReplyLogById(logId)
                                popupType = "DELIVERED"
                                popupLog = log
                            } catch (_: Exception) { }
                        }
                    }
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            message = "Auto message sent successfully ✅",
                            withDismissAction = true,
                            duration = androidx.compose.material3.SnackbarDuration.Short
                        )
                    }
                }
            }
        }
    }
    // Show popup when SMS sent successfully
    val sentReceiver = remember {
        object : android.content.BroadcastReceiver() {
            override fun onReceive(c: android.content.Context?, intent: android.content.Intent?) {
                val result = resultCode
                val logId = intent?.getLongExtra("log_id", -1L) ?: -1L
                val attempts = intent?.getIntExtra("attempts", 1) ?: 1
                if (result == android.app.Activity.RESULT_OK && logId > 0) {
                    scope.launch { autoReplyViewModel.markLogSent(logId, attempts) }
                    scope.launch { autoReplyViewModel.updateAllToDelivered() }
                    scope.launch {
                        try {
                            val log = autoReplyViewModel.getAutoReplyLogById(logId)
                            popupType = "SENT"
                            popupLog = log
                        } catch (_: Exception) { }
                    }
                }
            }
        }
    }
    androidx.compose.runtime.DisposableEffect(Unit) {
        val filterDelivered = android.content.IntentFilter("com.avinashpatil.app.automessage.SMS_DELIVERED")
        val filterSent = android.content.IntentFilter("com.avinashpatil.app.automessage.SMS_SENT")
        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                context.registerReceiver(deliveredReceiver, filterDelivered, android.content.Context.RECEIVER_NOT_EXPORTED)
                context.registerReceiver(sentReceiver, filterSent, android.content.Context.RECEIVER_NOT_EXPORTED)
            } else {
                context.registerReceiver(deliveredReceiver, filterDelivered)
                context.registerReceiver(sentReceiver, filterSent)
            }
        } catch (_: Exception) { }
        onDispose {
            try { context.unregisterReceiver(deliveredReceiver) } catch (_: Exception) { }
            try { context.unregisterReceiver(sentReceiver) } catch (_: Exception) { }
        }
    }

    // Warn user clearly when permissions missing
    androidx.compose.runtime.LaunchedEffect(selectedTab, hasCallLogPermission, hasPhoneStatePermission, hasSmsPermission) {
        if (selectedTab == 0 && (!hasCallLogPermission || !hasPhoneStatePermission || !hasSmsPermission)) {
            scope.launch {
                val result = snackbarHostState.showSnackbar(
                    message = "Unable to send automatic messages. Permissions required.",
                    actionLabel = "Grant",
                    withDismissAction = true,
                    duration = androidx.compose.material3.SnackbarDuration.Indefinite
                )
                if (result == androidx.compose.material3.SnackbarResult.ActionPerformed) {
                    // Request core permissions first
                    requestPermissionsLauncher.launch(requiredPermissions)
                    // Try requesting optional ones on devices where they are available
                    try {
                        if (android.os.Build.VERSION.SDK_INT <= android.os.Build.VERSION_CODES.UPSIDE_DOWN_CAKE) { // Android 14 or below
                            requestPermissionsLauncher.launch(optionalSmsPermissions)
                        }
                    } catch (_: Exception) { }
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU && !hasNotificationPermission) {
                        try { requestNotificationPermission.launch(android.Manifest.permission.POST_NOTIFICATIONS) } catch (_: Exception) { }
                    }
                }
            }
        }
    }

    // Proactively request SMS app role on Android 10+ when permissions are missing
    LaunchedEffect(hasSmsPermission) {
        if (!hasSmsPermission && android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            try {
                val rm = context.getSystemService(android.app.role.RoleManager::class.java)
                if (rm.isRoleAvailable(android.app.role.RoleManager.ROLE_SMS) && !rm.isRoleHeld(android.app.role.RoleManager.ROLE_SMS)) {
                    val intent = rm.createRequestRoleIntent(android.app.role.RoleManager.ROLE_SMS)
                    context.startActivity(intent)
                }
            } catch (_: Exception) { }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        NeoLightBackground,
                        NeoSurface
                    )
                )
            )
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                StandardTopAppBar(
                    title = "Auto Messaging",
                    modifier = Modifier.padding(top = 8.dp, start = 8.dp, end = 8.dp),
                    actions = {

                        if (selectedTab == 1) {
                            IconButton(onClick = { isSearching = !isSearching }) {
                                Icon(
                                    Icons.Default.Search,
                                    contentDescription = "Search",
                                    tint = NeoPrimaryText
                                )
                            }
                        }
                        }
                        /*
                        // Auto-reply status indicator and toggle
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            // Status indicator dot
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(
                                        color = if (isAutoReplyEnabled && defaultMessage != null) {
                                            androidx.compose.ui.graphics.Color(0xFF4CAF50) // Green for active
                                        } else if (defaultMessage == null) {
                                            androidx.compose.ui.graphics.Color(0xFFFF9800) // Orange for no message
                                        } else {
                                            androidx.compose.ui.graphics.Color(0xFF9E9E9E) // Gray for disabled
                                        },
                                        shape = CircleShape
                                    )
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isAutoReplyEnabled && defaultMessage != null) "Auto ON" else if (defaultMessage == null) "No Msg" else "Auto OFF",
                                style = MaterialTheme.typography.labelSmall,
                                color = NeoSecondaryText,
                                modifier = Modifier.padding(end = 8.dp)
                            )
                        }
                        
                        // Auto-reply toggle switch
                        NeumorphicSwitch(
                            checked = isAutoReplyEnabled,
                            onCheckedChange = { enabled ->
                                if (defaultMessage != null) {
                                    autoReplyViewModel.toggleAutoReply(enabled)
                                }
                            },
                            modifier = Modifier.padding(end = 8.dp)
                        )

                        if (selectedTab == 1) {
                            IconButton(onClick = { isSearching = !isSearching }) {
                                Icon(
                                    Icons.Default.Search,
                                    contentDescription = "Search",
                                    tint = NeoPrimaryText
                                )
                            }
                        }



                    }

                     */

                )
            },
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState) { data: SnackbarData ->
                    GlassSnackbar(message = data.visuals.message)
                }
            },
            modifier = Modifier
                .fillMaxSize()
                .background(NeoLightBackground)
                .windowInsetsPadding(WindowInsets.statusBars)
            .windowInsetsPadding(WindowInsets.navigationBars)
        ) { paddingValues ->
        // Replace GradientBackground with direct layout to remove gradients
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            // Enhanced tabs with neumorphic styling
            NeumorphicCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                cornerRadius = 20.dp,
                elevation = 4.dp,
                backgroundColor = NeoSurface
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp),
                    //horizontalArrangement = Arrangement.SpaceEvenly,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    NeumorphicTab(
                        text = "Call Logs",
                        isSelected = selectedTab == 0,
                        onClick = {
                            selectedTab = 0
                            isSearching = false
                            if (!hasCallLogPermission || !hasPhoneStatePermission || !hasSmsPermission) {
                                requestPermissionsLauncher.launch(requiredPermissions)
                            } else {
                                loadDeviceCallLogsLambda?.invoke()
                            }
                        },
                        modifier = Modifier.weight(1f)
                    )
                    NeumorphicTab(
                        text = "Auto Messages",
                        isSelected = selectedTab == 1,
                        onClick = {
                            selectedTab = 1
                            isSearching = false
                            searchQuery = ""
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Enhanced search bar with neumorphic styling
            AnimatedVisibility(
                visible = selectedTab == 1 && isSearching,
                enter = fadeIn(animationSpec = tween(200)) + expandVertically(),
                exit = fadeOut(animationSpec = tween(200)) + shrinkVertically()
            ) {
                NeumorphicSearchField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = "Search messages...",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )
            }

            // Content by tab with crossfade
            Crossfade(targetState = selectedTab, animationSpec = tween(durationMillis = 250)) { tab ->
                when (tab) {
                    0 -> {
                        // All Call Logs tab content
                        if (!hasCallLogPermission) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                NeumorphicCard(
                                    modifier = Modifier.padding(24.dp)
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier.padding(24.dp)
                                    ) {
                                        Text(
                                            text = "Permissions Required",
                                            style = MaterialTheme.typography.titleMedium,
                                            color = NeoPrimaryText
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = "Grant permissions to show call logs and send messages",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = NeoSecondaryText,
                                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                        )
                                        Spacer(modifier = Modifier.height(16.dp))
                                        NeumorphicButton(
                                            text = "Grant Permissions",
                                            onClick = {
                                                requestPermissionsLauncher.launch(requiredPermissions)
                                            }
                                        )
                                    }
                                }
                            }
                        } else {
                            if (deviceCallLogs.isEmpty()) {
                                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    NeumorphicCard(
                                        modifier = Modifier.padding(24.dp)
                                    ) {
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            modifier = Modifier.padding(24.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Phone,
                                                contentDescription = "No calls",
                                                modifier = Modifier.size(64.dp),
                                                tint = NeoSecondaryText.copy(alpha = 0.5f)
                                            )
                                            Spacer(modifier = Modifier.height(16.dp))
                                            Text(
                                                text = "No recent call logs found",
                                                style = MaterialTheme.typography.bodyLarge,
                                                color = NeoSecondaryText
                                            )
                                        }
                                    }
                                }
                            } else {
                                LazyColumn(
                                    contentPadding = PaddingValues(vertical = 8.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    itemsIndexed(deviceCallLogs) { index, callLog ->
                                        val isHighlighted = highlightedIds.contains(callLog.id)
                                        val backgroundColor by animateColorAsState(
                                            targetValue = if (isHighlighted) NeoAccent.copy(alpha = 0.2f) else NeoSurface,
                                            animationSpec = androidx.compose.animation.core.tween(500)
                                        )
                                        val elevation by animateFloatAsState(
                                            targetValue = if (isHighlighted) 8f else 4f,
                                            animationSpec = androidx.compose.animation.core.tween(500)
                                        )
                                        NeumorphicCard(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable {
                                                    // Handle call log click
                                                },
                                            cornerRadius = 16.dp,
                                            elevation = elevation.dp,
                                            backgroundColor = backgroundColor
                                        ) {
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(16.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                NeumorphicCard(
                                                    modifier = Modifier.size(48.dp),
                                                    cornerRadius = 24.dp,
                                                    elevation = 3.dp,
                                                    backgroundColor = when (callLog.type) {
                                                        android.provider.CallLog.Calls.INCOMING_TYPE -> NeoAccent.copy(alpha = 0.2f)
                                                        android.provider.CallLog.Calls.OUTGOING_TYPE -> NeoSecondaryText.copy(alpha = 0.2f)
                                                        else -> NeoPrimaryText.copy(alpha = 0.1f)
                                                    }
                                                ) {
                                                    Box(
                                                        modifier = Modifier.fillMaxSize(),
                                                        contentAlignment = Alignment.Center
                                                    ) {
                                                        Icon(
                                                            imageVector = when (callLog.type) {
                                                                android.provider.CallLog.Calls.INCOMING_TYPE -> Icons.AutoMirrored.Filled.CallReceived
                                                                android.provider.CallLog.Calls.OUTGOING_TYPE -> Icons.AutoMirrored.Filled.CallMade
                                                                else -> Icons.AutoMirrored.Filled.CallMissed
                                                            },
                                                            contentDescription = "Call type",
                                                            tint = when (callLog.type) {
                                                                android.provider.CallLog.Calls.INCOMING_TYPE -> NeoAccent
                                                                android.provider.CallLog.Calls.OUTGOING_TYPE -> NeoSecondaryText
                                                                else -> NeoPrimaryText
                                                            },
                                                            modifier = Modifier.size(24.dp)
                                                        )
                                                    }
                                                }
                                                
                                                Spacer(modifier = Modifier.width(12.dp))
                                                
                                                Column(
                                                    modifier = Modifier.weight(1f)
                                                ) {
                                                    Text(
                                                        text = callLog.name ?: callLog.number,
                                                        style = MaterialTheme.typography.bodyLarge,
                                                        fontWeight = FontWeight.Medium,
                                                        color = NeoPrimaryText
                                                    )
                                                    Text(
                                                        text = callLog.number,
                                                        style = MaterialTheme.typography.bodySmall,
                                                        color = NeoSecondaryText
                                                    )
                                                    Text(
                                                        text = formatDate(callLog.date),
                                                        style = MaterialTheme.typography.bodySmall,
                                                        color = NeoSecondaryText
                                                    )
                                                }
                                                
                                                Column(
                                                    horizontalAlignment = Alignment.End
                                                ) {
                                                    Text(
                                                        text = formatTime(callLog.date),
                                                        style = MaterialTheme.typography.bodySmall,
                                                        color = NeoSecondaryText
                                                    )
                                                    if (callLog.durationSec > 0) {
                                                        Text(
                                                            text = "${callLog.durationSec}s",
                                                            style = MaterialTheme.typography.bodySmall,
                                                            color = NeoSecondaryText
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    1 -> {
                        // Auto Message Sent List tab with optional search
                        LaunchedEffect(Unit) {
                            autoReplyViewModel.updateAllToDelivered()
                        }
                        val query = debouncedQuery
                        val base = autoReplyHistory.filter { it.isAutoReply && it.status == "DELIVERED" }
                        val filtered = if (query.isBlank()) base else base.filter { log ->
                            (log.contactName ?: "").contains(query, ignoreCase = true) ||
                            log.phoneNumber.contains(query, ignoreCase = true) ||
                            (log.messageText ?: "").contains(query, ignoreCase = true)
                        }
                        var lastPopupTs by remember { mutableStateOf(0L) }
                        var lastPopupId by remember { mutableStateOf(-1L) }
                        LaunchedEffect(base) {
                            try {
                                val now = System.currentTimeMillis()
                                val candidate = base.maxByOrNull { kotlin.math.max(it.deliveredTimestamp ?: 0L, it.sentTimestamp ?: 0L) }
                                val ts = candidate?.let { kotlin.math.max(it.deliveredTimestamp ?: 0L, it.sentTimestamp ?: 0L) } ?: 0L
                                if (candidate != null && ts > lastPopupTs && (now - ts) <= 1500) {
                                    popupType = if ((candidate.deliveredTimestamp ?: 0L) >= (candidate.sentTimestamp ?: 0L)) "DELIVERED" else "SENT"
                                    popupLog = candidate
                                    lastPopupTs = ts
                                    lastPopupId = candidate.id
                                }
                            } catch (_: Exception) { }
                        }
                        Box(modifier = Modifier.fillMaxSize()) {
                            if (isLoading) {
                                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    NeumorphicCard(
                                        modifier = Modifier.size(80.dp),
                                        cornerRadius = 40.dp,
                                        elevation = 6.dp,
                                        backgroundColor = NeoSurface
                                    ) {
                                        Box(
                                            modifier = Modifier.fillMaxSize(),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            CircularProgressIndicator(
                                                color = NeoAccent,
                                                strokeWidth = 3.dp
                                            )
                                        }
                                    }
                                }
                            } else if (filtered.isEmpty()) {
                                EmptyState()
                            } else {
                                Column {
                                    // Show last auto-clear status
                                    if (lastClearTime > 0) {
                                        val lastClearDate = java.util.Date(lastClearTime)
                                        val timeAgo = formatTimeAgo(lastClearTime)
                                        NeumorphicCard(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 16.dp)
                                                .padding(bottom = 8.dp),
                                            cornerRadius = 12.dp,
                                            elevation = 2.dp
                                        ) {
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(12.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Schedule,
                                                    contentDescription = null,
                                                    tint = NeoSecondaryText,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text(
                                                    text = "History auto-cleared $timeAgo",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = NeoSecondaryText
                                                )
                                            }
                                        }
                                    }
                                    
                                    AutoReplyHistoryList(
                                        history = filtered,
                                        onDeleteItem = { log ->
                                            confirmDeleteLog = log
                                        },
                                        onClickItem = { log ->
                                            navController.navigate("message_detail/${log.id}")
                                        }
                                    )
                                }
                            }

                            // Delete confirmation overlay
                            confirmDeleteLog?.let { log ->
                                NeumorphicCard(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp)
                                ) {
                                    Column(modifier = Modifier.padding(20.dp)) {
                                        Text(
                                            text = "Delete this entry?",
                                            style = MaterialTheme.typography.titleMedium,
                                            color = NeoPrimaryText
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = "This will remove the selected auto-reply log.",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = NeoSecondaryText
                                        )
                                        Spacer(modifier = Modifier.height(16.dp))
                                        Row {
                                            NeumorphicButton(
                                                text = "Cancel",
                                                onClick = { confirmDeleteLog = null },
                                                modifier = Modifier.weight(1f)
                                            )
                                            Spacer(modifier = Modifier.width(12.dp))
                                            NeumorphicButton(
                                                text = "Delete",
                                                onClick = {
                                                    viewModel.deleteAutoReplyLog(log)
                                                    confirmDeleteLog = null
                                                },
                                                modifier = Modifier.weight(1f)
                                            )
                                        }
                                    }
                                }
                            }

                            // One-time toast for midnight auto-clear
                            if (showAutoClearToast) {
                                androidx.compose.runtime.LaunchedEffect(Unit) {
                                    kotlinx.coroutines.delay(2500)
                                    showAutoClearToast = false
                                    AutoReplyHistoryClearWorker.markToastNotified(context)
                                }
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(16.dp),
                                    contentAlignment = Alignment.BottomCenter
                                ) {
                                    NeumorphicCard(
                                        cornerRadius = 20.dp,
                                        elevation = 10.dp,
                                        backgroundColor = NeoSurface
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .padding(horizontal = 22.dp, vertical = 18.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Info,
                                                contentDescription = null,
                                                tint = NeoAccent,
                                                modifier = Modifier.size(24.dp)
                                            )
                                            Text(
                                                text = "Auto-reply history cleared at midnight.",
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = NeoPrimaryText,
                                                fontWeight = FontWeight.Medium
                                            )
                                        }
                                    }
                                }
                            }

                            // In-app popup for sent/delivered
                            popupLog?.let { log ->
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(16.dp),
                                    contentAlignment = Alignment.BottomCenter
                                ) {
                                    NeumorphicCard(
                                        cornerRadius = 16.dp,
                                        elevation = 12.dp,
                                        backgroundColor = NeoSurface
                                    ) {
                                        Column(modifier = Modifier.padding(16.dp)) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(
                                                    imageVector = Icons.Default.Notifications,
                                                    contentDescription = null,
                                                    tint = NeoAccent,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text(
                                                    text = if (popupType == "DELIVERED") "Delivered" else "Sent",
                                                    style = MaterialTheme.typography.titleSmall,
                                                    color = NeoPrimaryText,
                                                    fontWeight = FontWeight.SemiBold
                                                )
                                            }
                                            Spacer(modifier = Modifier.height(6.dp))
                                            Text(
                                                text = "To: ${log.contactName.ifEmpty { log.phoneNumber }}",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = NeoSecondaryText
                                            )
                                            Text(
                                                text = "At: ${formatTime(log.timestamp)}",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = NeoSecondaryText
                                            )
                                            Spacer(modifier = Modifier.height(6.dp))
                                            Text(
                                                text = (log.messageText ?: "").split('\n').firstOrNull().orEmpty(),
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = NeoPrimaryText,
                                                maxLines = 2,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            Spacer(modifier = Modifier.height(10.dp))
                                            Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                                                NeumorphicButton(
                                                    text = "Close",
                                                    onClick = { popupLog = null },
                                                    cornerRadius = 10.dp
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
}

// Top-level model for device call logs
data class DeviceCallLog(
    val id: Long,
    val name: String?,
    val number: String,
    val type: Int,
    val date: Long,
    val durationSec: Int
)

@Composable
private fun CallLogsList(logs: List<DeviceCallLog>, highlightedIds: Set<Long>) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        itemsIndexed(logs) { index, log ->
            val isAnswered = (
                (log.type == android.provider.CallLog.Calls.INCOMING_TYPE && log.durationSec > 0) ||
                (log.type == android.provider.CallLog.Calls.OUTGOING_TYPE && log.durationSec > 0)
            )
            val isMissed = log.type == android.provider.CallLog.Calls.MISSED_TYPE
            val isRejected = try { android.provider.CallLog.Calls.REJECTED_TYPE == log.type } catch (_: Exception) { false }
            val isVoicemail = log.type == android.provider.CallLog.Calls.VOICEMAIL_TYPE
            
            // Color coding for different call types (accessible colors with high contrast)
            val (tint, bgColor) = when {
                // Incoming answered calls: Dark green on light green background
                (log.type == android.provider.CallLog.Calls.INCOMING_TYPE && log.durationSec > 0) -> {
                    Color(0xFF1B5E20) to Color(0xFFE8F5E8) // Dark green on very light green
                }
                // Outgoing made calls: Dark blue on light blue background  
                log.type == android.provider.CallLog.Calls.OUTGOING_TYPE -> {
                    Color(0xFF0D47A1) to Color(0xFFE3F2FD) // Dark blue on very light blue
                }
                // Missed calls: Dark red on light red background
                isMissed || isRejected -> {
                    Color(0xFFB71C1C) to Color(0xFFFFEBEE) // Dark red on very light red
                }
                // Voicemail: Dark purple on light purple background
                isVoicemail -> {
                    Color(0xFF4A148C) to Color(0xFFF3E5F5) // Dark purple on very light purple
                }
                // Default fallback
                else -> {
                    NeoAccent to NeoSurface
                }
            }
            val callIcon = when {
                isMissed || isRejected -> Icons.AutoMirrored.Filled.CallMissed
                log.type == android.provider.CallLog.Calls.OUTGOING_TYPE -> Icons.AutoMirrored.Filled.CallMade
                else -> Icons.AutoMirrored.Filled.CallReceived
            }
            val isHighlighted = isAnswered && highlightedIds.contains(log.id)
            val stagger = 200 + ((index % 5) * 40)

            AnimatedVisibility(
                visible = true,
                enter = fadeIn(animationSpec = tween(durationMillis = stagger)) + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                val targetBg = if (isHighlighted) Color(0xFF4CAF50).copy(alpha = 0.1f) else bgColor
                val bgColor by animateColorAsState(
                    targetValue = targetBg,
                    animationSpec = androidx.compose.animation.core.tween(durationMillis = 2000)
                )
                val iconBgTargetAlpha = if (isHighlighted) 0.25f else 0.15f
                val iconBgAlpha by animateFloatAsState(
                    targetValue = iconBgTargetAlpha,
                    animationSpec = androidx.compose.animation.core.tween(durationMillis = 2000)
                )

                NeumorphicCard(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(bgColor, RoundedCornerShape(20.dp))
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = log.name ?: "Unknown",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = NeoPrimaryText,
                                    fontWeight = FontWeight.SemiBold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = log.number,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = NeoSecondaryText,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(tint.copy(alpha = iconBgAlpha)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = callIcon, 
                                    contentDescription = null, 
                                    tint = tint,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "${formatDateOnly(log.date)} • ${formatTime(log.date)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = NeoSecondaryText
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyState() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        NeumorphicCard(
            modifier = Modifier.size(80.dp),
            cornerRadius = 40.dp,
            elevation = 4.dp,
            backgroundColor = NeoSurface
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Phone,
                    contentDescription = "No recent calls",
                    modifier = Modifier.size(40.dp),
                    tint = NeoSecondaryText
                )
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "No recent auto-replies",
            style = MaterialTheme.typography.titleMedium,
            color = NeoPrimaryText
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Auto-replies will appear here after calls end",
            style = MaterialTheme.typography.bodyMedium,
            color = NeoSecondaryText,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}

@Composable
private fun AutoReplyHistoryList(
    history: List<AutoReplyLogEntity>,
    onDeleteItem: (AutoReplyLogEntity) -> Unit,
    onClickItem: (AutoReplyLogEntity) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        itemsIndexed(history) { index, log ->
            val stagger = 180 + ((index % 5) * 40)
            val snippet = log.messageText.split('\n').firstOrNull().orEmpty()

            // Status visuals
            val (statusLabel, statusColor) = when (log.status) {
                "DELIVERED" -> "Delivered" to Color(0xFF2E7D32)
                "SENT" -> "Sent" to Color(0xFF1565C0)
                "FAILED" -> "Failed" to Color(0xFFC62828)
                else -> "Pending" to Color(0xFFF9A825)
            }

            AnimatedVisibility(
                visible = true,
                enter = fadeIn(animationSpec = tween(durationMillis = stagger)) + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                NeumorphicCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onClickItem(log) }
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(statusColor.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Phone, 
                                    contentDescription = null, 
                                    tint = statusColor,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = log.contactName.ifEmpty { log.phoneNumber },
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = NeoPrimaryText,
                                    fontWeight = FontWeight.SemiBold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = snippet,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = NeoSecondaryText,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = statusLabel,
                                    color = statusColor,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = formatTime(log.timestamp),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = NeoSecondaryText
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(NeoSecondaryText.copy(alpha = 0.1f))
                                    .clickable { onDeleteItem(log) },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete",
                                    tint = NeoSecondaryText,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
