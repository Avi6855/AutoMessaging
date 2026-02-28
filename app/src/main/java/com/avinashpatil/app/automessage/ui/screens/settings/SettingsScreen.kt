package com.avinashpatil.app.automessage.ui.screens.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Settings

import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.avinashpatil.app.automessage.data.entity.CustomMessageEntity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.navigationBars
import com.avinashpatil.app.automessage.ui.components.StandardTopAppBar
import com.avinashpatil.app.automessage.ui.screens.messages.MessagesViewModel
import com.avinashpatil.app.automessage.ui.viewmodel.AutoReplyViewModel
import androidx.navigation.NavHostController
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.IconButton
import androidx.compose.foundation.shape.CircleShape
import com.avinashpatil.app.automessage.ui.theme.NeoLightBackground
import com.avinashpatil.app.automessage.ui.theme.NeoSurface
import com.avinashpatil.app.automessage.ui.theme.NeoPrimaryText
import com.avinashpatil.app.automessage.ui.theme.NeoSecondaryText
import com.avinashpatil.app.automessage.ui.theme.NeoAccent
import com.avinashpatil.app.automessage.ui.theme.NeumorphicSurface
import com.avinashpatil.app.automessage.ui.theme.NeumorphicCard
import com.avinashpatil.app.automessage.ui.theme.NeumorphicButton
import com.avinashpatil.app.automessage.ui.theme.NeumorphicSwitch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavHostController,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val autoReplyEnabled by viewModel.autoReplyEnabled.collectAsState()
    val autoReplyDelay by viewModel.autoReplyDelay.collectAsState()
    val defaultMessageId by viewModel.defaultMessageId.collectAsState()
    val darkMode by viewModel.darkMode.collectAsState()
    val isFirstTime by viewModel.isFirstTime.collectAsState()
    val notificationSound by viewModel.notificationSound.collectAsState()
    
    val messagesViewModel: MessagesViewModel = hiltViewModel()
    val autoReplyViewModel: AutoReplyViewModel = hiltViewModel()
    val defaultMessage by messagesViewModel.defaultMessage.collectAsState()
    val isAutoReplyEnabled by autoReplyViewModel.isAutoReplyEnabled.collectAsState()
    
    var selectedTab by remember { mutableStateOf(0) }
    var isSearching by remember { mutableStateOf(false) }
    
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
                StandardTopAppBar(
                    title = "Auto-Messaging Settings",
                    modifier = Modifier.padding(top = 8.dp, start = 8.dp, end = 8.dp)
              /*      actions = {
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
                        IconButton(onClick = { nc.navigate("settings") }) { 
                            Icon( 
                                Icons.Default.Settings, 
                                contentDescription = "Settings", 
                                tint = NeoPrimaryText 
                            ) 
                        } 
                    }

               */
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
                    NeumorphicCard(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {


                            // Appearance settings
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
                            IconButton(onClick = { navController.navigate("reliability") }) {
                                Icon(
                                    Icons.Default.Settings,
                                    contentDescription = "Settings",
                                    tint = NeoPrimaryText
                                )
                            }

                            SettingsSection(
                                title = "Appearance",
                                icon = Icons.Default.Settings
                            ) {
                                // Dark mode
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "D mode",
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = NeoPrimaryText,
                                        modifier = Modifier.weight(1f)
                                    )
                                    NeumorphicSwitch(
                                        checked = darkMode,
                                        onCheckedChange = { enabled ->
                                            viewModel.setDarkMode(enabled)
                                        },
                                        modifier = Modifier.size(50.dp, 30.dp)
                                    )
                                }
                            }

                            SettingsSection(
                                title = "Notifications",
                                icon = Icons.Default.Notifications
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Notification sound",
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = NeoPrimaryText,
                                        modifier = Modifier.weight(1f)
                                    )
                                    NeumorphicSwitch(
                                        checked = notificationSound,
                                        onCheckedChange = { enabled -> viewModel.setNotificationSound(enabled) },
                                        modifier = Modifier.size(50.dp, 30.dp)
                                    )
                                }
                            }

                            // Appearance settings 
                            SettingsSection( 
                                title = "Appearance", 
                                icon = Icons.Default.Settings 
                            ) { 
                                // Dark mode 
                                Row( 
                                    modifier = Modifier.fillMaxWidth(), 
                                    verticalAlignment = Alignment.CenterVertically 
                                ) { 
                                    Text( 
                                        text = "Dark mode", 
                                        style = MaterialTheme.typography.bodyLarge, 
                                        color = NeoPrimaryText, 
                                        modifier = Modifier.weight(1f) 
                                    ) 
                                    NeumorphicSwitch( 
                                        checked = darkMode, 
                                        onCheckedChange = { enabled -> 
                                            viewModel.setDarkMode(enabled) 
                                        }, 
                                        modifier = Modifier.size(50.dp, 30.dp) 
                                    ) 
                                } 
                            } 
    
                            // About 
                            SettingsSection( 
                                title = "About", 
                                icon = Icons.Default.Info 
                            ) { 
                                SettingsItem( 
                                    title = "App Version", 
                                    description = "1.0.0", 
                                    onClick = { } 
                                ) 
                                 
                                SettingsItem( 
                                    title = "Developer", 
                                    description = "Avinash Patil", 
                                    onClick = { } 
                                ) 
                            } 
                        }
                    }
                }
            }
        }

    

@Composable
private fun SettingsSection(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    content: @Composable () -> Unit
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
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = NeoAccent,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = NeoPrimaryText,
                    fontWeight = FontWeight.Bold
                )
            }
            content()
        }
    }
}

@Composable
private fun SettingsItem(
    title: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    hasSwitch: Boolean = false,
    switchChecked: Boolean = false,
    onClick: () -> Unit = {},
    onSwitchChanged: (Boolean) -> Unit = {}
) {
    NeumorphicCard(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            icon?.let {
                Icon(
                    imageVector = it,
                    contentDescription = null,
                    tint = NeoSecondaryText,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
            }

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    color = NeoPrimaryText,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = NeoSecondaryText
                )
            }

            if (hasSwitch) {
                NeumorphicSwitch(
                    checked = switchChecked,
                    onCheckedChange = onSwitchChanged,
                    modifier = Modifier.size(50.dp, 30.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DelayDialog(
    currentDelay: Int,
    onDismiss: () -> Unit,
    onSave: (Int) -> Unit
) {
    var delay by remember { mutableStateOf(currentDelay.toString()) }
    
    androidx.compose.ui.window.Dialog(onDismissRequest = onDismiss) {
        NeumorphicCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            cornerRadius = 16.dp,
            elevation = 6.dp,
            backgroundColor = NeoLightBackground
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Auto-Reply Delay",
                    style = MaterialTheme.typography.titleMedium,
                    color = NeoPrimaryText
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Set the delay in seconds before sending auto-reply",
                    style = MaterialTheme.typography.bodySmall,
                    color = NeoSecondaryText
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = delay,
                    onValueChange = { delay = it },
                    label = { Text("Delay (seconds)", color = NeoSecondaryText) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = MaterialTheme.typography.bodyMedium.copy(color = NeoPrimaryText),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeoAccent,
                        unfocusedBorderColor = NeoSecondaryText.copy(alpha = 0.3f),
                        focusedLabelColor = NeoAccent,
                        unfocusedLabelColor = NeoSecondaryText,
                        cursorColor = NeoAccent
                    )
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row {
                    NeumorphicButton(
                        text = "Cancel",
                        onClick = onDismiss,
                        cornerRadius = 8.dp
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    NeumorphicButton(
                        text = "Save",
                        onClick = { delay.toIntOrNull()?.let { onSave(it) } },
                        cornerRadius = 8.dp
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun SettingsScreenPreview() {
    SettingsScreen(navController = androidx.navigation.compose.rememberNavController())
}

@Preview
@Composable
private fun SettingsSectionPreview() {
    SettingsSection(
        title = "Sample Section",
        icon = Icons.Default.Settings
    ) {
        Column {
            Text("Item 1 in section")
            Text("Item 2 in section")
        }
    }
}

@Preview
@Composable
private fun SettingsItemPreview() {
    SettingsItem(
        title = "Sample Setting",
        description = "This is a description for the sample setting.",
        icon = Icons.Default.Info
    )
}

@Preview
@Composable
private fun SettingsItemWithSwitchPreview() {
    SettingsItem(
        title = "Setting with Switch",
        description = "This setting has a switch.",
        hasSwitch = true,
        switchChecked = true
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun DelayDialogPreview() {
    DelayDialog(
        currentDelay = 5,
        onDismiss = {},
        onSave = {}
    )
}
