package com.clockinpro.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ReminderDao {
    @Query("SELECT * FROM reminders WHERE userId = :userId ORDER BY timeHour, timeMinute")
    fun getAllRemindersByUser(userId: Long): Flow<List<ReminderEntity>>

    @Query("SELECT * FROM reminders WHERE userId = :userId AND isEnabled = 1 ORDER BY timeHour, timeMinute")
    suspend fun getEnabledReminders(userId: Long): List<ReminderEntity>

    @Query("SELECT * FROM reminders WHERE id = :reminderId LIMIT 1")
    suspend fun getReminderById(reminderId: Long): ReminderEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReminder(reminder: ReminderEntity): Long

    @Update
    suspend fun updateReminder(reminder: ReminderEntity)

    @Query("UPDATE reminders SET isEnabled = :enabled WHERE id = :reminderId")
    suspend fun setReminderEnabled(reminderId: Long, enabled: Boolean)

    @Delete
    suspend fun deleteReminder(reminder: ReminderEntity)

    @Query("DELETE FROM reminders WHERE userId = :userId")
    suspend fun deleteAllRemindersByUser(userId: Long)
}
