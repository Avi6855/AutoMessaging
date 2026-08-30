package com.avinashpatil.app.automessage.utils

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

val Context.dataStore by preferencesDataStore(name = "auto_message_preferences")

object AutoMessagingStateChecker {
    private val AUTO_MESSAGING_ENABLED = booleanPreferencesKey("auto_messaging_enabled")

    fun isAutoMessagingEnabled(context: Context): Boolean {
        return try {
            runBlocking {
                context.dataStore.data.first()[AUTO_MESSAGING_ENABLED] ?: true
            }
        } catch (e: Exception) {
            true
        }
    }
}
