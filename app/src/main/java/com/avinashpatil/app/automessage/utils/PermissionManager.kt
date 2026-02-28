package com.avinashpatil.app.automessage.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat

object PermissionManager {
    
    // Core permissions required for auto messaging to function
    val REQUIRED_PERMISSIONS = mutableListOf(
        Manifest.permission.READ_CONTACTS,
        Manifest.permission.READ_CALL_LOG,
        Manifest.permission.READ_PHONE_STATE,
        Manifest.permission.SEND_SMS,
    ).apply {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            add(Manifest.permission.POST_NOTIFICATIONS)
        }
    }.toList()
    
    // Optional/supporting permissions. On some OEMs <= Android 14 these
    // can improve SMS delivery tracking, but they are often restricted on
    // Android 15. We will request them only when beneficial and available.
    val OPTIONAL_SMS_PERMISSIONS = listOf(
        Manifest.permission.RECEIVE_SMS,
        Manifest.permission.READ_SMS
    )
    
    val STORAGE_PERMISSIONS = listOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
    
    fun hasPermissions(context: Context, permissions: List<String>): Boolean {
        return permissions.all { permission ->
            ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
        }
    }
    
    fun hasRequiredPermissions(context: Context): Boolean {
        return hasPermissions(context, REQUIRED_PERMISSIONS)
    }
    
    fun hasStoragePermissions(context: Context): Boolean {
        return hasPermissions(context, STORAGE_PERMISSIONS)
    }
    
    fun getMissingPermissions(context: Context, permissions: List<String>): List<String> {
        return permissions.filter { permission ->
            ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED
        }
    }
    
    fun getMissingRequiredPermissions(context: Context): List<String> {
        return getMissingPermissions(context, REQUIRED_PERMISSIONS)
    }
    
    fun getMissingOptionalSmsPermissions(context: Context): List<String> {
        return getMissingPermissions(context, OPTIONAL_SMS_PERMISSIONS)
    }
    
    fun getMissingStoragePermissions(context: Context): List<String> {
        return getMissingPermissions(context, STORAGE_PERMISSIONS)
    }
    
    fun getPermissionRationale(permission: String): String {
        return when (permission) {
            Manifest.permission.READ_CONTACTS -> "This permission is needed to access your contacts for auto-reply functionality."
            Manifest.permission.READ_CALL_LOG -> "This permission is needed to detect missed calls for auto-reply."
            Manifest.permission.READ_PHONE_STATE -> "This permission is needed to monitor phone state changes."
            Manifest.permission.SEND_SMS -> "This permission is needed to send auto-reply messages."
            Manifest.permission.RECEIVE_SMS -> "This permission is needed to monitor SMS status."
            Manifest.permission.READ_SMS -> "This permission is needed to read SMS status and improve reliability on some devices."
            Manifest.permission.READ_EXTERNAL_STORAGE -> "This permission is needed to export message history."
            Manifest.permission.WRITE_EXTERNAL_STORAGE -> "This permission is needed to save exported data."
            Manifest.permission.POST_NOTIFICATIONS -> "This permission is needed to show notifications."
            else -> "This permission is required for the app to function properly."
        }
    }
}