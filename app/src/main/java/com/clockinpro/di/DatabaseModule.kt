package com.clockinpro.di

import android.content.Context
import androidx.room.Room
import com.clockinpro.data.local.AppDatabase
import com.clockinpro.data.local.CheckRecordDao
import com.clockinpro.data.local.ReminderDao
import com.clockinpro.data.local.UserDao
import com.clockinpro.v2.data.local.CompletionDao
import com.clockinpro.v2.data.local.TargetDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "clockin_database"
        ).fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideUserDao(database: AppDatabase): UserDao {
        return database.userDao()
    }

    @Provides
    fun provideCheckRecordDao(database: AppDatabase): CheckRecordDao {
        return database.checkRecordDao()
    }

    @Provides
    fun provideReminderDao(database: AppDatabase): ReminderDao {
        return database.reminderDao()
    }

    @Provides
    fun provideTargetDao(database: AppDatabase): TargetDao {
        return database.targetDao()
    }

    @Provides
    fun provideCompletionDao(database: AppDatabase): CompletionDao {
        return database.completionDao()
    }
}
