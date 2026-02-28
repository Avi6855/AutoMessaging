package com.avinashpatil.app.automessage.data.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import com.avinashpatil.app.automessage.data.dao.*
import com.avinashpatil.app.automessage.data.entity.*

@Database(
    entities = [
        ContactEntity::class,
        GroupEntity::class,
        CustomMessageEntity::class,
        AutoReplyLogEntity::class,
        PriorityEntity::class,
        BlacklistEntity::class,
        LastSeenCallEntity::class,
        DiscrepancyLogEntity::class
    ],
    version = 4,
    exportSchema = true
)
abstract class AutoMessageDatabase : RoomDatabase() {
    
    abstract fun contactDao(): ContactDao
    abstract fun groupDao(): GroupDao
    abstract fun customMessageDao(): CustomMessageDao
    abstract fun autoReplyLogDao(): AutoReplyLogDao
    abstract fun priorityDao(): PriorityDao
    abstract fun blacklistDao(): BlacklistDao
    abstract fun lastSeenCallDao(): LastSeenCallDao
    abstract fun discrepancyLogDao(): DiscrepancyLogDao
    
    companion object {
        const val DATABASE_NAME = "auto_message_db"
        
        @Volatile
        private var INSTANCE: AutoMessageDatabase? = null
        
        fun getInstance(context: Context): AutoMessageDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AutoMessageDatabase::class.java,
                    DATABASE_NAME
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}