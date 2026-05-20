package com.clockinpro.v2.domain.model

data class SaveTargetRequest(
    val id: Long? = null,
    val name: String,
    val iconKey: String,
    val colorKey: String,
    val reminder: ReminderConfig
)
