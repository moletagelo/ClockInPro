package com.clockinpro.data.repository

import com.clockinpro.data.local.*
import com.clockinpro.domain.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val userDao: UserDao,
    private val preferencesManager: PreferencesManager
) {
    suspend fun getUserByPhone(phone: String): User? {
        return userDao.getUserByPhone(phone)?.toDomain()
    }

    suspend fun getUserById(userId: Long): User? {
        return userDao.getUserById(userId)?.toDomain()
    }

    fun observeCurrentUser(): Flow<User?> {
        return preferencesManager.currentUserId.map { userId ->
            userId?.let { userDao.getUserById(it)?.toDomain() }
        }
    }

    suspend fun insertUser(user: User): Long {
        return userDao.insertUser(user.toEntity())
    }

    suspend fun updateUser(user: User) {
        userDao.updateUser(user.toEntity())
    }

    suspend fun deleteUser(userId: Long) {
        userDao.deleteUser(userId)
    }

    suspend fun login(userId: Long) {
        preferencesManager.setLoggedIn(true)
        preferencesManager.setCurrentUserId(userId)
    }

    suspend fun logout() {
        preferencesManager.clearSession()
    }

    fun isLoggedIn(): Flow<Boolean> = preferencesManager.isLoggedIn

    fun isGuest(): Flow<Boolean> = preferencesManager.isGuest

    fun getCurrentUserId(): Flow<Long?> = preferencesManager.currentUserId

    suspend fun enterGuestMode() {
        val guestUser = User(
            phone = "guest",
            passwordHash = "",
            nickname = "游客",
            avatarUrl = null,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        val userId = insertUser(guestUser)
        preferencesManager.setGuestMode(true)
        preferencesManager.setCurrentUserId(userId)
    }

    suspend fun exitGuestMode() {
        preferencesManager.clearSession()
    }
}
