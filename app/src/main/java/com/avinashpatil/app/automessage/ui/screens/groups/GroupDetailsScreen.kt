package com.avinashpatil.app.automessage.ui.screens.groups

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.background
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.avinashpatil.app.automessage.data.entity.ContactEntity
import com.avinashpatil.app.automessage.data.entity.CustomMessageEntity

import kotlinx.coroutines.launch
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.window.Dialog
import kotlinx.coroutines.CoroutineScope
import android.telephony.SmsManager
import android.telephony.SubscriptionManager
import android.os.Build
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import com.avinashpatil.app.automessage.R
import com.avinashpatil.app.automessage.ui.components.StandardTopAppBar
import com.avinashpatil.app.automessage.ui.theme.NeumorphicCard
import com.avinashpatil.app.automessage.ui.theme.NeumorphicButton
import com.avinashpatil.app.automessage.ui.theme.NeoAccent
import com.avinashpatil.app.automessage.ui.theme.NeoLightBackground
import com.avinashpatil.app.automessage.ui.theme.NeoPrimaryText
import com.avinashpatil.app.automessage.ui.theme.NeoSecondaryText
import com.avinashpatil.app.automessage.ui.theme.NeoSurface

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupDetailsScreen(
    groupId: Long,
    navController: NavController,
    viewModel: GroupDetailsViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    // Collect states
    val group by viewModel.group.collectAsState()
    val contacts by viewModel.contacts.collectAsState()
    val messages by viewModel.messages.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val successMessage by viewModel.successMessage.collectAsState()

    // Load group details
    LaunchedEffect(groupId) {
        viewModel.loadGroupDetails(groupId)
    }

    // Show snackbars for error/success
    LaunchedEffect(error) {
        error?.let {
            scope.launch { snackbarHostState.showSnackbar(it) }
            viewModel.clearError()
        }
    }
    LaunchedEffect(successMessage) {
        successMessage?.let {
            scope.launch { snackbarHostState.showSnackbar(it) }
            viewModel.clearSuccessMessage()
        }
    }

    // Dialog states
    var showRenameDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showRemoveContactDialog by remember { mutableStateOf<ContactEntity?>(null) }
    var showMessageDropdown by remember { mutableStateOf(false) }
    var showScheduleDialog by remember { mutableStateOf(false) }
    var selectedMessage by remember { mutableStateOf<CustomMessageEntity?>(null) }
    var showAddContactDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.background(NeoLightBackground),
        topBar = {
            StandardTopAppBar(
                title = group?.name ?: "Group Details",
                showBackButton = true,
                onBackClick = { navController.navigateUp() },
                actions = {
                    /*IconButton(onClick = { showMessageDropdown = true }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Message,
                            contentDescription = "Send Message to Group",
                            tint = NeoPrimaryText
                        )
                    }

                    IconButton(onClick = { showRenameDialog = true }) {
                        Icon(Icons.Default.Edit, contentDescription = null, tint = NeoPrimaryText)
                    }
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(Icons.Default.Delete, contentDescription = null, tint = NeoPrimaryText)
                    }

                     */
                    Row {
                        IconButton(onClick = { showMessageDropdown = true }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Message,
                                contentDescription = "Send Message to Group",
                                tint = NeoAccent
                            )
                        }
                        IconButton(onClick = { showRenameDialog = true }) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Rename Group",
                                tint = NeoSecondaryText
                            )
                        }
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete Group",
                                tint = Color.Red
                            )
                        }
                    }
                }


            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { paddingValues ->
        // Root container changed to Box to support child alignment
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(NeoLightBackground)
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                // Contact count info
                Text(
                    text = "${contacts.size} contacts",
                    style = MaterialTheme.typography.bodySmall,
                    color = NeoSecondaryText,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                // Message sending section
                if (showMessageDropdown) {
                    Dialog(onDismissRequest = { showMessageDropdown = false }) {
                        NeumorphicCard(
                            onClick = { },
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
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Send Message to Group",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = NeoPrimaryText
                                    )
                                    IconButton(onClick = { showMessageDropdown = false }) {
                                        Icon(
                                            Icons.Default.Close,
                                            contentDescription = "Close",
                                            tint = NeoPrimaryText
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                // Message dropdown
                                var expanded by remember { mutableStateOf(false) }
                                ExposedDropdownMenuBox(
                                    expanded = expanded,
                                    onExpandedChange = { expanded = it }
                                ) {
                                    NeumorphicCard(
                                        onClick = { },
                                        modifier = Modifier
                                            .menuAnchor()
                                            .fillMaxWidth(),
                                        cornerRadius = 20.dp,
                                        elevation = 12.dp,
                                        backgroundColor = NeoSurface
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(16.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = selectedMessage?.title ?: "Select a message",
                                                color = NeoPrimaryText,
                                                style = MaterialTheme.typography.bodyMedium
                                            )
                                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                                        }
                                    }

                                    ExposedDropdownMenu(
                                        expanded = expanded,
                                        onDismissRequest = { expanded = false }
                                    ) {
                                        messages.forEach { message ->
                                            DropdownMenuItem(
                                                text = {
                                                    Column {
                                                        Text(
                                                            text = message.title,
                                                            style = MaterialTheme.typography.bodyMedium,
                                                            fontWeight = FontWeight.Medium,
                                                            color = NeoPrimaryText
                                                        )
                                                        Text(
                                                            text = message.body.take(50) + if (message.body.length > 50) "..." else "",
                                                            style = MaterialTheme.typography.bodySmall,
                                                            color = NeoSecondaryText,
                                                            maxLines = 1,
                                                            overflow = TextOverflow.Ellipsis
                                                        )
                                                    }
                                                },
                                                onClick = {
                                                    selectedMessage = message
                                                    expanded = false
                                                }
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                // Send button (only show when message is selected)
                                if (selectedMessage != null) {
                                    NeumorphicButton(
                                        text = if (isLoading) "Sending..." else "Send to ${contacts.size} contacts",
                                        onClick = {
                                            selectedMessage?.let { message ->
                                                viewModel.sendMessageToGroup(contacts, message)
                                                selectedMessage = null
                                                showMessageDropdown = false
                                            }
                                        },
                                        textColor = Color.White
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Contacts list
            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .windowInsetsPadding(WindowInsets.statusBars)
                        .windowInsetsPadding(WindowInsets.navigationBars),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (contacts.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Message,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No contacts in this group",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Add contacts to this group from the Contacts tab",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Enhanced contact list items with neumorphic styling
                    items(contacts) { contact ->
                        NeumorphicCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            cornerRadius = 20.dp,
                            elevation = 8.dp,
                            backgroundColor = NeoSurface,
                            onClick = { /* Handle contact click */ }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Contact avatar styled like ContactsScreen
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(NeoAccent.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = contact.name.take(1).uppercase(),
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = FontWeight.SemiBold,
                                        color = NeoAccent
                                    )
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column(
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        text = contact.name,
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = FontWeight.Medium,
                                        color = NeoPrimaryText
                                    )
                                    Text(
                                        text = contact.phoneNumber,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = NeoSecondaryText
                                    )
                                }

                                IconButton(onClick = { showRemoveContactDialog = contact }) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Remove Contact",
                                        tint = NeoAccent,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        if (showRenameDialog && group != null) {
            RenameGroupDialog(
                currentName = group?.name ?: "",
                currentDescription = group?.description ?: "",
                onDismiss = { showRenameDialog = false },
                onConfirm = { newName, newDescription ->
                    viewModel.updateGroup(groupId, newName, newDescription)
                    showRenameDialog = false
                }
            )
        }

        // Delete Group Dialog
        if (showDeleteDialog && group != null) {
            DeleteGroupDialog(
                groupName = group?.name ?: "Unknown Group",
                onDismiss = { showDeleteDialog = false },
                onConfirm = {
                    viewModel.deleteGroup(groupId)
                    navController.navigateUp()
                }
            )
        }

        // Remove Contact Dialog
        if (showRemoveContactDialog != null) {
            RemoveContactDialog(
                contactName = showRemoveContactDialog!!.name,
                groupName = group?.name ?: "",
                onDismiss = { showRemoveContactDialog = null },
                onConfirm = {
                    viewModel.removeContactFromGroup(showRemoveContactDialog!!.id, groupId)
                    showRemoveContactDialog = null
                }
            )
        }
    }
}

@Preview
@Composable
fun GroupDetailsScreenPreview() {
    // Fake NavController
    val navController = NavController(LocalContext.current)

    // You might need a fake ViewModel or mock it if it's complex.
    // For a simple preview, you can often pass a default instance
    // if hiltViewModel() is not strictly required for the preview to render.
    // Or, you could create a fake implementation of the ViewModel.
    // For this example, let's assume we can't easily create a ViewModel,
    // so we will comment out the call that requires it, or pass a placeholder.
    // The simplest way to make it build is to provide the parameters.

    // Since creating a full ViewModel instance for a preview is complex,
    // and the UI is what we want to preview, we'll call it with dummy data.
    GroupDetailsScreen(groupId = 1L, navController = navController)
}
    @Composable
    private fun ContactItem(
        contact: ContactEntity,
        onRemoveFromGroup: () -> Unit
    ) {
        NeumorphicCard(
            modifier = Modifier.fillMaxWidth(),
            cornerRadius = 20.dp,
            elevation = 8.dp,
            backgroundColor = NeoSurface
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Contact avatar styled like ContactsScreen, with image if available
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(NeoAccent.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        if (contact.photoUri != null) {
                            AsyncImage(
                                model = contact.photoUri,
                                contentDescription = "Contact photo",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Text(
                                text = contact.name.take(1).uppercase(),
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.SemiBold,
                                color = NeoAccent
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = contact.name,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium,
                            color = NeoPrimaryText
                        )
                        Text(
                            text = contact.phoneNumber,
                            style = MaterialTheme.typography.bodyMedium,
                            color = NeoSecondaryText
                        )
                    }
                }

                IconButton(
                    onClick = onRemoveFromGroup
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Remove from group",
                        tint = NeoAccent,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }

@Preview
@Composable
fun ContactItemPreview() {
    val contact = ContactEntity(id = "1", name = "John Doe", phoneNumber = "123-456-7890")
    ContactItem(contact = contact, onRemoveFromGroup = {})
}

    @Composable
    private fun RenameGroupDialog(
        currentName: String,
        currentDescription: String,
        onDismiss: () -> Unit,
        onConfirm: (String, String) -> Unit
    ) {
        var name by remember { mutableStateOf(currentName) }
        var description by remember { mutableStateOf(currentDescription) }

        Dialog(onDismissRequest = onDismiss) {
            NeumorphicCard(
                modifier = Modifier.fillMaxWidth(),
                cornerRadius = 20.dp,
                elevation = 12.dp,
                backgroundColor = NeoSurface
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Rename Group",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = NeoPrimaryText
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Group Name", color = NeoSecondaryText) },
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = LocalTextStyle.current.copy(color = NeoPrimaryText),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeoAccent,
                            unfocusedBorderColor = NeoSecondaryText,
                            focusedLabelColor = NeoAccent,
                            unfocusedLabelColor = NeoSecondaryText,
                            cursorColor = NeoAccent
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Description (optional)", color = NeoSecondaryText) },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3,
                        textStyle = LocalTextStyle.current.copy(color = NeoPrimaryText),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeoAccent,
                            unfocusedBorderColor = NeoSecondaryText,
                            focusedLabelColor = NeoAccent,
                            unfocusedLabelColor = NeoSecondaryText,
                            cursorColor = NeoAccent
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        NeumorphicButton(
                            text = "Cancel",
                            onClick = onDismiss,
                            textColor = NeoPrimaryText
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        NeumorphicButton(
                            text = "Save",
                            onClick = { onConfirm(name, description) },
                            enabled = name.isNotBlank(),
                            textColor = Color.White
                        )
                    }
                }
            }
        }
    }

@Preview
@Composable
fun RenameGroupDialogPreview() {
    RenameGroupDialog(
        currentName = "Old Group Name",
        currentDescription = "Old description.",
        onDismiss = {},
        onConfirm = { _, _ -> }
    )
}
    @Composable
    private fun DeleteGroupDialog(
        groupName: String,
        onDismiss: () -> Unit,
        onConfirm: () -> Unit
    ) {
        Dialog(onDismissRequest = onDismiss) {
            NeumorphicCard(
                modifier = Modifier.fillMaxWidth(),
                cornerRadius = 20.dp,
                elevation = 12.dp,
                backgroundColor = NeoSurface
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Delete Group",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = NeoPrimaryText
                    )

                    Text(
                        text = "Are you sure you want to delete this group? This action cannot be undone.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = NeoSecondaryText
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        NeumorphicButton(
                            text = "CANCEL",
                            onClick = onDismiss,
                            textColor = NeoPrimaryText,
                            modifier = Modifier.weight(1f)
                        )
                        NeumorphicButton(
                            text = "DELETE",
                            onClick = onConfirm,
                            textColor = Color.White,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }

@Preview
@Composable
fun DeleteGroupDialogPreview() {
    DeleteGroupDialog(groupName = "Friends", onDismiss = {}, onConfirm = {})
}

    @Composable
    private fun RemoveContactDialog(
        contactName: String,
        groupName: String,
        onDismiss: () -> Unit,
        onConfirm: () -> Unit
    ) {
        Dialog(onDismissRequest = onDismiss) {
            NeumorphicCard(
                modifier = Modifier.fillMaxWidth(),
                cornerRadius = 20.dp,
                elevation = 12.dp,
                backgroundColor = NeoSurface
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Remove Contact",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = NeoPrimaryText
                    )

                    Text(
                        text = "Are you sure you want to remove $contactName from $groupName?",
                        style = MaterialTheme.typography.bodyMedium,
                        color = NeoSecondaryText
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        NeumorphicButton(
                            text = "CANCEL",
                            onClick = onDismiss,
                            textColor = NeoPrimaryText,
                            modifier = Modifier.weight(1f)
                        )
                        NeumorphicButton(
                            text = "REMOVE",
                            onClick = onConfirm,
                            textColor = Color.White,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }

@Preview
@Composable
fun RemoveContactDialogPreview() {
    RemoveContactDialog(
        contactName = "John Doe",
        groupName = "Family",
        onDismiss = {},
        onConfirm = {}
    )
}
