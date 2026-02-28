package com.avinashpatil.app.automessage.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.avinashpatil.app.automessage.ui.theme.NeoAccent
import com.avinashpatil.app.automessage.ui.theme.NeoCardBackground
import com.avinashpatil.app.automessage.ui.theme.NeoPrimaryText
import com.avinashpatil.app.automessage.ui.theme.NeoSurface

/**
 * Standardized Top App Bar component with consistent styling across all screens
 * Height: 64dp (Material Design standard)
 * Corner radius: 16dp
 * Elevation: 4dp
 */
@Composable
fun StandardTopAppBar(
    title: String,
    modifier: Modifier = Modifier,
    showBackButton: Boolean = false,
    showMenuButton: Boolean = false,
    onBackClick: () -> Unit = {},
    onMenuClick: () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {},
    backgroundColor: Color = NeoSurface,
    contentColor: Color = NeoPrimaryText
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left navigation buttons
            if (showBackButton) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = contentColor,
                        modifier = Modifier.size(24.dp)
                    )
                }
            } else if (showMenuButton) {
                IconButton(
                    onClick = onMenuClick,
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "Menu",
                        tint = contentColor,
                        modifier = Modifier.size(24.dp)
                    )
                }
            } else {
                Spacer(modifier = Modifier.width(8.dp))
            }

            // Title
            Text(
                text = title,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = contentColor,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp)
            )

            // Actions
            Row(
                modifier = Modifier.wrapContentWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                actions()
            }
        }
    }
}

/**
 * Simplified version of StandardTopAppBar for screens that only need title and back button
 */
@Composable
fun SimpleTopAppBar(
    title: String,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color = NeoSurface,
    contentColor: Color = NeoPrimaryText
) {
    StandardTopAppBar(
        title = title,
        modifier = modifier,
        showBackButton = true,
        onBackClick = onBackClick,
        backgroundColor = backgroundColor,
        contentColor = contentColor
    )
}

/**
 * Top App Bar with menu button for main screens
 */
@Composable
fun MainTopAppBar(
    title: String,
    onMenuClick: () -> Unit,
    modifier: Modifier = Modifier,
    actions: @Composable RowScope.() -> Unit = {},
    backgroundColor: Color = NeoSurface,
    contentColor: Color = NeoPrimaryText
) {
    StandardTopAppBar(
        title = title,
        modifier = modifier,
        showMenuButton = true,
        onMenuClick = onMenuClick,
        actions = actions,
        backgroundColor = backgroundColor,
        contentColor = contentColor
    )
}