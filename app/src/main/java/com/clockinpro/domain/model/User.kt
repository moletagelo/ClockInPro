package com.clockinpro.domain.model

data class User(
    val id: Long = 0,
    val phone: String,
    val passwordHash: String = "",
    val nickname: String = "",
    val avatarUrl: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
