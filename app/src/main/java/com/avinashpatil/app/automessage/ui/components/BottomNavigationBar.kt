package com.avinashpatil.app.automessage.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.MaterialTheme
import com.avinashpatil.app.automessage.ui.theme.NeoSoftShadow
import com.avinashpatil.app.automessage.ui.theme.NeoLightShadow
import com.avinashpatil.app.automessage.ui.theme.NeoSurface
import com.avinashpatil.app.automessage.ui.theme.NeoAccent
import com.avinashpatil.app.automessage.ui.theme.NeoSecondaryText
import com.avinashpatil.app.automessage.ui.theme.NeoLightBackground
import com.avinashpatil.app.automessage.ui.theme.NeoPrimaryText
import com.avinashpatil.app.automessage.ui.theme.NeumorphicCard

@Composable
fun BottomNavigationBar(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val items = listOf(
        BottomNavItem.Recent,
        BottomNavItem.Contacts,
        BottomNavItem.Messages,
        //BottomNavItem.Settings
    )
    
    // Neumorphic bottom navigation container
    NeumorphicCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .navigationBarsPadding(),
        cornerRadius = 24.dp,
        elevation = 8.dp,
        backgroundColor = NeoSurface
    ) {
        NavigationBar(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            containerColor = Color.Transparent,
            tonalElevation = 0.dp
        ) {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route
            
            items.forEach { item ->
                val isSelected = currentRoute == item.route
                val scale by animateFloatAsState(
                    targetValue = if (isSelected) 1.1f else 1.0f,
                    animationSpec = tween(300),
                    label = "scale"
                )
                val iconColor by animateColorAsState(
                    targetValue = if (isSelected) NeoAccent else NeoSecondaryText,
                    animationSpec = tween(300),
                    label = "iconColor"
                )
                val textColor by animateColorAsState(
                    targetValue = if (isSelected) NeoAccent else NeoSecondaryText,
                    animationSpec = tween(300),
                    label = "textColor"
                )
                
                NavigationBarItem(
                    icon = {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.title,
                            tint = iconColor,
                            modifier = Modifier.scale(scale)
                        )
                    },
                    label = { 
                        Text(
                            text = item.title,
                            color = textColor,
                            fontSize = 12.sp
                        ) 
                    },
                    selected = isSelected,
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = Color.Transparent,
                        selectedIconColor = NeoAccent,
                        selectedTextColor = NeoAccent,
                        unselectedIconColor = NeoSecondaryText,
                        unselectedTextColor = NeoSecondaryText
                    ),
                    onClick = {
                        navController.navigate(item.route) {
                            navController.graph.startDestinationRoute?.let { route ->
                                popUpTo(route) {
                                    saveState = true
                                }
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    }
}

sealed class BottomNavItem(
    val title: String,
    val icon: ImageVector,
    val route: String
) {
    object Recent : BottomNavItem(
        title = "Recent",
        icon = Icons.Filled.History,
        route = "recent"
    )
    
    object Contacts : BottomNavItem(
        title = "Contacts",
        icon = Icons.Filled.Person,
        route = "contacts"
    )
    
    object Messages : BottomNavItem(
        title = "Messages",
        icon = Icons.AutoMirrored.Filled.Message,
        route = "messages"
    )
    /*
    object Settings : BottomNavItem(
        title = "Settings",
        icon = Icons.Filled.Settings,
        route = "settings"
    )

     */
}