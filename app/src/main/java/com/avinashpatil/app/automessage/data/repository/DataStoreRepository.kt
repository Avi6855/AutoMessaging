package com.avinashpatil.app.automessage.data.repository

import kotlinx.coroutines.flow.Flow

interface DataStoreRepository {
    suspend fun saveAutoReplyEnabled(enabled: Boolean)
    fun isAutoReplyEnabled(): Flow<Boolean>
    suspend fun saveAutoReplyDelay(delaySeconds: Int)
    fun getAutoReplyDelay(): Flow<Int>
    suspend fun saveDefaultMessageId(messageId: Long)
    fun getDefaultMessageId(): Flow<Long>
    suspend fun saveDarkModeEnabled(enabled: Boolean)
    fun isDarkModeEnabled(): Flow<Boolean>
    // Group auto-reply toggle
    suspend fun saveGroupAutoReplyEnabled(enabled: Boolean)
    fun isGroupAutoReplyEnabled(): Flow<Boolean>
    suspend fun saveFirstTimeUser(isFirstTime: Boolean)
    fun isFirstTimeUser(): Flow<Boolean>
    // Notification sound preference
    suspend fun saveNotificationSoundEnabled(enabled: Boolean)
    fun isNotificationSoundEnabled(): Flow<Boolean>
    suspend fun clearAllPreferences()
}
