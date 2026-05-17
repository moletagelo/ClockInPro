package com.clockinpro.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class RepeatType {
    DAILY,
    WEEKDAY
}

@Entity(tableName = "reminders")
data class ReminderEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: Long,
    val timeHour: Int,
    val timeMinute: Int,
    val repeatType: RepeatType = RepeatType.DAILY,
    val isEnabled: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)
