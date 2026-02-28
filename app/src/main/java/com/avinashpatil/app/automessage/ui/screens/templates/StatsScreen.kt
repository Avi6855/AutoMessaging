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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.ChevronRight
import com.avinashpatil.app.automessage.ui.theme.NeumorphicButton
import com.avinashpatil.app.automessage.ui.theme.NeumorphicCard
import com.avinashpatil.app.automessage.ui.theme.NeumorphicBadge
import com.avinashpatil.app.automessage.ui.theme.NeoAccent
import com.avinashpatil.app.automessage.ui.theme.NeoLightBackground
import com.avinashpatil.app.automessage.ui.theme.NeoPrimaryText
import com.avinashpatil.app.automessage.ui.theme.NeoSecondaryText
import com.avinashpatil.app.automessage.ui.theme.NeoSurface
import com.avinashpatil.app.automessage.ui.components.StandardTopAppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsScreen(
    onBack: () -> Unit = {},
    onRefresh: () -> Unit = {}
) {
    Scaffold(
        modifier = Modifier
            .background(NeoLightBackground)
            .windowInsetsPadding(WindowInsets.statusBars)
            .windowInsetsPadding(WindowInsets.navigationBars),
        containerColor = Color.Transparent,
        topBar = {
            StandardTopAppBar(
                title = "Stats",
                showBackButton = true,
                onBackClick = onBack,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                actions = {
                    IconButton(onClick = { }) {
                        Icon(
                            imageVector = Icons.Default.Info, 
                            contentDescription = "Info",
                            tint = NeoAccent
                        )
                    }
                }
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
            // Enhanced summary tiles with neumorphic styling
            Row(
                modifier = Modifier.fillMaxWidth(), 
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                MetricTile(label = "Answered", value = "128", modifier = Modifier.weight(1f))
                MetricTile(label = "Auto-Replies", value = "124", modifier = Modifier.weight(1f))
                MetricTile(label = "Discrepancies", value = "4", modifier = Modifier.weight(1f))
            }

            // Enhanced verification progress with neumorphic styling
            NeumorphicCard(
                modifier = Modifier.fillMaxWidth(),
                cornerRadius = 20.dp,
                elevation = 8.dp
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Verification Progress", 
                            style = MaterialTheme.typography.titleMedium, 
                            color = NeoPrimaryText,
                            fontWeight = FontWeight.Bold
                        )
                        NeumorphicBadge(
                            text = "86%",
                            backgroundColor = NeoAccent,
                            textColor = Color.White
                        )
                    }
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically, 
                        horizontalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        Box(
                            modifier = Modifier.size(80.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                progress = { 0.86f },
                                color = NeoAccent,
                                trackColor = NeoSurface.copy(alpha = 0.3f),
                                strokeWidth = 8.dp,
                                modifier = Modifier.size(72.dp)
                            )
                            Text(
                                text = "86%",
                                color = NeoPrimaryText,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Column {
                            LinearProgressIndicator(
                                progress = { 0.86f },
                                modifier = Modifier.fillMaxWidth(),
                                color = NeoAccent,
                                trackColor = NeoSurface.copy(alpha = 0.3f)
                            )
                            Spacer(Modifier.size(8.dp))
                            Text(
                                text = "Last run: 15 min ago", 
                                color = NeoSecondaryText,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }

            // Enhanced recent activity with neumorphic styling
            NeumorphicCard(
                modifier = Modifier.fillMaxWidth(),
                cornerRadius = 20.dp,
                elevation = 8.dp
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Recent Activity", 
                            style = MaterialTheme.typography.titleMedium, 
                            color = NeoPrimaryText,
                            fontWeight = FontWeight.Bold
                        )
                        NeumorphicBadge(
                            text = "3 new",
                            backgroundColor = NeoAccent,
                            textColor = Color.White
                        )
                    }
                    ActivityItem("Auto-reply sent to John Doe", "2 min ago")
                    ActivityItem("Verification completed", "5 min ago") 
                    ActivityItem("New contact added", "12 min ago")
                }
            }

            // Enhanced action buttons with neumorphic styling
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                NeumorphicButton(
                    text = "Export Report",
                    onClick = { /*TODO*/ }, 
                    modifier = Modifier.weight(1f),
                    cornerRadius = 16.dp
                )
                NeumorphicButton(
                    text = "Schedule",
                    onClick = { /*TODO*/ }, 
                    modifier = Modifier.weight(1f),
                    cornerRadius = 16.dp
                )
            }
        }
    }
}

@Composable
private fun ActivityItem(title: String, time: String) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp), 
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        NeumorphicCard(
            modifier = Modifier.size(12.dp),
            cornerRadius = 6.dp,
            elevation = 2.dp,
            backgroundColor = NeoAccent
        ) {
            Box(modifier = Modifier.fillMaxSize())
        }
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = title, 
                style = MaterialTheme.typography.bodyMedium, 
                color = NeoPrimaryText,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = time, 
                style = MaterialTheme.typography.bodySmall, 
                color = NeoSecondaryText,
                fontWeight = FontWeight.Normal
            )
        }
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = "More",
            tint = NeoSecondaryText,
            modifier = Modifier.size(16.dp)
        )
    }
}

@Composable
private fun MetricTile(label: String, value: String, modifier: Modifier = Modifier) {
    NeumorphicCard(
        modifier = modifier,
        cornerRadius = 16.dp,
        elevation = 6.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp), 
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value, 
                style = MaterialTheme.typography.headlineSmall, 
                color = NeoAccent,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = label, 
                style = MaterialTheme.typography.bodySmall, 
                color = NeoSecondaryText,
                fontWeight = FontWeight.Medium
            )
        }
    }
}