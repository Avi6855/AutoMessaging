package com.avinashpatil.app.automessage.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.avinashpatil.app.automessage.ui.screens.recent.RecentScreen
import com.avinashpatil.app.automessage.ui.screens.contacts.ContactsScreen
import com.avinashpatil.app.automessage.ui.screens.messages.MessagesScreen
import com.avinashpatil.app.automessage.ui.screens.settings.SettingsScreen
import com.avinashpatil.app.automessage.ui.screens.settings.ReliabilityChecklistScreen
import com.avinashpatil.app.automessage.ui.screens.recent.MessageDetailScreen
import com.avinashpatil.app.automessage.ui.screens.groups.GroupDetailsScreen

@Composable
fun NavigationGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = "recent",
        modifier = modifier
    ) {
        composable("recent") {
            RecentScreen(navController = navController)
        }
        composable("contacts") {
            ContactsScreen(navController = navController)
        }
        composable("messages") {
            MessagesScreen()
        }
        composable("settings") {
            SettingsScreen(navController = navController)
        }
        composable("reliability") {
            ReliabilityChecklistScreen(navController = navController)
        }
        // Message detail route
        composable(
            route = "message_detail/{logId}",
            arguments = listOf(
                navArgument("logId") { type = NavType.LongType }
            )
        ) {
            MessageDetailScreen(navController = navController)
        }
        // Group details route
        composable(
            route = "group_details/{groupId}",
            arguments = listOf(
                navArgument("groupId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val groupId = backStackEntry.arguments?.getLong("groupId") ?: 0L
            GroupDetailsScreen(
                navController = navController,
                groupId = groupId
            )
        }
    }
}
