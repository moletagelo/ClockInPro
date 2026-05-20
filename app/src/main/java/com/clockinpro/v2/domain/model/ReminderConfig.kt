package com.clockinpro.v2.domain.model

data class ReminderConfig(
    val enabled: Boolean = false,
    val hour: Int = 9,
    val minute: Int = 0
)
