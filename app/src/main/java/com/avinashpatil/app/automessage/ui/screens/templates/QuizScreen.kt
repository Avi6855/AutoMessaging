package com.avinashpatil.app.automessage.ui.screens.templates

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import com.avinashpatil.app.automessage.ui.components.GlassCard
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
fun QuizScreen(
    onBack: () -> Unit = {},
    onSubmit: () -> Unit = {}
) {
    var activeTab by remember { mutableStateOf(1) } // 1-5
    Scaffold(
        modifier = Modifier
            .background(NeoLightBackground)
            .windowInsetsPadding(WindowInsets.statusBars)
            .windowInsetsPadding(WindowInsets.navigationBars),
        topBar = {
            StandardTopAppBar(
                title = "Quiz",
                showBackButton = true,
                onBackClick = onBack,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                actions = {
                    NeumorphicBadge(
                        text = "Training",
                        backgroundColor = NeoAccent,
                        textColor = Color.White
                    )
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Enhanced numbered tabs with neumorphic styling
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp), 
                modifier = Modifier.fillMaxWidth()
            ) {
                (1..5).forEach { i ->
                    val isActive = i == activeTab
                    NeumorphicCard(
                        modifier = Modifier.weight(1f),
                        cornerRadius = 16.dp,
                        elevation = if (isActive) 8.dp else 4.dp,
                        backgroundColor = if (isActive) NeoAccent else NeoSurface.copy(alpha = 0.8f)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = i.toString(),
                                color = if (isActive) Color.White else NeoPrimaryText,
                                fontWeight = if (isActive) FontWeight.Bold else FontWeight.Medium,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
            }

            // Enhanced question card with neumorphic styling
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
                        NeumorphicBadge(
                            text = "Question 1",
                            backgroundColor = NeoAccent,
                            textColor = Color.White
                        )
                        Text(
                            text = "1/5",
                            color = NeoSecondaryText,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    
                    Text(
                        text = "What is the default behavior after an answered call?",
                        style = MaterialTheme.typography.titleLarge,
                        color = NeoPrimaryText,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Text(
                        text = "Choose the best answer that describes the system's default response.",
                        color = NeoSecondaryText,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    
                    listOf(
                        "Auto-send default message",
                        "Do nothing",
                        "Open messages app",
                        "Log only"
                    ).forEach { option ->
                        NeumorphicCard(
                            modifier = Modifier.fillMaxWidth(),
                            cornerRadius = 16.dp,
                            elevation = 4.dp,
                            backgroundColor = NeoSurface.copy(alpha = 0.3f)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                NeumorphicCard(
                                    modifier = Modifier.size(24.dp),
                                    cornerRadius = 12.dp,
                                    elevation = 4.dp,
                                    backgroundColor = NeoSurface.copy(alpha = 0.3f)
                                ) {
                                    Box(
                                        modifier = Modifier.fillMaxSize(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(8.dp)
                                                .clip(CircleShape)
                                                .background(NeoAccent)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.size(12.dp))
                                Text(
                                    option, 
                                    color = NeoPrimaryText,
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }

            // Enhanced submit button with neumorphic styling
            NeumorphicButton(
                text = "Submit Answer",
                onClick = onSubmit,
                modifier = Modifier.fillMaxWidth(),
                cornerRadius = 20.dp
            )
        }
    }
}