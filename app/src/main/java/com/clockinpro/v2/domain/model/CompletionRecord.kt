package com.clockinpro.v2.domain.model

import java.time.LocalDate

data class CompletionRecord(
    val id: Long,
    val targetId: Long,
    val date: LocalDate,
    val completedAt: Long
)
