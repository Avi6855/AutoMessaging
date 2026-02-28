package com.avinashpatil.app.automessage.di

import android.app.Application
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import com.avinashpatil.app.automessage.data.database.AutoMessageDatabase
import com.avinashpatil.app.automessage.data.repository.AutoReplyRepository
import com.avinashpatil.app.automessage.data.repository.AutoReplyRepositoryImpl
import com.avinashpatil.app.automessage.data.repository.ContactRepository
import com.avinashpatil.app.automessage.data.repository.ContactRepositoryImpl
import com.avinashpatil.app.automessage.data.repository.DataStoreRepository
import com.avinashpatil.app.automessage.data.repository.DataStoreRepositoryImpl
import com.avinashpatil.app.automessage.data.repository.GroupRepository
import com.avinashpatil.app.automessage.data.repository.GroupRepositoryImpl
import com.avinashpatil.app.automessage.data.repository.MessageRepository
import com.avinashpatil.app.automessage.data.repository.MessageRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

private val Application.dataStore by preferencesDataStore(name = "auto_message_preferences")

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    
    @Provides
    @Singleton
    fun provideAutoMessageDatabase(app: Application): AutoMessageDatabase {
        return Room.databaseBuilder(
            app,
            AutoMessageDatabase::class.java,
            "auto_message_db"
        ).fallbackToDestructiveMigration()
            .build()
    }
    
    @Provides
    @Singleton
    fun provideContactRepository(database: AutoMessageDatabase): ContactRepository {
        return ContactRepositoryImpl(database.contactDao())
    }
    
    @Provides
    @Singleton
    fun provideMessageRepository(database: AutoMessageDatabase): MessageRepository {
        return MessageRepositoryImpl(database.customMessageDao())
    }
    
    @Provides
    @Singleton
    fun provideAutoReplyRepository(database: AutoMessageDatabase): AutoReplyRepository {
        return AutoReplyRepositoryImpl(database.autoReplyLogDao(), database.lastSeenCallDao())
    }
    
    @Provides
    @Singleton
    fun provideDiscrepancyRepository(database: AutoMessageDatabase): com.avinashpatil.app.automessage.data.repository.DiscrepancyRepository {
        return com.avinashpatil.app.automessage.data.repository.DiscrepancyRepositoryImpl(database.discrepancyLogDao())
    }
    
    @Provides
    @Singleton
    fun provideGroupRepository(database: AutoMessageDatabase): GroupRepository {
        return GroupRepositoryImpl(database.groupDao())
    }
    
    @Provides
    @Singleton
    fun provideDataStore(app: Application): DataStore<Preferences> {
        return app.dataStore
    }
    
    @Provides
    @Singleton
    fun provideDataStoreRepository(dataStore: DataStore<Preferences>): DataStoreRepository {
        return DataStoreRepositoryImpl(dataStore)
    }
}