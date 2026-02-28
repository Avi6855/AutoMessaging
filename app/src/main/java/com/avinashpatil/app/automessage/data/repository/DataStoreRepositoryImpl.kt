package com.avinashpatil.app.automessage.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataStoreRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : DataStoreRepository {
    
    companion object {
        val AUTO_REPLY_ENABLED = booleanPreferencesKey("auto_reply_enabled")
        val AUTO_REPLY_DELAY = intPreferencesKey("auto_reply_delay")
        val DEFAULT_MESSAGE_ID = longPreferencesKey("default_message_id")
        val DARK_MODE_ENABLED = booleanPreferencesKey("dark_mode_enabled")
        val FIRST_TIME_USER = booleanPreferencesKey("first_time_user")
        // New: group auto-reply toggle
        val GROUP_AUTO_REPLY_ENABLED = booleanPreferencesKey("group_auto_reply_enabled")
        // Notification sound toggle
        val NOTIFICATION_SOUND_ENABLED = booleanPreferencesKey("notification_sound_enabled")
        
        const val DEFAULT_DELAY_SECONDS = 10
    }

    override suspend fun saveAutoReplyEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[AUTO_REPLY_ENABLED] = enabled
        }
    }
    
    override fun isAutoReplyEnabled(): Flow<Boolean> {
        return dataStore.data.map { preferences ->
            preferences[AUTO_REPLY_ENABLED] ?: false
        }
    }
    
    override suspend fun saveAutoReplyDelay(delaySeconds: Int) {
        dataStore.edit { preferences ->
            preferences[AUTO_REPLY_DELAY] = delaySeconds
        }
    }
    
    override fun getAutoReplyDelay(): Flow<Int> {
        return dataStore.data.map { preferences ->
            preferences[AUTO_REPLY_DELAY] ?: DEFAULT_DELAY_SECONDS
        }
    }
    
    override suspend fun saveDefaultMessageId(messageId: Long) {
        dataStore.edit { preferences ->
            preferences[DEFAULT_MESSAGE_ID] = messageId
        }
    }
    
    override fun getDefaultMessageId(): Flow<Long> {
        return dataStore.data.map { preferences ->
            preferences[DEFAULT_MESSAGE_ID] ?: -1L
        }
    }
    
    override suspend fun saveDarkModeEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[DARK_MODE_ENABLED] = enabled
        }
    }
    
    override fun isDarkModeEnabled(): Flow<Boolean> {
        return dataStore.data.map { preferences ->
            preferences[DARK_MODE_ENABLED] ?: false
        }
    }
    
    // New: group auto-reply toggle impl
    override suspend fun saveGroupAutoReplyEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[GROUP_AUTO_REPLY_ENABLED] = enabled
        }
    }
    
    override fun isGroupAutoReplyEnabled(): Flow<Boolean> {
        return dataStore.data.map { preferences ->
            preferences[GROUP_AUTO_REPLY_ENABLED] ?: false
        }
    }
    
    override suspend fun saveFirstTimeUser(isFirstTime: Boolean) {
        dataStore.edit { preferences ->
            preferences[FIRST_TIME_USER] = isFirstTime
        }
    }
    
    override fun isFirstTimeUser(): Flow<Boolean> {
        return dataStore.data.map { preferences ->
            preferences[FIRST_TIME_USER] ?: true
        }
    }

    override suspend fun saveNotificationSoundEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[NOTIFICATION_SOUND_ENABLED] = enabled
        }
    }

    override fun isNotificationSoundEnabled(): Flow<Boolean> {
        return dataStore.data.map { preferences ->
            preferences[NOTIFICATION_SOUND_ENABLED] ?: true
        }
    }
    
    override suspend fun clearAllPreferences() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}
