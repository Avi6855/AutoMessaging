package com.avinashpatil.app.automessage.ui.screens.recent

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.background
import androidx.compose.ui.graphics.Color
import com.avinashpatil.app.automessage.ui.components.StandardTopAppBar
import com.avinashpatil.app.automessage.ui.theme.NeumorphicButton
import com.avinashpatil.app.automessage.ui.theme.NeumorphicCard
import com.avinashpatil.app.automessage.ui.theme.NeoAccent
import com.avinashpatil.app.automessage.ui.theme.NeoLightBackground
import com.avinashpatil.app.automessage.ui.theme.NeoPrimaryText
import com.avinashpatil.app.automessage.ui.theme.NeoSecondaryText
import com.avinashpatil.app.automessage.ui.theme.NeoSurface

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessageDetailScreen(
    navController: NavHostController,
    viewModel: MessageDetailViewModel = hiltViewModel()
) {
    val log by viewModel.log.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(NeoLightBackground)
            .windowInsetsPadding(WindowInsets.systemBars)
    ) {
        Scaffold(
            topBar = {
                StandardTopAppBar(
                    title = "Message Detail",
                    showBackButton = true,
                    onBackClick = { navController.popBackStack() },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            },
            containerColor = Color.Transparent
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                when {
                    isLoading -> {
                        Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                            NeumorphicCard(
                                modifier = Modifier.size(80.dp),
                                cornerRadius = 40.dp,
                                elevation = 6.dp
                            ) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(36.dp),
                                        color = NeoAccent,
                                        strokeWidth = 3.dp
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.size(16.dp))
                            Text(
                                "Loading...",
                                color = NeoPrimaryText,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                    error != null -> {
                        NeumorphicCard(
                            modifier = Modifier.fillMaxWidth(),
                            cornerRadius = 12.dp,
                            elevation = 3.dp
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                NeumorphicCard(
                                    modifier = Modifier.size(48.dp),
                                    cornerRadius = 24.dp,
                                    elevation = 3.dp
                                ) {
                                    Box(
                                        modifier = Modifier.fillMaxSize(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Info,
                                            contentDescription = null,
                                            tint = NeoAccent,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.size(12.dp))
                                Text(
                                    text = error ?: "Unknown error",
                                    color = NeoPrimaryText,
                                    style = MaterialTheme.typography.bodyLarge,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                                Spacer(modifier = Modifier.size(12.dp))
                                NeumorphicButton(
                                    text = "Back",
                                    onClick = { navController.popBackStack() }
                                )
                            }
                        }
                    }
                    log != null -> {
                        val l = log!!
                        NeumorphicCard(
                            modifier = Modifier.fillMaxWidth(),
                            cornerRadius = 12.dp,
                            elevation = 4.dp
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = l.contactName.ifEmpty { "Unknown Contact" },
                                    color = NeoPrimaryText,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                                )
                                Text(
                                    text = "Number: ${l.phoneNumber}",
                                    color = NeoSecondaryText,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    text = "Status: ${l.status}",
                                    color = NeoPrimaryText,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    text = "Attempts: ${l.attempts}",
                                    color = NeoPrimaryText,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    text = "Sent: ${l.sentTimestamp?.let { java.text.SimpleDateFormat("hh:mm a", java.util.Locale.getDefault()).format(java.util.Date(it)) } ?: "—"}",
                                    color = NeoSecondaryText,
                                    style = MaterialTheme.typography.bodySmall
                                )
                                Text(
                                    text = "Delivered: ${l.deliveredTimestamp?.let { java.text.SimpleDateFormat("hh:mm a", java.util.Locale.getDefault()).format(java.util.Date(it)) } ?: "—"}",
                                    color = NeoSecondaryText,
                                    style = MaterialTheme.typography.bodySmall
                                )
                                Spacer(modifier = Modifier.size(12.dp))
                                Text(
                                    text = "Message:",
                                    color = NeoPrimaryText,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold
                                )
                                Text(
                                    text = l.messageText,
                                    color = NeoPrimaryText,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                        }
                    }
                    else -> {
                        NeumorphicCard(
                            modifier = Modifier.fillMaxWidth(),
                            cornerRadius = 12.dp,
                            elevation = 3.dp
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "No data available",
                                    color = NeoPrimaryText,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Spacer(modifier = Modifier.size(12.dp))
                                NeumorphicButton(
                                    text = "Back",
                                    onClick = { navController.popBackStack() }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}