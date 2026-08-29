package com.avinashpatil.app.automessage.ui.screens.settings

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryAlert
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.avinashpatil.app.automessage.ui.components.StandardTopAppBar
import com.avinashpatil.app.automessage.ui.theme.NeoAccent
import com.avinashpatil.app.automessage.ui.theme.NeoLightBackground
import com.avinashpatil.app.automessage.ui.theme.NeoPrimaryText
import com.avinashpatil.app.automessage.ui.theme.NeoSecondaryText
import com.avinashpatil.app.automessage.ui.theme.NeoSurface
import com.avinashpatil.app.automessage.ui.theme.NeumorphicCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavHostController,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val autoMessagingEnabled by viewModel.autoMessagingEnabled.collectAsState()
    val isBatteryOptimized by viewModel.isBatteryOptimized.collectAsState()
    val autoReplyEnabled by viewModel.autoReplyEnabled.collectAsState()
    val darkMode by viewModel.darkMode.collectAsState()
    val notificationSound by viewModel.notificationSound.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(NeoLightBackground, NeoSurface)
                )
            )
            .windowInsetsPadding(WindowInsets.statusBars)
            .windowInsetsPadding(WindowInsets.navigationBars)
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                StandardTopAppBar(
                    title = "Settings",
                    modifier = Modifier.padding(top = 8.dp, start = 8.dp, end = 8.dp)
                )
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
                AutomaticMessagingCard(
                    enabled = autoMessagingEnabled,
                    onToggle = { viewModel.setAutoMessagingEnabled(it) }
                )

                if (autoMessagingEnabled) {
                    BackgroundReliabilityCard(
                        isBatteryOptimized = isBatteryOptimized,
                        onRequestOptimization = { viewModel.openBatteryOptimizationSettings() },
                        onOpenAppSettings = { viewModel.openAppSettings() }
                    )

                    GeneralSettingsCard(
                        darkMode = darkMode,
                        notificationSound = notificationSound,
                        onDarkModeChanged = { viewModel.setDarkMode(it) },
                        onNotificationSoundChanged = { viewModel.setNotificationSound(it) }
                    )
                }

                AboutCard()

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun AutomaticMessagingCard(
    enabled: Boolean,
    onToggle: (Boolean) -> Unit
) {
    val cardColor by animateColorAsState(
        targetValue = if (enabled) Color(0xFF4CAF50).copy(alpha = 0.08f) else Color(0xFFFF9800).copy(alpha = 0.08f),
        animationSpec = tween(300),
        label = "cardColor"
    )
    val statusColor by animateColorAsState(
        targetValue = if (enabled) Color(0xFF4CAF50) else Color(0xFF9E9E9E),
        animationSpec = tween(300),
        label = "statusColor"
    )

    NeumorphicCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(cardColor),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.CheckCircle,
                        contentDescription = null,
                        tint = statusColor,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Automatic Messaging",
                        style = MaterialTheme.typography.titleMedium,
                        color = NeoPrimaryText,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Automatically send your configured message after an eligible call ends",
                        style = MaterialTheme.typography.bodySmall,
                        color = NeoSecondaryText
                    )
                }

                Switch(
                    checked = enabled,
                    onCheckedChange = onToggle,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = NeoAccent,
                        uncheckedThumbColor = Color.White,
                        uncheckedTrackColor = NeoSecondaryText.copy(alpha = 0.3f)
                    )
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.Black.copy(alpha = 0.03f))
                    .padding(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(statusColor)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (enabled) "Active \u2014 Auto messaging is running" else "Paused \u2014 No automatic messages will be sent",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (enabled) Color(0xFF4CAF50) else Color(0xFF9E9E9E)
                )
            }
        }
    }
}

@Composable
private fun BackgroundReliabilityCard(
    isBatteryOptimized: Boolean,
    onRequestOptimization: () -> Unit,
    onOpenAppSettings: () -> Unit
) {
    NeumorphicCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.BatteryAlert,
                    contentDescription = null,
                    tint = NeoAccent,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Background Reliability",
                    style = MaterialTheme.typography.titleMedium,
                    color = NeoPrimaryText,
                    fontWeight = FontWeight.Bold
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Battery Optimization",
                        style = MaterialTheme.typography.bodyMedium,
                        color = NeoPrimaryText,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = if (isBatteryOptimized) "Restricted \u2014 May affect background operation" else "Unrestricted \u2014 Optimal background performance",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isBatteryOptimized) Color(0xFFFF9800) else Color(0xFF4CAF50)
                    )
                }

                if (isBatteryOptimized) {
                    NeumorphicButtonSmall(
                        text = "Allow",
                        onClick = onRequestOptimization,
                        color = NeoAccent
                    )
                } else {
                    Icon(
                        imageVector = Icons.Filled.CheckCircle,
                        contentDescription = null,
                        tint = Color(0xFF4CAF50),
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "App Permissions",
                        style = MaterialTheme.typography.bodyMedium,
                        color = NeoPrimaryText,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "Manage call log, phone state, and SMS permissions",
                        style = MaterialTheme.typography.bodySmall,
                        color = NeoSecondaryText
                    )
                }

                NeumorphicButtonSmall(
                    text = "Settings",
                    onClick = onOpenAppSettings,
                    color = NeoSecondaryText
                )
            }
        }
    }
}

@Composable
private fun GeneralSettingsCard(
    darkMode: Boolean,
    notificationSound: Boolean,
    onDarkModeChanged: (Boolean) -> Unit,
    onNotificationSoundChanged: (Boolean) -> Unit
) {
    NeumorphicCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Settings,
                    contentDescription = null,
                    tint = NeoAccent,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "General",
                    style = MaterialTheme.typography.titleMedium,
                    color = NeoPrimaryText,
                    fontWeight = FontWeight.Bold
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Dark Mode",
                    style = MaterialTheme.typography.bodyMedium,
                    color = NeoPrimaryText,
                    modifier = Modifier.weight(1f)
                )
                Switch(
                    checked = darkMode,
                    onCheckedChange = onDarkModeChanged,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = NeoAccent,
                        uncheckedThumbColor = Color.White,
                        uncheckedTrackColor = NeoSecondaryText.copy(alpha = 0.3f)
                    )
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Notification Sound",
                    style = MaterialTheme.typography.bodyMedium,
                    color = NeoPrimaryText,
                    modifier = Modifier.weight(1f)
                )
                Switch(
                    checked = notificationSound,
                    onCheckedChange = onNotificationSoundChanged,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = NeoAccent,
                        uncheckedThumbColor = Color.White,
                        uncheckedTrackColor = NeoSecondaryText.copy(alpha = 0.3f)
                    )
                )
            }
        }
    }
}

@Composable
private fun AboutCard() {
    NeumorphicCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Info,
                    contentDescription = null,
                    tint = NeoAccent,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "About",
                    style = MaterialTheme.typography.titleMedium,
                    color = NeoPrimaryText,
                    fontWeight = FontWeight.Bold
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "App Version",
                    style = MaterialTheme.typography.bodyMedium,
                    color = NeoPrimaryText,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "1.0.0",
                    style = MaterialTheme.typography.bodySmall,
                    color = NeoSecondaryText
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Developer",
                    style = MaterialTheme.typography.bodyMedium,
                    color = NeoPrimaryText,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "Avinash Patil",
                    style = MaterialTheme.typography.bodySmall,
                    color = NeoSecondaryText
                )
            }
        }
    }
}

@Composable
private fun NeumorphicButtonSmall(
    text: String,
    onClick: () -> Unit,
    color: Color = NeoAccent
) {
    androidx.compose.material3.TextButton(
        onClick = onClick,
        colors = androidx.compose.material3.ButtonDefaults.textButtonColors(
            contentColor = color
        )
    ) {
        Text(
            text = text,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
