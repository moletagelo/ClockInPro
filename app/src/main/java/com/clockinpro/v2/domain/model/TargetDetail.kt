package com.clockinpro.v2.domain.model

data class TargetDetail(
    val target: Target,
    val stats: TargetStats,
    val completions: List<CompletionRecord>
)
