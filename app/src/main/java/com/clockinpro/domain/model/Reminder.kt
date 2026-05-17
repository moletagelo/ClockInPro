package com.clockinpro.domain.model

data class Reminder(
    val id: Long = 0,
    val userId: Long,
    val timeHour: Int,
    val timeMinute: Int,
    val repeatType: RepeatType = RepeatType.DAILY,
    val isEnabled: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)

enum class RepeatType {
    DAILY,
    WEEKDAY
}
