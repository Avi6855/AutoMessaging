package com.avinashpatil.app.automessage.ui.screens.templates

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.draw.rotate
import com.avinashpatil.app.automessage.ui.theme.NeumorphicSurface
import com.avinashpatil.app.automessage.ui.theme.NeumorphicCard
import com.avinashpatil.app.automessage.ui.theme.NeumorphicButton
import com.avinashpatil.app.automessage.ui.theme.NeoAccent
import com.avinashpatil.app.automessage.ui.theme.NeoLightBackground
import com.avinashpatil.app.automessage.ui.theme.NeoPrimaryText
import com.avinashpatil.app.automessage.ui.theme.NeoSecondaryText
import com.avinashpatil.app.automessage.ui.theme.NeoSurface
import com.avinashpatil.app.automessage.ui.components.StandardTopAppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    onBack: () -> Unit = {}
) {
    var query by remember { mutableStateOf("") }
    Scaffold(
        modifier = Modifier
            .background(NeoLightBackground)
            .windowInsetsPadding(WindowInsets.statusBars)
            .windowInsetsPadding(WindowInsets.navigationBars),
        containerColor = Color.Transparent,
        topBar = {
            StandardTopAppBar(
                title = "Search",
                showBackButton = true,
                onBackClick = onBack,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Enhanced neumorphic search bar
            NeumorphicCard(
                modifier = Modifier.fillMaxWidth(),
                cornerRadius = 24.dp,
                elevation = 8.dp,
                backgroundColor = NeoSurface
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Search, 
                        contentDescription = null, 
                        tint = NeoAccent,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(Modifier.size(12.dp))
                    TextField(
                        value = query,
                        onValueChange = { query = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { 
                            Text(
                                "Search contacts and logs", 
                                color = NeoSecondaryText,
                                style = MaterialTheme.typography.bodyMedium
                            ) 
                        },
                        colors = TextFieldDefaults.colors(
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            disabledIndicatorColor = Color.Transparent,
                            cursorColor = NeoAccent,
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent
                        ),
                        textStyle = MaterialTheme.typography.bodyLarge.copy(
                            color = NeoPrimaryText,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
            }

            // Enhanced results list with neumorphic cards
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                listOf(
                    "Alice - 2 replies",
                    "Bob - 1 reply", 
                    "Carol - 3 replies",
                    "David - 0 replies"
                ).forEachIndexed { index, item ->
                    NeumorphicCard(
                        modifier = Modifier.fillMaxWidth(),
                        cornerRadius = 20.dp,
                        elevation = 6.dp,
                        onClick = { /* Handle item click */ }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Enhanced orange number badge with neumorphic styling
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(
                                        brush = androidx.compose.ui.graphics.Brush.radialGradient(
                                            colors = listOf(
                                                NeoAccent.copy(alpha = 0.3f),
                                                NeoAccent.copy(alpha = 0.1f)
                                            )
                                        ),
                                        shape = CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${index + 1}", 
                                    color = NeoPrimaryText, 
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                            Spacer(Modifier.size(16.dp))
                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = item, 
                                    color = NeoPrimaryText,
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = "Tap to view details", 
                                    color = NeoSecondaryText,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "View details",
                                tint = NeoSecondaryText,
                                modifier = Modifier
                                    .size(20.dp)
                                    .rotate(180f)
                            )
                        }
                    }
                }
            }

            // Add some bottom spacing
            Spacer(modifier = Modifier.size(8.dp))
        }
    }
}