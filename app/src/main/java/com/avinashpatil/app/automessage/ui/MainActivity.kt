package com.avinashpatil.app.automessage.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.graphics.Brush
import com.avinashpatil.app.automessage.navigation.NavigationGraph
import com.avinashpatil.app.automessage.ui.components.BottomNavigationBar
import com.avinashpatil.app.automessage.ui.theme.AutoMessageTheme
import com.avinashpatil.app.automessage.utils.PermissionManager
import com.avinashpatil.app.automessage.ui.theme.NeoLightBackground
import com.avinashpatil.app.automessage.ui.theme.NeoSurface
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.all { it.value }
        if (allGranted) {
            try {
                android.widget.Toast.makeText(this, "✅ SMS permission granted successfully.", android.widget.Toast.LENGTH_SHORT).show()
            } catch (_: Exception) { }
            // Start service so auto-reply works without opening app
            try {
                val serviceIntent = android.content.Intent(this, com.avinashpatil.app.automessage.service.CallDetectionService::class.java)
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    startForegroundService(serviceIntent)
                } else {
                    startService(serviceIntent)
                }
            } catch (_: Exception) { }
        } else {
            // If any permission denied, open app settings to grant permanently
            try {
                val rm = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                    getSystemService(android.app.role.RoleManager::class.java)
                } else null
                if (rm != null && rm.isRoleAvailable(android.app.role.RoleManager.ROLE_SMS) && !rm.isRoleHeld(android.app.role.RoleManager.ROLE_SMS)) {
                    val intent = rm.createRequestRoleIntent(android.app.role.RoleManager.ROLE_SMS)
                    startActivity(intent)
                } else {
                    val intent = android.content.Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = android.net.Uri.parse("package:" + packageName)
                    }
                    startActivity(intent)
                }
            } catch (_: Exception) { }
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            AutoMessageTheme {
                MainScreen()
            }
        }
        
        // Request permissions
        requestPermissions()
    }
    
    private fun requestPermissions() {
        val missingPermissions = PermissionManager.getMissingRequiredPermissions(this)
        if (missingPermissions.isNotEmpty()) {
            try { permissionLauncher.launch(missingPermissions.toTypedArray()) } catch (_: Exception) {}
            // On Android 10+ also prompt user to set app as Default SMS to bypass
            // OEM restrictions and improve reliability
            try {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                    val rm = getSystemService(android.app.role.RoleManager::class.java)
                    if (rm.isRoleAvailable(android.app.role.RoleManager.ROLE_SMS) && !rm.isRoleHeld(android.app.role.RoleManager.ROLE_SMS)) {
                        val intent = rm.createRequestRoleIntent(android.app.role.RoleManager.ROLE_SMS)
                        startActivity(intent)
                    }
                }
            } catch (_: Exception) { }
        }
    }
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    var showBottomNav by remember { mutableStateOf(true) }
    
    LaunchedEffect(navController) {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            showBottomNav = when (destination.route) {
                "recent", "contacts", "messages", "settings" -> true
                else -> false
            }
        }
    }
    
    // Neumorphic gradient background
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        NeoLightBackground,
                        NeoSurface
                    )
                )
            )
    ) {
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.statusBars)
                .windowInsetsPadding(WindowInsets.navigationBars),
            containerColor = androidx.compose.ui.graphics.Color.Transparent,
            bottomBar = {
                if (showBottomNav) {
                    BottomNavigationBar(navController = navController)
                }
            }
        ) { paddingValues ->
            NavigationGraph(
                navController = navController,
                modifier = Modifier.padding(paddingValues)
            )
        }
    }
}