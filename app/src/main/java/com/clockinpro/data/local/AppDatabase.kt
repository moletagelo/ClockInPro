package com.clockinpro.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.clockinpro.v2.data.local.CompletionDao
import com.clockinpro.v2.data.local.CompletionEntity
import com.clockinpro.v2.data.local.TargetDao
import com.clockinpro.v2.data.local.TargetEntity

@Database(
    entities = [
        UserEntity::class,
        CheckRecordEntity::class,
        ReminderEntity::class,
        TargetEntity::class,
        CompletionEntity::class
    ],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun checkRecordDao(): CheckRecordDao
    abstract fun reminderDao(): ReminderDao
    abstract fun targetDao(): TargetDao
    abstract fun completionDao(): CompletionDao
}
