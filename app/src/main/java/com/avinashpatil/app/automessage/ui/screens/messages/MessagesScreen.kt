package com.avinashpatil.app.automessage.ui.screens.messages

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Message

import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.avinashpatil.app.automessage.data.entity.CustomMessageEntity


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.animateContentSize
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.alpha
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import com.avinashpatil.app.automessage.ui.theme.NeoLightBackground
import com.avinashpatil.app.automessage.ui.theme.NeoSurface
import com.avinashpatil.app.automessage.ui.theme.NeoPrimaryText
import com.avinashpatil.app.automessage.ui.theme.NeoSecondaryText
import com.avinashpatil.app.automessage.ui.theme.NeoAccent
import com.avinashpatil.app.automessage.ui.theme.NeumorphicCard
import com.avinashpatil.app.automessage.ui.theme.NeumorphicButton
import com.avinashpatil.app.automessage.ui.components.StandardTopAppBar
import androidx.compose.ui.window.Dialog
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessagesScreen(
    viewModel: MessagesViewModel = hiltViewModel()
) {
    val messages by viewModel.messages.collectAsState()
    val defaultMessage by viewModel.defaultMessage.collectAsState()
    
    var showAddDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf<CustomMessageEntity?>(null) }
    var showDeleteConfirmDialog by remember { mutableStateOf<CustomMessageEntity?>(null) }
    var showDeleteSuccessToast by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            StandardTopAppBar(
                title = "Message Templates",
                actions = {
                    NeumorphicCard(
                        onClick = { showAddDialog = true },
                        modifier = Modifier.size(40.dp),
                        cornerRadius = 20.dp,
                        elevation = 6.dp,
                        backgroundColor = NeoAccent
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add",
                                tint = Color.White
                            )
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            NeumorphicButton(
                text = "Add",
                onClick = { showAddDialog = true },
                modifier = Modifier.width(80.dp)
            )
        },
        modifier = Modifier
            .fillMaxSize()
            .background(NeoLightBackground)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .windowInsetsPadding(WindowInsets.navigationBars)
                .background(NeoLightBackground)
        ) {
            if (messages.isEmpty()) {
                EmptyState()
            } else {
                MessageList(
                    messages = messages,
                    defaultMessage = defaultMessage,
                    onSetDefault = { message ->
                        viewModel.setDefaultMessage(message.id)
                    },
                    onEdit = { message ->
                        showEditDialog = message
                    },
                    onDelete = { message ->
                        showDeleteConfirmDialog = message
                    }
                )
            }
        }
    }
    
    // Add message dialog
    if (showAddDialog) {
        AddMessageDialog(
            onDismiss = { showAddDialog = false },
            onSave = { title, content ->
                viewModel.addMessage(title, content)
                showAddDialog = false
            }
        )
    }
    
    // Edit message dialog
    if (showEditDialog != null) {
        EditMessageDialog(
            message = showEditDialog!!,
            onDismiss = { showEditDialog = null },
            onSave = { title, content ->
                viewModel.updateMessage(showEditDialog!!.copy(title = title, body = content))
                showEditDialog = null
            }
        )
    }
    
    // Delete confirmation dialog
    if (showDeleteConfirmDialog != null) {
        NeumorphicDeleteConfirmationDialog(
            message = showDeleteConfirmDialog!!,
            onDismiss = { showDeleteConfirmDialog = null },
            onConfirm = {
                viewModel.deleteMessage(showDeleteConfirmDialog!!)
                showDeleteConfirmDialog = null
                showDeleteSuccessToast = true
            }
        )
    }
    
    // Delete success toast
    if (showDeleteSuccessToast) {
        LaunchedEffect(Unit) {
            delay(2000) // Show for 2 seconds
            showDeleteSuccessToast = false
        }
        NeumorphicToast(
            message = "Message deleted successfully!",
            onDismiss = { showDeleteSuccessToast = false }
        )
    }
}

@Composable
private fun EmptyState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        NeumorphicCard(
            modifier = Modifier.padding(32.dp),
            cornerRadius = 16.dp,
            elevation = 8.dp,
            backgroundColor = NeoSurface
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Message,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = NeoSecondaryText
                )
                Text(
                    text = "No message templates yet",
                    style = MaterialTheme.typography.bodyLarge,
                    color = NeoPrimaryText,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "Tap the + button to create your first template",
                    style = MaterialTheme.typography.bodySmall,
                    color = NeoSecondaryText
                )
            }
        }
    }
}

@Composable
private fun NeumorphicDeleteConfirmationDialog(
    message: CustomMessageEntity,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        NeumorphicCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            cornerRadius = 20.dp,
            elevation = 12.dp,
            backgroundColor = NeoSurface
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Are you sure you want to delete this message?",
                    style = MaterialTheme.typography.titleMedium,
                    color = NeoPrimaryText
                )
                
                Text(
                    text = "\"${message.title}\"",
                    style = MaterialTheme.typography.bodyLarge,
                    color = NeoSecondaryText,
                    fontWeight = FontWeight.Medium
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    NeumorphicButton(
                        text = "Cancel",
                        onClick = onDismiss,
                        textColor = NeoPrimaryText,
                        modifier = Modifier.weight(1f)
                    )
                    NeumorphicButton(
                        text = "Delete",
                        onClick = onConfirm,
                        textColor = Color.White,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun NeumorphicToast(
    message: String,
    onDismiss: () -> Unit
) {
    val alpha by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(durationMillis = 300),
        label = "toast_alpha"
    )
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        NeumorphicCard(
            modifier = Modifier
                .alpha(alpha)
                .animateContentSize(),
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
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Success",
                    tint = NeoAccent,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = NeoPrimaryText,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun MessageList(
    messages: List<CustomMessageEntity>,
    defaultMessage: CustomMessageEntity?,
    onSetDefault: (CustomMessageEntity) -> Unit,
    onEdit: (CustomMessageEntity) -> Unit,
    onDelete: (CustomMessageEntity) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(messages) { message ->
            MessageItem(
                message = message,
                isDefault = message.id == defaultMessage?.id,
                onSetDefault = { onSetDefault(message) },
                onEdit = { onEdit(message) },
                onDelete = { onDelete(message) }
            )
        }
    }
}

@Composable
private fun MessageItem(
    message: CustomMessageEntity,
    isDefault: Boolean,
    onSetDefault: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    NeumorphicCard(
        modifier = Modifier.fillMaxWidth(),
        cornerRadius = 20.dp, // Match Auto Messages style
        elevation = if (isDefault) 8.dp else 4.dp,
        backgroundColor = if (isDefault) NeoAccent.copy(alpha = 0.1f) else NeoSurface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp) // Match Auto Messages style
        ) {
            // Header with title and default indicator
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = message.title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = NeoPrimaryText,
                    modifier = Modifier.weight(1f),
                    lineHeight = 24.sp
                )
                
                val badgeScale by animateFloatAsState(
                    targetValue = if (isDefault) 1f else 0.9f,
                    animationSpec = tween(durationMillis = 200)
                )
                AnimatedVisibility(
                    visible = isDefault,
                    enter = fadeIn() + scaleIn(),
                    exit = fadeOut() + scaleOut()
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Default",
                        tint = NeoAccent,
                        modifier = Modifier.size(20.dp).scale(badgeScale)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Message content with enhanced edit/delete icons positioning
            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = message.body,
                    style = MaterialTheme.typography.bodyMedium,
                    color = NeoSecondaryText,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(end = 80.dp), // Space for icons
                    lineHeight = 20.sp
                )
                
                // Enhanced edit/delete icons positioned at top-right with better sizing
                Row(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Edit icon with circular background - matching Auto Messages style
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(NeoSecondaryText.copy(alpha = 0.1f), shape = CircleShape)
                            .clickable { onEdit() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = NeoSecondaryText,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    
                    // Delete icon with circular background - matching Auto Messages style
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(Color.Red.copy(alpha = 0.1f), shape = CircleShape)
                            .clickable { onDelete() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = Color.Red,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Set as Default button (only for non-default messages)
            AnimatedVisibility(
                visible = !isDefault,
                enter = fadeIn(animationSpec = tween(200)),
                exit = fadeOut(animationSpec = tween(150))
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    NeumorphicButton(
                        text = "Set as Default",
                        onClick = onSetDefault
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddMessageDialog(
    onDismiss: () -> Unit,
    onSave: (String, String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    val scrollState = rememberScrollState()
    
    androidx.compose.ui.window.Dialog(onDismissRequest = onDismiss) {
        NeumorphicCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            cornerRadius = 20.dp,
            elevation = 12.dp,
            backgroundColor = NeoSurface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.9f)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .verticalScroll(scrollState),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Add Message Template",
                        style = MaterialTheme.typography.titleMedium,
                        color = NeoPrimaryText
                    )
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Title", color = NeoPrimaryText) },
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = MaterialTheme.typography.bodyMedium.copy(color = NeoPrimaryText),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = content,
                        onValueChange = { content = it },
                        label = { Text("Message Content", color = NeoPrimaryText) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 120.dp),
                        textStyle = MaterialTheme.typography.bodyMedium.copy(color = NeoPrimaryText)
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    NeumorphicButton(
                        text = "Cancel",
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    )
                    NeumorphicButton(
                        text = "Save",
                        onClick = { onSave(title, content) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditMessageDialog(
    message: CustomMessageEntity,
    onDismiss: () -> Unit,
    onSave: (String, String) -> Unit
) {
    var title by remember { mutableStateOf(message.title) }
    var content by remember { mutableStateOf(message.body) }
    val scrollState = rememberScrollState()
    
    androidx.compose.ui.window.Dialog(onDismissRequest = onDismiss) {
        NeumorphicCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            cornerRadius = 20.dp,
            elevation = 12.dp,
            backgroundColor = NeoSurface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.9f)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .verticalScroll(scrollState),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Edit Message Template",
                        style = MaterialTheme.typography.titleMedium,
                        color = NeoPrimaryText
                    )
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Title", color = NeoPrimaryText) },
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = MaterialTheme.typography.bodyMedium.copy(color = NeoPrimaryText),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = content,
                        onValueChange = { content = it },
                        label = { Text("Message Content", color = NeoPrimaryText) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 120.dp),
                        textStyle = MaterialTheme.typography.bodyMedium.copy(color = NeoPrimaryText)
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    NeumorphicButton(
                        text = "Cancel",
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    )
                    NeumorphicButton(
                        text = "Save",
                        onClick = { onSave(title, content) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}
