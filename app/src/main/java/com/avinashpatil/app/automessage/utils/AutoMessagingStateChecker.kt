package com.avinashpatil.app.automessage.utils

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

val Context.dataStore by preferencesDataStore(name = "auto_message_preferences")

object AutoMessagingStateChecker {
    private val AUTO_MESSAGING_ENABLED = booleanPreferencesKey("auto_messaging_enabled")

    @Volatile
    private var cachedEnabled: Boolean = true

    fun init(context: Context) {
        runBlocking(Dispatchers.IO) {
            try {
                context.dataStore.data.first().let { prefs ->
                    cachedEnabled = prefs[AUTO_MESSAGING_ENABLED] ?: true
                }
            } catch (_: Exception) {
                cachedEnabled = true
            }
        }
    }

    fun syncEnabled(context: Context) {
        try {
            runBlocking(Dispatchers.IO) {
                context.dataStore.data.first().let { prefs ->
                    cachedEnabled = prefs[AUTO_MESSAGING_ENABLED] ?: true
                }
            }
        } catch (_: Exception) {}
    }

    fun isAutoMessagingEnabled(context: Context): Boolean {
        return cachedEnabled
    }
}
