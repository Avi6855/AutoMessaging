package com.avinashpatil.app.automessage.ui.screens.contacts

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.filled.GroupAdd
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Send
import androidx.compose.ui.window.Dialog
import com.avinashpatil.app.automessage.ui.screens.messages.MessagesViewModel
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.avinashpatil.app.automessage.data.entity.ContactEntity
import com.avinashpatil.app.automessage.ui.components.GlassCard

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.telephony.SmsManager
import android.telephony.SubscriptionManager
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.heightIn
import androidx.lifecycle.ViewModel
import com.avinashpatil.app.automessage.data.repository.ContactRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf
import android.content.Context
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Check
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Message
import com.avinashpatil.app.automessage.ui.theme.NeumorphicButton
import com.avinashpatil.app.automessage.ui.theme.NeumorphicCard
import com.avinashpatil.app.automessage.ui.theme.NeumorphicSearchField
import com.avinashpatil.app.automessage.ui.theme.NeumorphicTab
import com.avinashpatil.app.automessage.ui.theme.NeoAccent
import com.avinashpatil.app.automessage.ui.theme.NeoLightBackground
import com.avinashpatil.app.automessage.ui.theme.NeoPrimaryText
import com.avinashpatil.app.automessage.ui.theme.NeoSecondaryText
import com.avinashpatil.app.automessage.ui.theme.NeoSurface
import com.avinashpatil.app.automessage.ui.components.StandardTopAppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactsScreen(
    viewModel: ContactsViewModel = hiltViewModel(),
    navController: NavHostController? = null
) {
    val contacts by viewModel.contacts.collectAsState()
    val priorityContacts by viewModel.priorityContacts.collectAsState()
    val blacklistedContacts by viewModel.blacklistedContacts.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()
    val groups by viewModel.groups.collectAsState()
    val context = LocalContext.current

    var selectedTab by remember { mutableIntStateOf(0) }
    var searchQuery by remember { mutableStateOf("") }
    var debouncedQuery by remember { mutableStateOf("") }
    var isSearching by remember { mutableStateOf(false) }
    var showAddDialog by remember { mutableStateOf(false) }
    var editContact by remember { mutableStateOf<ContactEntity?>(null) }
    var assignTarget by remember { mutableStateOf<ContactEntity?>(null) }
    var deleteTarget by remember { mutableStateOf<ContactEntity?>(null) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        // Sync device contacts into local DB once screen launches
        viewModel.syncDeviceContacts(context)
    }

    // Replace LaunchedEffect(searchQuery) with snapshotFlow debounce to avoid recomposition loops
    LaunchedEffect(Unit) {
        snapshotFlow { searchQuery }
            .debounce(300)
            .distinctUntilChanged()
            .collect { qRaw ->
                val q = qRaw.trim()
                debouncedQuery = q
                if (q.isNotBlank()) {
                    viewModel.searchContacts(q)
                } else {
                    viewModel.clearSearchResults()
                }
            }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(NeoLightBackground)
            .windowInsetsPadding(WindowInsets.statusBars)
            .windowInsetsPadding(WindowInsets.navigationBars)
    ) {
        Scaffold(
            topBar = {
                StandardTopAppBar(
                    title = "Contacts",
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    actions = {
                        if (isSearching) {
                            NeumorphicSearchField(
                                value = searchQuery,
                                onValueChange = { query ->
                                    searchQuery = query
                                },
                                placeholder = "Search Contacts..",
                                modifier = Modifier.width(220.dp)
                            )
                            IconButton(onClick = {
                                searchQuery = ""
                                debouncedQuery = ""
                                isSearching = false
                                viewModel.clearSearchResults()
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Clear",
                                    tint = NeoPrimaryText
                                )
                            }
                        } else {
                            IconButton(onClick = { isSearching = true }) {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "Search",
                                    tint = NeoPrimaryText
                                )
                            }
                        }
                    }
                )
            },
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState) { data: androidx.compose.material3.SnackbarData ->
                    GlassSnackbar(message = data.visuals.message)
                }
            },
            floatingActionButton = {
                // Hide FAB in Groups tab only
                if (selectedTab != 1) {
                    NeumorphicButton(
                        text = "Add",
                        onClick = { showAddDialog = true },
                        modifier = Modifier.width(80.dp),
                        textColor = Color.White
                    )
                }
            },
            containerColor = Color.Transparent
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
        ) {
                // Show search results when query is active
                if (debouncedQuery.isNotBlank()) {
                    if (searchResults.isNotEmpty()) {
                        ContactList(
                            contacts = searchResults,
                            onContactClick = { contact -> editContact = contact },
                            onToggleBlacklist = { contact ->
                                viewModel.toggleBlacklist(contact.id, !contact.isBlacklisted)
                                scope.launch { snackbarHostState.showSnackbar("Blacklist ${if (!contact.isBlacklisted) "added" else "removed"}") }
                            },
                            onDelete = { contact ->
                                deleteTarget = contact
                            },
                            onAssignGroup = { contact ->
                                assignTarget = contact
                            },
                            onCall = { contact ->
                                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${contact.phoneNumber}"))
                                context.startActivity(intent)
                            },
                            onMessage = { contact ->
                                val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:${contact.phoneNumber}"))
                                context.startActivity(intent)
                            }
                        )
                    }
                } else {
                    // Tabs and default content when not searching
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        NeumorphicTab(
                            text = "All Contacts",
                            isSelected = selectedTab == 0,
                            onClick = { selectedTab = 0 },
                            modifier = Modifier.weight(1f),
                            fontSize = 16.sp
                        )
                        NeumorphicTab(
                            text = "Groups",
                            isSelected = selectedTab == 1,
                            onClick = { selectedTab = 1 },
                            modifier = Modifier.weight(1f),
                            fontSize = 16.sp
                        )
                    }

                    when (selectedTab) {
                        0 -> ContactList(
                            contacts = contacts,
                            onContactClick = { contact -> editContact = contact },
                            onToggleBlacklist = { contact ->
                                viewModel.toggleBlacklist(contact.id, !contact.isBlacklisted)
                                scope.launch { snackbarHostState.showSnackbar("Blacklist ${if (!contact.isBlacklisted) "added" else "removed"}") }
                            },
                            onDelete = { contact ->
                                deleteTarget = contact
                            },
                            onAssignGroup = { contact ->
                                assignTarget = contact
                            },
                            onCall = { contact ->
                                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${contact.phoneNumber}"))
                                context.startActivity(intent)
                            },
                            onMessage = { contact ->
                                val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:${contact.phoneNumber}"))
                                context.startActivity(intent)
                            }
                        )
                        1 -> GroupsList(viewModel = viewModel, snackbarHostState = snackbarHostState, scope = scope, navController = navController)
                    }
                }
            }
        }
    
    // Add Contact Dialog
    if (showAddDialog) {
        AddContactDialog(
            onDismiss = { showAddDialog = false },
            onAdd = { name, phone ->
                viewModel.addContact(name, phone)
                scope.launch { snackbarHostState.showSnackbar("Contact added") }
                showAddDialog = false
            }
        )
    }

    // Edit Contact Dialog
    editContact?.let { ec ->
        EditContactDialog(
            contact = ec,
            onDismiss = { editContact = null },
            onSave = { updated ->
                viewModel.updateContact(updated)
                scope.launch { snackbarHostState.showSnackbar("Contact updated") }
                editContact = null
            }
        )
    }

    // Assign to Group Dialog
    assignTarget?.let { target ->
        AssignGroupDialog(
            onDismiss = { assignTarget = null },
            onAssign = { groupId ->
                viewModel.assignToGroup(target.id, groupId)
                scope.launch { snackbarHostState.showSnackbar("Assigned to group $groupId") }
                assignTarget = null
            }
        )
    }

    // Confirm delete contact dialog
    deleteTarget?.let { dc ->
        androidx.compose.ui.window.Dialog(onDismissRequest = { deleteTarget = null }) {
            NeumorphicCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                cornerRadius = 20.dp,
                elevation = 12.dp,
                backgroundColor = NeoSurface
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text(text = "Delete ${dc.name}?", style = MaterialTheme.typography.titleMedium, color = NeoPrimaryText)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "This action cannot be undone.", style = MaterialTheme.typography.bodySmall, color = NeoSecondaryText)
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        NeumorphicButton(
                            text = "CANCEL",
                            onClick = { deleteTarget = null },
                            textColor = NeoPrimaryText,
                            modifier = Modifier.weight(1f)
                        )
                        NeumorphicButton(
                            text = "DELETE",
                            onClick = {
                                viewModel.deleteContact(dc)
                                scope.launch { snackbarHostState.showSnackbar("Contact deleted") }
                                deleteTarget = null
                            },
                            textColor = Color.White,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }

    // Message dropdown moved to GroupsList implementation to avoid duplication
}
}

class FakeContactsViewModel : ContactsViewModel(FakeContactRepository()) {
    private val _fakeContacts = MutableStateFlow(
        listOf(
            ContactEntity("1", "Alice", "123-456-7890"),
            ContactEntity("2", "Bob", "098-765-4321", isPriority = true),
            ContactEntity("3", "Charlie", "555-555-5555", isBlacklisted = true, groupId = 1L)
        )
    )
    override val contacts = _fakeContacts.asStateFlow()

    private val _fakePriorityContacts = MutableStateFlow(
        _fakeContacts.value.filter { it.isPriority }
    )
    override val priorityContacts = _fakePriorityContacts.asStateFlow()

    private val _fakeBlacklistedContacts = MutableStateFlow(
        _fakeContacts.value.filter { it.isBlacklisted }
    )
    override val blacklistedContacts = _fakeBlacklistedContacts.asStateFlow()

    private val _fakeSearchResults = MutableStateFlow<List<ContactEntity>>(emptyList())
    override val searchResults = _fakeSearchResults.asStateFlow()

    private val _fakeGroups = MutableStateFlow(listOf(1L))
    override val groups = _fakeGroups.asStateFlow()
}

class FakeContactRepository : ContactRepository {
    override fun getAllContacts(): Flow<List<ContactEntity>> = flowOf(
        listOf(
            ContactEntity("1", "Alice", "123-456-7890"),
            ContactEntity("2", "Bob", "098-765-4321", isPriority = true),
            ContactEntity("3", "Charlie", "555-555-5555", isBlacklisted = true, groupId = 1L)
        )
    )
    override suspend fun getContactById(contactId: String): ContactEntity? = null
    override suspend fun getContactByPhoneNumber(phone: String): ContactEntity? = null
    override fun searchContacts(query: String): Flow<List<ContactEntity>> = emptyFlow()
    override suspend fun insertContact(contact: ContactEntity) {}
    override suspend fun updateContact(contact: ContactEntity) {}
    override suspend fun deleteContact(contact: ContactEntity) {}
    override suspend fun assignContactToGroup(contactId: String, groupId: Long) {}
    override suspend fun markAsPriority(contactId: String, isPriority: Boolean) {}
    override suspend fun addToBlacklist(contactId: String, isBlacklisted: Boolean, reason: String?) {}
    override fun getPriorityContacts(): Flow<List<ContactEntity>> = flowOf(
        listOf(
            ContactEntity("2", "Bob", "098-765-4321", isPriority = true)
        )
    )
    override fun getBlacklistedContacts(): Flow<List<ContactEntity>> = flowOf(
        listOf(
            ContactEntity("3", "Charlie", "555-555-5555", isBlacklisted = true, groupId = 1L)
        )
    )
    override fun getContactsByGroup(groupId: Long): Flow<List<ContactEntity>> = emptyFlow()
    override fun getDistinctGroups(): Flow<List<Long>> = flowOf(listOf(1L))
    override suspend fun getContactCount(): Int = 3
    override suspend fun isPriorityContact(contactId: String): Boolean = false
    override suspend fun isBlacklistedContact(contactId: String): Boolean = false
    override suspend fun getContactCountByGroup(groupId: Long): Int = 0
    override suspend fun removeContactFromGroup(contactId: String) {}
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun ContactsScreenPreview() {
    val viewModel = FakeContactsViewModel()
    ContactsScreen(viewModel = viewModel)
}

@Preview
@Composable
fun ContactListPreview() {
    val contacts = listOf(
        ContactEntity(id = "1", name = "John Doe", phoneNumber = "1234567890"),
        ContactEntity(id = "2", name = "Jane Smith", phoneNumber = "0987654321", isPriority = true),
        ContactEntity(id = "3", name = "Peter Jones", phoneNumber = "5555555555", isBlacklisted = true),
    )
    ContactList(
        contacts = contacts,
        onContactClick = {},
        onToggleBlacklist = {},
        onDelete = {},
        onAssignGroup = {},
        onCall = {},
        onMessage = {}
    )
}

@Preview
@Composable
fun ContactItemPreview() {
    val contact = ContactEntity(id = "1", name = "John Doe", phoneNumber = "1234567890", isPriority = true)
    ContactItem(
        contact = contact,
        onClick = {},
        onToggleBlacklist = {},
        onEdit = {},
        onDelete = {},
        onAssignGroup = {},
        onCall = {},
        onMessage = {}
    )
}

@Preview
@Composable
fun ContactAvatarPreview() {
    ContactAvatar(name = "John Doe")
}

@Composable
fun ContactList(
    contacts: List<ContactEntity>,
    onContactClick: (ContactEntity) -> Unit,
    onToggleBlacklist: (ContactEntity) -> Unit,
    onDelete: (ContactEntity) -> Unit,
    onAssignGroup: (ContactEntity) -> Unit,
    onCall: (ContactEntity) -> Unit,
    onMessage: (ContactEntity) -> Unit,
    onRemoveFromGroup: (ContactEntity) -> Unit = {},
    isGroupView: Boolean = false
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(contacts) { contact ->
            ContactItem(
                contact = contact,
                onClick = { onContactClick(contact) },
                onToggleBlacklist = { onToggleBlacklist(contact) },
                onEdit = { onContactClick(contact) },
                onDelete = { onDelete(contact) },
                onAssignGroup = { onAssignGroup(contact) },
                onCall = { onCall(contact) },
                onMessage = { onMessage(contact) },
                onRemoveFromGroup = { onRemoveFromGroup(contact) },
                isGroupView = isGroupView
            )
        }
    }
}

@Composable
fun ContactItem(
    contact: ContactEntity,
    onClick: () -> Unit,
    onToggleBlacklist: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onAssignGroup: () -> Unit,
    onCall: () -> Unit,
    onMessage: () -> Unit,
    onRemoveFromGroup: () -> Unit = {},
    isGroupView: Boolean = false
) {
    NeumorphicCard(
        modifier = Modifier.fillMaxWidth(),
        cornerRadius = 20.dp,
        elevation = 4.dp,
        backgroundColor = NeoSurface,
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header row with avatar, name, and phone - matching Auto Messages design
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Contact avatar with circular background - matching Auto Messages style
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
                        fontWeight = FontWeight.SemiBold,
                        color = NeoPrimaryText,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = contact.phoneNumber,
                        style = MaterialTheme.typography.bodySmall,
                        color = NeoSecondaryText,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action buttons row - matching Auto Messages style with better spacing
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Call icon button with circular background
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(NeoAccent.copy(alpha = 0.1f))
                        .clickable { onCall() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Phone,
                        contentDescription = "Call",
                        tint = NeoAccent,
                        modifier = Modifier.size(20.dp)
                    )
                }
                
                Spacer(modifier = Modifier.width(8.dp))
                
                // Message icon button with circular background
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(NeoAccent.copy(alpha = 0.1f))
                        .clickable { onMessage() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Message,
                        contentDescription = "Message",
                        tint = NeoAccent,
                        modifier = Modifier.size(20.dp)
                    )
                }
                
                Spacer(modifier = Modifier.width(8.dp))
                
                // Group-related action with circular background
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(if (isGroupView) Color.Red.copy(alpha = 0.1f) else NeoSecondaryText.copy(alpha = 0.1f))
                        .clickable { if (isGroupView) onRemoveFromGroup() else onAssignGroup() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isGroupView) Icons.Default.Close else Icons.Default.Group,
                        contentDescription = if (isGroupView) "Remove from Group" else "Assign to Group",
                        tint = if (isGroupView) Color.Red else NeoSecondaryText,
                        modifier = Modifier.size(20.dp)
                    )
                }
                
                Spacer(modifier = Modifier.width(8.dp))
                
                // Edit icon button with circular background
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(NeoSecondaryText.copy(alpha = 0.1f))
                        .clickable { onEdit() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = NeoSecondaryText,
                        modifier = Modifier.size(20.dp)
                    )
                }
                
                Spacer(modifier = Modifier.width(8.dp))
                
                // Delete icon button with circular background
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.Red.copy(alpha = 0.1f))
                        .clickable { onDelete() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = Color.Red,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
                    /*
                    Row {
                        IconButton(onClick = onCall) {
                            Icon(
                                imageVector = Icons.Default.Phone,
                                contentDescription = "Call",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                        IconButton(onClick = onMessage) {
                            Icon(
                                imageVector = Icons.Default.Message,
                                contentDescription = "Message",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                     */
                }
            }
        }

@Composable
fun ContactAvatar(
    name: String,
    modifier: Modifier = Modifier
) {
    val initial = name.firstOrNull()?.uppercaseChar() ?: '?'

    NeumorphicCard(
        modifier = modifier.size(40.dp),
        cornerRadius = 20.dp,
        elevation = 4.dp,
        backgroundColor = NeoAccent
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = initial.toString(),
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Preview
@Composable
fun GroupsListPreview() {
    val viewModel = FakeContactsViewModel()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    GroupsList(viewModel = viewModel, snackbarHostState = snackbarHostState, scope = scope)
}

@Composable
fun GroupsList(
    viewModel: ContactsViewModel,
    snackbarHostState: SnackbarHostState,
    scope: kotlinx.coroutines.CoroutineScope,
    navController: NavHostController? = null
) {
    val groupViewModel: com.avinashpatil.app.automessage.ui.viewmodel.GroupViewModel = hiltViewModel()
    val groups by groupViewModel.groups.collectAsState()
    val contacts by viewModel.contacts.collectAsState()
    val localContext = LocalContext.current

    // Wire group /error to snackbars
    val groupError by groupViewModel.error.collectAsState()
    val groupSuccess by groupViewModel.successMessage.collectAsState()
    LaunchedEffect(groupError) {
        groupError?.let {
            scope.launch { snackbarHostState.showSnackbar(it) }
            groupViewModel.clearError()
        }
    }
    LaunchedEffect(groupSuccess) {
        groupSuccess?.let {
            scope.launch { snackbarHostState.showSnackbar(it) }
            groupViewModel.clearSuccessMessage()
        }
    }

    var selectedGroup by remember { mutableStateOf<com.avinashpatil.app.automessage.data.entity.GroupEntity?>(null) }

    var showCreateDialog by remember { mutableStateOf(false) }
    var showRenameDialogFor by remember { mutableStateOf<com.avinashpatil.app.automessage.data.entity.GroupEntity?>(null) }
    var showDeleteDialogFor by remember { mutableStateOf<com.avinashpatil.app.automessage.data.entity.GroupEntity?>(null) }
    var contactToRemove by remember { mutableStateOf<ContactEntity?>(null) }
    var groupForRemove by remember { mutableStateOf<com.avinashpatil.app.automessage.data.entity.GroupEntity?>(null) }
    var showRemoveConfirmDialog by remember { mutableStateOf(false) }
    
    // Message functionality
    val messagesViewModel: MessagesViewModel = hiltViewModel()
    val messages by messagesViewModel.messages.collectAsState()
    var showMessageDropdown by remember { mutableStateOf<com.avinashpatil.app.automessage.data.entity.GroupEntity?>(null) }
    var selectedMessage by remember { mutableStateOf<com.avinashpatil.app.automessage.data.entity.CustomMessageEntity?>(null) }
    var isSending by remember { mutableStateOf(false) }

    if (selectedGroup == null) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Groups", style = MaterialTheme.typography.titleMedium, color = NeoPrimaryText)
                NeumorphicButton(
                    text = "Create Group",
                    onClick = { showCreateDialog = true },
                    modifier = Modifier.width(160.dp),
                    textColor = NeoPrimaryText
                )
            }

            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(groups) { group ->
                val contactCount = contacts.count { it.groupId == group.id }
                
                NeumorphicCard(
                    modifier = Modifier.fillMaxWidth(),
                    cornerRadius = 16.dp,
                    elevation = 4.dp,
                    backgroundColor = NeoSurface,
                    onClick = { 
                        // Navigate to GroupDetailsScreen instead of showing inline view
                        navController?.navigate("group_details/${group.id}")
                    }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = group.name,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Medium,
                                color = NeoPrimaryText
                            )
                            Text(
                                text = "$contactCount Contacts",
                                style = MaterialTheme.typography.bodySmall,
                                color = NeoSecondaryText
                            )
                        }
                        
                        Row {
                            IconButton(onClick = { showMessageDropdown = group }) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.Message,
                                    contentDescription = "Send Message to Group",
                                    tint = NeoAccent
                                )
                            }
                            IconButton(onClick = { showRenameDialogFor = group }) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Rename Group",
                                    tint = NeoSecondaryText
                                )
                            }
                            IconButton(onClick = { showDeleteDialogFor = group }) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete Group",
                                    tint = Color.Red
                                )
                            }
                        }
                    }
                }
            }
        }

            // Message selection and sending dialog
            if (showMessageDropdown != null) {
                    val targetGroup = showMessageDropdown!!
                    val groupContacts = contacts.filter { it.groupId == targetGroup.id }
                    androidx.compose.ui.window.Dialog(onDismissRequest = {
                        showMessageDropdown = null
                        selectedMessage = null
                    }) {
                        NeumorphicCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            cornerRadius = 20.dp,
                            elevation = 12.dp,
                            backgroundColor = NeoSurface
                        ) {
                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                                Text(text = "Select Message", style = MaterialTheme.typography.titleMedium, color = NeoPrimaryText)
                                Spacer(modifier = Modifier.height(8.dp))
                                if (messages.isEmpty()) {
                                    Text(text = "No messages available", style = MaterialTheme.typography.bodySmall, color = NeoSecondaryText)
                                } else {
                                    LazyColumn(
                                        modifier = Modifier.heightIn(max = 300.dp)
                                    ) {
                                        items(messages) { msg ->
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(8.dp)
                                                    .clip(RoundedCornerShape(8.dp))
                                                    .clickable { selectedMessage = msg },
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(imageVector = Icons.Default.Message, contentDescription = null, tint = NeoAccent)
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Column(modifier = Modifier.weight(1f)) {
                                                    Text(text = msg.title, style = MaterialTheme.typography.bodyMedium, color = NeoPrimaryText)
                                                    Text(text = msg.body, style = MaterialTheme.typography.bodySmall, color = NeoSecondaryText, maxLines = 1)
                                                }
                                                if (selectedMessage?.id == msg.id) {
                                                    Icon(imageVector = Icons.Default.Check, contentDescription = "Selected", tint = NeoAccent)
                                                }
                                            }
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    IconButton(onClick = {
                                        showMessageDropdown = null
                                        selectedMessage = null
                                    }) {
                                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = NeoPrimaryText)
                                    }
                                    Spacer(modifier = Modifier.weight(1f))
                                    if (selectedMessage != null) {
                                        Button(onClick = {
                                            // Programmatic auto-send with permission + validation, on IO dispatcher
                                            isSending = true
                                            val msgBody = selectedMessage?.body?.trim().orEmpty()
                                            scope.launch {
                                                try {
                                                    // Permission check
                                                    val hasSms = androidx.core.content.ContextCompat.checkSelfPermission(
                                                        localContext,
                                                        android.Manifest.permission.SEND_SMS
                                                    ) == android.content.pm.PackageManager.PERMISSION_GRANTED
                                                    if (!hasSms) {
                                                        snackbarHostState.showSnackbar("Please enable SMS permissions to send messages.")
                                                    } else if (msgBody.isEmpty()) {
                                                        snackbarHostState.showSnackbar("⚠️ Message content is empty.")
                                                    } else {
                                                        val totalCount = groupContacts.size
                                                        val successCount = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
                                                             var success = 0
                                                             groupContacts.forEach { contact ->
                                                                 val phone = contact.phoneNumber?.trim().orEmpty()
                                                                 if (phone.isNotEmpty()) {
                                                                 try {
                                                                         val smsManager = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                                                                             localContext.getSystemService(android.telephony.SmsManager::class.java) ?: android.telephony.SmsManager.getDefault()
                                                                         } else {
                                                                             android.telephony.SmsManager.getDefault()
                                                                         }
                                                                         val parts = smsManager.divideMessage(msgBody)
                                                                         if (parts.size > 1) {
                                                                             val sentIntents = java.util.ArrayList<android.app.PendingIntent>()
                                                                             parts.forEach { _ ->
                                                                                 sentIntents.add(
                                                                                     android.app.PendingIntent.getBroadcast(
                                                                                         localContext,
                                                                                         0,
                                                                                         android.content.Intent("SMS_SENT"),
                                                                                         android.app.PendingIntent.FLAG_UPDATE_CURRENT or android.app.PendingIntent.FLAG_IMMUTABLE
                                                                                     )
                                                                                 )
                                                                             }
                                                                             smsManager.sendMultipartTextMessage(phone, null, parts, sentIntents, null)
                                                                         } else {
                                                                             val sentIntent = android.app.PendingIntent.getBroadcast(
                                                                                 localContext,
                                                                                 0,
                                                                                 android.content.Intent("SMS_SENT"),
                                                                                 android.app.PendingIntent.FLAG_UPDATE_CURRENT or android.app.PendingIntent.FLAG_IMMUTABLE
                                                                             )
                                                                             smsManager.sendTextMessage(phone, null, msgBody, sentIntent, null)
                                                                         }
                                                                         success++
                                                                     } catch (e: Exception) {
                                                                         android.util.Log.e("GroupsList", "Failed to send to $phone: ${e.message}")
                                                                     }
                                                                 }
                                                             }
                                                          success
                                                           }
                                                          if (successCount == totalCount && totalCount > 0) {
                                                            snackbarHostState.showSnackbar("✅ Message successfully sent to all $totalCount contacts in ${targetGroup.name}!")
                                                        } else if (successCount > 0) {
                                                            snackbarHostState.showSnackbar("⚠️ Some messages couldn't be sent. $successCount/$totalCount messages sent successfully.")
                                                        } else {
                                                            snackbarHostState.showSnackbar("⚠️ Failed to send messages. Please check contact details and permissions.")
                                                        }
                                                    }
                                                } catch (e: Exception) {
                                                    snackbarHostState.showSnackbar("Error sending messages: ${e.message}")
                                                } finally {
                                                    isSending = false
                                                }
                                            }
                                            showMessageDropdown = null
                                            selectedMessage = null
                                        }) {
                                            if (isSending) {
                                                androidx.compose.material3.CircularProgressIndicator(modifier = Modifier.size(18.dp))
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text(text = "Sending...")
                                            } else {
                                                Icon(imageVector = Icons.Default.Send, contentDescription = "Send")
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text(text = "Send to ${groupContacts.size} contacts")
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

    if (selectedGroup != null) {
        val groupContacts = contacts.filter { it.groupId == selectedGroup!!.id }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { selectedGroup = null }) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(text = selectedGroup!!.name, style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.weight(1f))
                IconButton(onClick = { showRenameDialogFor = selectedGroup }) {
                    Icon(imageVector = Icons.Default.Edit, contentDescription = "Rename Group")
                }
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(onClick = { showDeleteDialogFor = selectedGroup }) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete Group", tint = Color.Red)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            ContactList(
                contacts = groupContacts,
                onContactClick = { _: ContactEntity -> /* open edit via dialog triggered from item */ },
                onToggleBlacklist = { contact: ContactEntity ->
                    viewModel.toggleBlacklist(contact.id, !contact.isBlacklisted)
                    scope.launch { snackbarHostState.showSnackbar("Blacklist ${if (!contact.isBlacklisted) "added" else "removed"}") }
                },
                onDelete = { contact: ContactEntity ->
                    viewModel.deleteContact(contact)
                    scope.launch { snackbarHostState.showSnackbar("Contact deleted") }
                },
                onAssignGroup = { contact: ContactEntity ->
                    selectedGroup?.let { g ->
                        viewModel.assignToGroup(contact.id, g.id)
                        scope.launch { snackbarHostState.showSnackbar("Assigned to group ${g.name}") }
                    }
                },
                onCall = { contact: ContactEntity ->
                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${contact.phoneNumber}"))
                    localContext.startActivity(intent)
                },
                onMessage = { contact: ContactEntity ->
                    val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:${contact.phoneNumber}"))
                    localContext.startActivity(intent)
                },
                onRemoveFromGroup = { contact: ContactEntity ->
                    selectedGroup?.let { group ->
                        contactToRemove = contact
                        groupForRemove = group
                        showRemoveConfirmDialog = true
                    }
                },
                isGroupView = true
            )
        }
    }

    // Create Group Dialog
    if (showCreateDialog) {
        var groupNameText by remember { mutableStateOf("") }
        var groupDescriptionText by remember { mutableStateOf("") }
        androidx.compose.ui.window.Dialog(onDismissRequest = { showCreateDialog = false }) {
            NeumorphicCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                cornerRadius = 20.dp,
                elevation = 12.dp,
                backgroundColor = NeoSurface
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text(text = "Create Group", style = MaterialTheme.typography.titleMedium, color = NeoPrimaryText)
                    Spacer(modifier = Modifier.height(8.dp))
                    TextField(value = groupNameText, onValueChange = { groupNameText = it }, label = { Text("Group Name", color = NeoSecondaryText) })
                    Spacer(modifier = Modifier.height(8.dp))
                    TextField(value = groupDescriptionText, onValueChange = { groupDescriptionText = it }, label = { Text("Description (optional)", color = NeoSecondaryText) })
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(modifier = Modifier.weight(1f)) {
                            NeumorphicButton(
                                text = "Cancel",
                                onClick = { showCreateDialog = false },
                                textColor = NeoPrimaryText,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        Box(modifier = Modifier.weight(1f)) {
                            NeumorphicButton(
                                text = "CREATE",
                                onClick = {
                                    val name = groupNameText.trim()
                                    val desc = groupDescriptionText.trim()
                                    when {
                                        name.isEmpty() -> scope.launch { snackbarHostState.showSnackbar("Name cannot be empty") }
                                        groupViewModel.getGroupByName(name) != null -> scope.launch { snackbarHostState.showSnackbar("Group name already exists") }
                                        else -> {
                                            groupViewModel.addGroup(name, desc)
                                            showCreateDialog = false
                                        }
                                    }
                                },
                                textColor = NeoPrimaryText,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }
    }

    // Rename Group Dialog
    if (showRenameDialogFor != null) {
        val currentGroup = showRenameDialogFor!!
        var newGroupNameText by remember { mutableStateOf(currentGroup.name) }
        var newGroupDescriptionText by remember { mutableStateOf(currentGroup.description ?: "") }
        androidx.compose.ui.window.Dialog(onDismissRequest = { showRenameDialogFor = null }) {
            NeumorphicCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                cornerRadius = 20.dp,
                elevation = 12.dp,
                backgroundColor = NeoSurface
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text(text = "Rename Group", style = MaterialTheme.typography.titleMedium, color = NeoPrimaryText)
                    Spacer(modifier = Modifier.height(8.dp))
                    TextField(value = newGroupNameText, onValueChange = { newGroupNameText = it }, label = { Text("New Group Name", color = NeoSecondaryText) })
                    Spacer(modifier = Modifier.height(8.dp))
                    TextField(value = newGroupDescriptionText, onValueChange = { newGroupDescriptionText = it }, label = { Text("Description", color = NeoSecondaryText) })
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        NeumorphicButton(
                            text = "Cancel",
                            onClick = { showRenameDialogFor = null },
                            textColor = NeoPrimaryText,
                            modifier = Modifier.weight(1f)
                        )
                        NeumorphicButton(
                            text = "SAVE",
                            onClick = {
                                val newName = newGroupNameText.trim()
                                val newDesc = newGroupDescriptionText.trim()
                                when {
                                    newName.isEmpty() -> scope.launch { snackbarHostState.showSnackbar("Name cannot be empty") }
                                    // Duplicate name validation excluding current group
                                    groupViewModel.getGroupByName(newName)?.let { it.id != currentGroup.id } == true -> scope.launch { snackbarHostState.showSnackbar("Group name already exists") }
                                    else -> {
                                        groupViewModel.updateGroup(currentGroup.id, newName, newDesc)
                                        showRenameDialogFor = null
                                    }
                                }
                            },
                            textColor = NeoPrimaryText,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }

    // Delete Group Dialog
    if (showDeleteDialogFor != null) {
        val delGroup = showDeleteDialogFor!!
        androidx.compose.ui.window.Dialog(onDismissRequest = { showDeleteDialogFor = null }) {
            NeumorphicCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                cornerRadius = 20.dp,
                elevation = 12.dp,
                backgroundColor = NeoSurface
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text(text = "Delete ${delGroup.name}?", style = MaterialTheme.typography.titleMedium, color = NeoPrimaryText)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "This will remove all members from this group.", style = MaterialTheme.typography.bodySmall, color = NeoSecondaryText)
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        NeumorphicButton(
                            text = "Cancel",
                            onClick = { showDeleteDialogFor = null },
                            textColor = NeoPrimaryText,
                            modifier = Modifier.weight(1f)
                        )
                        NeumorphicButton(
                            text = "DELETE",
                            onClick = {
                                contacts.filter { it.groupId == delGroup.id }.forEach { c ->
                                    viewModel.removeFromGroup(c.id)
                                }
                                groupViewModel.deleteGroup(delGroup.id)
                                selectedGroup = null
                                showDeleteDialogFor = null
                            },
                            textColor = NeoPrimaryText,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
    
    // Remove Contact from Group Confirmation Dialog
    if (showRemoveConfirmDialog) {
        val contact = contactToRemove
        val group = groupForRemove
        if (contact != null && group != null) {
            val cName = contact.name
            val gName = group.name
            val cId = contact.id
            androidx.compose.ui.window.Dialog(onDismissRequest = { showRemoveConfirmDialog = false }) {
                NeumorphicCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    cornerRadius = 20.dp,
                    elevation = 12.dp,
                    backgroundColor = NeoSurface
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Text(text = "Remove $cName from $gName?", style = MaterialTheme.typography.titleMedium, color = NeoPrimaryText)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "This contact will no longer belong to this group.", style = MaterialTheme.typography.bodySmall, color = NeoSecondaryText)
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            NeumorphicButton(
                                text = "Cancel",
                                onClick = { showRemoveConfirmDialog = false },
                                textColor = NeoPrimaryText,
                                modifier = Modifier.weight(1f)
                            )
                            NeumorphicButton(
                                text = "REMOVE",
                                onClick = {
                                    viewModel.removeFromGroup(cId)
                                    scope.launch { snackbarHostState.showSnackbar("Removed $cName from $gName") }
                                    showRemoveConfirmDialog = false
                                },
                                textColor = NeoPrimaryText,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }
    }


} // End of GroupsList function

@Preview
@Composable
fun AddContactDialogPreview() {
    AddContactDialog(
        onDismiss = {},
        onAdd = { name: String, phone: String -> }
    )
}

@Composable
fun AddContactDialog(
    onDismiss: () -> Unit,
    onAdd: (String, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }

    androidx.compose.ui.window.Dialog(onDismissRequest = onDismiss) {
        NeumorphicCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            cornerRadius = 20.dp,
            elevation = 12.dp,
            backgroundColor = NeoSurface
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(text = "Add Contact", style = MaterialTheme.typography.titleMedium, color = NeoPrimaryText)
                Spacer(modifier = Modifier.height(8.dp))
                TextField(value = name, onValueChange = { name = it }, label = { Text("Name", color = NeoSecondaryText) })
                Spacer(modifier = Modifier.height(8.dp))
                TextField(value = phone, onValueChange = { phone = it }, label = { Text("Phone", color = NeoSecondaryText) })
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    NeumorphicButton(
                        onClick = onDismiss,
                        text = "Cancel",
                        textColor = NeoPrimaryText,
                        cornerRadius = 8.dp,
                        modifier = Modifier.weight(1f)
                    )
                    NeumorphicButton(
                        onClick = { if (name.isNotBlank() && phone.isNotBlank()) onAdd(name, phone) },
                        text = "Add",
                        textColor = Color.White,
                        cornerRadius = 8.dp,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun EditContactDialogPreview() {
    val contact = ContactEntity(id = "1", name = "John Doe", phoneNumber = "1234567890")
    EditContactDialog(
        contact = contact,
        onDismiss = {},
        onSave = {}
    )
}

@Composable
fun EditContactDialog(
    contact: ContactEntity,
    onDismiss: () -> Unit,
    onSave: (ContactEntity) -> Unit
) {
    var name by remember { mutableStateOf(contact.name) }
    var phone by remember { mutableStateOf(contact.phoneNumber) }

    androidx.compose.ui.window.Dialog(onDismissRequest = onDismiss) {
        NeumorphicCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            cornerRadius = 20.dp,
            elevation = 12.dp,
            backgroundColor = NeoSurface
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(text = "Edit Contact", style = MaterialTheme.typography.titleMedium, color = NeoPrimaryText)
                Spacer(modifier = Modifier.height(8.dp))
                TextField(value = name, onValueChange = { name = it }, label = { Text("Name", color = NeoSecondaryText) })
                Spacer(modifier = Modifier.height(8.dp))
                TextField(value = phone, onValueChange = { phone = it }, label = { Text("Phone", color = NeoSecondaryText) })
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    NeumorphicButton(
                        onClick = onDismiss,
                        text = "Cancel",
                        textColor = NeoPrimaryText,
                        cornerRadius = 8.dp,
                        modifier = Modifier.weight(1f)
                    )
                    NeumorphicButton(
                        onClick = {
                            if (name.isNotBlank() && phone.isNotBlank()) {
                                onSave(contact.copy(name = name, phoneNumber = phone))
                            }
                        },
                        text = "Save",
                        textColor = Color.White,
                        cornerRadius = 8.dp,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun AssignGroupDialogPreview() {
    AssignGroupDialog(
        onDismiss = {},
        onAssign = {}
    )
}

@Composable
fun AssignGroupDialog(
    onDismiss: () -> Unit,
    onAssign: (Long) -> Unit
) {
    val groupViewModel: com.avinashpatil.app.automessage.ui.viewmodel.GroupViewModel = hiltViewModel()
    val groups by groupViewModel.groups.collectAsState()

    androidx.compose.ui.window.Dialog(onDismissRequest = onDismiss) {
        NeumorphicCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            cornerRadius = 20.dp,
            elevation = 12.dp,
            backgroundColor = NeoSurface
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(text = "Assign to Group", style = MaterialTheme.typography.titleMedium, color = NeoPrimaryText)
                Spacer(modifier = Modifier.height(8.dp))
                if (groups.isEmpty()) {
                    Text(text = "No groups available", style = MaterialTheme.typography.bodySmall, color = NeoSecondaryText)
                } else {
                    LazyColumn(modifier = Modifier.heightIn(max = 300.dp)) {
                        items(groups) { group ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable {
                                        onAssign(group.id)
                                        onDismiss()
                                    },
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(imageVector = Icons.Default.Group, contentDescription = null, tint = NeoAccent)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = group.name, style = MaterialTheme.typography.bodyMedium, color = NeoPrimaryText)
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                Row {
                    NeumorphicButton(
                        text = "Cancel",
                        onClick = onDismiss,
                        textColor = NeoPrimaryText
                    )
                }
            }
        }
    }
}

val GROUP_NAME_PREFS = "group_names"
fun getGroupName(context: Context, id: Long): String? {
    val prefs = context.getSharedPreferences(GROUP_NAME_PREFS, Context.MODE_PRIVATE)
    return prefs.getString(id.toString(), null)
}
fun setGroupName(context: Context, id: Long, name: String) {
    val prefs = context.getSharedPreferences(GROUP_NAME_PREFS, Context.MODE_PRIVATE)
    prefs.edit().putString(id.toString(), name).apply()
}
fun removeGroupName(context: Context, id: Long) {
    val prefs = context.getSharedPreferences(GROUP_NAME_PREFS, Context.MODE_PRIVATE)
    prefs.edit().remove(id.toString()).apply()
}

// Styled snackbar using NeumorphicCard for consistent visual styling
@Composable
fun GlassSnackbar(message: String) {
    NeumorphicCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        cornerRadius = 12.dp,
        elevation = 4.dp,
        backgroundColor = NeoSurface
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = Icons.Default.Info, contentDescription = null, tint = NeoAccent)
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = message, style = MaterialTheme.typography.bodyMedium, color = NeoPrimaryText)
        }
    }
}

@Composable
fun GroupMessageSelectionDialog(
    messages: List<com.avinashpatil.app.automessage.data.entity.CustomMessageEntity>,
    selectedMessage: com.avinashpatil.app.automessage.data.entity.CustomMessageEntity?,
    onDismiss: () -> Unit,
    onSelect: (com.avinashpatil.app.automessage.data.entity.CustomMessageEntity) -> Unit,
    onSend: () -> Unit
) {
    androidx.compose.ui.window.Dialog(onDismissRequest = onDismiss) {
        NeumorphicCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            cornerRadius = 16.dp,
            elevation = 6.dp,
            backgroundColor = NeoLightBackground
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "Select Message", style = MaterialTheme.typography.titleMedium, color = NeoPrimaryText)
                Spacer(modifier = Modifier.height(8.dp))
                if (messages.isEmpty()) {
                    Text(text = "No messages available", style = MaterialTheme.typography.bodySmall, color = NeoSecondaryText)
                } else {
                    LazyColumn(
                        modifier = Modifier.heightIn(max = 300.dp)
                    ) {
                        items(messages) { msg ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { onSelect(msg) },
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(imageVector = Icons.Default.Message, contentDescription = null, tint = NeoAccent)
                                Spacer(modifier = Modifier.width(8.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = msg.title, style = MaterialTheme.typography.bodyMedium, color = NeoPrimaryText)
                                    Text(text = msg.body, style = MaterialTheme.typography.bodySmall, color = NeoSecondaryText, maxLines = 1)
                                }
                                if (selectedMessage?.id == msg.id) {
                                    Icon(imageVector = Icons.Default.Check, contentDescription = "Selected", tint = NeoAccent)
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    NeumorphicButton(
                        text = "Close",
                        onClick = onDismiss,
                        textColor = NeoPrimaryText
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    if (selectedMessage != null) {
                        NeumorphicButton(
                            text = "Send",
                            onClick = onSend,
                            textColor = Color.White
                        )
                    }
                }
            }
        }
    }
}
