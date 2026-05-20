package com.clockinpro.v2.domain.model

data class TargetSummary(
    val target: Target,
    val isCompletedToday: Boolean,
    val completedAt: Long?
)
