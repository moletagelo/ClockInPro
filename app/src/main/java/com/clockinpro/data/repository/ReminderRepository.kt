package com.clockinpro.data.repository

import com.clockinpro.data.local.ReminderDao
import com.clockinpro.data.local.ReminderEntity
import com.clockinpro.data.local.RepeatType as LocalRepeatType
import com.clockinpro.domain.model.Reminder
import com.clockinpro.domain.model.RepeatType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReminderRepository @Inject constructor(
    private val reminderDao: ReminderDao
) {
    fun getAllReminders(userId: Long): Flow<List<Reminder>> {
        return reminderDao.getAllRemindersByUser(userId).map { list ->
            list.map { it.toDomain() }
        }
    }

    suspend fun getEnabledReminders(userId: Long): List<Reminder> {
        return reminderDao.getEnabledReminders(userId).map { it.toDomain() }
    }

    suspend fun getReminderById(reminderId: Long): Reminder? {
        return reminderDao.getReminderById(reminderId)?.toDomain()
    }

    suspend fun insertReminder(reminder: Reminder): Long {
        return reminderDao.insertReminder(reminder.toEntity())
    }

    suspend fun updateReminder(reminder: Reminder) {
        reminderDao.updateReminder(reminder.toEntity())
    }

    suspend fun setReminderEnabled(reminderId: Long, enabled: Boolean) {
        reminderDao.setReminderEnabled(reminderId, enabled)
    }

    suspend fun deleteReminder(reminder: Reminder) {
        reminderDao.deleteReminder(reminder.toEntity())
    }

    private fun ReminderEntity.toDomain() = Reminder(
        id = id,
        userId = userId,
        timeHour = timeHour,
        timeMinute = timeMinute,
        repeatType = when (repeatType) {
            LocalRepeatType.DAILY -> RepeatType.DAILY
            LocalRepeatType.WEEKDAY -> RepeatType.WEEKDAY
        },
        isEnabled = isEnabled,
        createdAt = createdAt
    )

    private fun Reminder.toEntity() = ReminderEntity(
        id = id,
        userId = userId,
        timeHour = timeHour,
        timeMinute = timeMinute,
        repeatType = when (repeatType) {
            RepeatType.DAILY -> LocalRepeatType.DAILY
            RepeatType.WEEKDAY -> LocalRepeatType.WEEKDAY
        },
        isEnabled = isEnabled,
        createdAt = createdAt
    )
}
