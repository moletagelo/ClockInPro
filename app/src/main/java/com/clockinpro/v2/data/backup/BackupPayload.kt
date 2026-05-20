package com.clockinpro.v2.data.backup

data class BackupPayload(
    val version: Int = 2,
    val exportedAt: String,
    val targets: List<BackupTarget>,
    val completions: List<BackupCompletion>
)

data class BackupTarget(
    val id: Long,
    val name: String,
    val iconKey: String,
    val colorKey: String,
    val reminderEnabled: Boolean,
    val reminderHour: Int,
    val reminderMinute: Int,
    val createdAt: Long,
    val sortOrder: Int
)

data class BackupCompletion(
    val id: Long,
    val targetId: Long,
    val dateKey: String,
    val completedAt: Long
)
