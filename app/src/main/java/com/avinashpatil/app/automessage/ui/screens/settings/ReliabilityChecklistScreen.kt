package com.avinashpatil.app.automessage.ui.screens.settings

import android.Manifest
import android.app.AlarmManager
import android.app.role.RoleManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.BatterySaver
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.core.content.ContextCompat
import com.avinashpatil.app.automessage.ui.theme.NeoLightBackground
import com.avinashpatil.app.automessage.ui.theme.NeoSurface
import com.avinashpatil.app.automessage.ui.theme.NeoPrimaryText
import com.avinashpatil.app.automessage.ui.theme.NeoSecondaryText
import com.avinashpatil.app.automessage.ui.theme.NeoAccent
import com.avinashpatil.app.automessage.ui.theme.NeumorphicCard

@Composable
fun ReliabilityChecklistScreen(
    navController: NavHostController
) {
    val context = LocalContext.current
    val packageName = context.packageName
    val powerManager = remember { context.getSystemService(Context.POWER_SERVICE) as PowerManager }
    val alarmManager = remember { context.getSystemService(AlarmManager::class.java) }
    val hasCallLogPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CALL_LOG) == android.content.pm.PackageManager.PERMISSION_GRANTED
    val hasPhoneStatePermission = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) == android.content.pm.PackageManager.PERMISSION_GRANTED
    val hasSendSmsPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.SEND_SMS) == android.content.pm.PackageManager.PERMISSION_GRANTED
    val hasNotificationPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == android.content.pm.PackageManager.PERMISSION_GRANTED
    } else {
        true
    }
    val isIgnoringOptimizations = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        powerManager.isIgnoringBatteryOptimizations(packageName)
    } else {
        true
    }
    val canScheduleExactAlarms = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        try {
            alarmManager?.canScheduleExactAlarms() == true
        } catch (_: Exception) {
            false
        }
    } else {
        true
    }
    val isDefaultSms = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        try {
            val rm = context.getSystemService(RoleManager::class.java)
            rm != null && rm.isRoleAvailable(RoleManager.ROLE_SMS) && rm.isRoleHeld(RoleManager.ROLE_SMS)
        } catch (_: Exception) {
            false
        }
    } else {
        true
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
            .windowInsetsPadding(WindowInsets.statusBars)
            .windowInsetsPadding(WindowInsets.navigationBars)
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = NeoPrimaryText)
                    }
                    Text(
                        text = "Reliability Checklist",
                        style = MaterialTheme.typography.titleLarge,
                        color = NeoPrimaryText,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                NeumorphicCard(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "To keep auto SMS running reliably, review and enable the items below.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = NeoSecondaryText
                        )
                    }
                }

                ChecklistItem(
                    title = "Call log access",
                    description = "Required to detect every answered call and avoid missed auto replies.",
                    icon = Icons.Default.Phone,
                    ok = hasCallLogPermission,
                    onClick = {
                        openAppSettings(context, packageName)
                    }
                )

                ChecklistItem(
                    title = "Phone state access",
                    description = "Improves real-time detection when the call starts and ends.",
                    icon = Icons.Default.Phone,
                    ok = hasPhoneStatePermission,
                    onClick = {
                        openAppSettings(context, packageName)
                    }
                )

                ChecklistItem(
                    title = "SMS send permission",
                    description = "Required to send automatic SMS after each call disconnect.",
                    icon = Icons.Default.Message,
                    ok = hasSendSmsPermission,
                    onClick = {
                        openAppSettings(context, packageName)
                    }
                )

                ChecklistItem(
                    title = "Notification permission",
                    description = "Needed so the foreground service notification is always shown.",
                    icon = Icons.Default.Notifications,
                    ok = hasNotificationPermission,
                    onClick = {
                        openAppSettings(context, packageName)
                    }
                )

                ChecklistItem(
                    title = "Ignore battery optimizations",
                    description = "Prevents the system from stopping the service in the background.",
                    icon = Icons.Default.BatterySaver,
                    ok = isIgnoringOptimizations,
                    onClick = {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            try {
                                val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
                                    data = Uri.parse("package:$packageName")
                                }
                                context.startActivity(intent)
                            } catch (_: Exception) {
                                openAppSettings(context, packageName)
                            }
                        } else {
                            openAppSettings(context, packageName)
                        }
                    }
                )

                ChecklistItem(
                    title = "Exact alarm permission",
                    description = "Improves keep-alive scheduling on Android 12 and above.",
                    icon = Icons.Default.Schedule,
                    ok = canScheduleExactAlarms,
                    onClick = {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                            try {
                                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                                    data = Uri.parse("package:$packageName")
                                }
                                context.startActivity(intent)
                            } catch (_: Exception) {
                                openAppSettings(context, packageName)
                            }
                        } else {
                            openAppSettings(context, packageName)
                        }
                    }
                )

                ChecklistItem(
                    title = "Default SMS app",
                    description = "Strongly recommended for best delivery reliability on modern Android.",
                    icon = Icons.Default.Message,
                    ok = isDefaultSms,
                    onClick = {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            try {
                                val rm = context.getSystemService(RoleManager::class.java)
                                if (rm != null && rm.isRoleAvailable(RoleManager.ROLE_SMS) && !rm.isRoleHeld(RoleManager.ROLE_SMS)) {
                                    val intent = rm.createRequestRoleIntent(RoleManager.ROLE_SMS)
                                    context.startActivity(intent)
                                } else {
                                    openAppSettings(context, packageName)
                                }
                            } catch (_: Exception) {
                                openAppSettings(context, packageName)
                            }
                        } else {
                            openAppSettings(context, packageName)
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun ChecklistItem(
    title: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    ok: Boolean,
    onClick: () -> Unit
) {
    NeumorphicCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(NeoSurface, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = NeoAccent
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                    color = NeoPrimaryText
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = NeoSecondaryText
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .size(20.dp),
                contentAlignment = Alignment.Center
            ) {
                if (ok) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Enabled",
                        tint = Color(0xFF4CAF50)
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Error,
                        contentDescription = "Action needed",
                        tint = Color(0xFFFFA000)
                    )
                }
            }
        }
    }
}

private fun openAppSettings(context: Context, packageName: String) {
    try {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.parse("package:$packageName")
        }
        context.startActivity(intent)
    } catch (_: Exception) {
    }
}

