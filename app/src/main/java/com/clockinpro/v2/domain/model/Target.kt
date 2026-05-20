package com.clockinpro.v2.domain.model

data class Target(
    val id: Long,
    val name: String,
    val iconKey: String,
    val colorKey: String,
    val reminder: ReminderConfig,
    val createdAt: Long,
    val sortOrder: Int
)
