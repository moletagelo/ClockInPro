package com.clockinpro.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clockinpro.data.repository.CheckRecordRepository
import com.clockinpro.data.repository.UserRepository
import com.clockinpro.domain.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileUiState(
    val user: User? = null,
    val consecutiveDays: Int = 0,
    val totalCheckIns: Int = 0,
    val isLoading: Boolean = true,
    val error: String? = null
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val checkRecordRepository: CheckRecordRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadProfile()
    }

    private fun loadProfile() {
        viewModelScope.launch {
            userRepository.observeCurrentUser().collect { user ->
                if (user != null) {
                    val consecutiveDays = checkRecordRepository.getConsecutiveDays(user.id)
                    val totalCheckIns = checkRecordRepository.getTotalCheckInCount(user.id)

                    _uiState.value = _uiState.value.copy(
                        user = user,
                        consecutiveDays = consecutiveDays,
                        totalCheckIns = totalCheckIns,
                        isLoading = false
                    )
                } else {
                    _uiState.value = _uiState.value.copy(isLoading = false)
                }
            }
        }
    }

    fun logout(onLogout: () -> Unit) {
        viewModelScope.launch {
            userRepository.logout()
            onLogout()
        }
    }
}

data class EditProfileUiState(
    val user: User? = null,
    val nickname: String = "",
    val avatarUrl: String = "",
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val error: String? = null,
    val saveSuccess: Boolean = false
)

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditProfileUiState())
    val uiState: StateFlow<EditProfileUiState> = _uiState.asStateFlow()

    init {
        loadProfile()
    }

    private fun loadProfile() {
        viewModelScope.launch {
            userRepository.observeCurrentUser().collect { user ->
                if (user != null) {
                    _uiState.value = _uiState.value.copy(
                        user = user,
                        nickname = user.nickname,
                        avatarUrl = user.avatarUrl,
                        isLoading = false
                    )
                }
            }
        }
    }

    fun updateNickname(nickname: String) {
        _uiState.value = _uiState.value.copy(nickname = nickname)
    }

    fun updateAvatarUrl(url: String) {
        _uiState.value = _uiState.value.copy(avatarUrl = url)
    }

    fun saveProfile() {
        val state = _uiState.value
        val user = state.user ?: return

        if (state.nickname.isBlank()) {
            _uiState.value = state.copy(error = "昵称不能为空")
            return
        }

        viewModelScope.launch {
            _uiState.value = state.copy(isSaving = true)

            try {
                val updatedUser = user.copy(
                    nickname = state.nickname,
                    avatarUrl = state.avatarUrl,
                    updatedAt = System.currentTimeMillis()
                )
                userRepository.updateUser(updatedUser)

                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    saveSuccess = true
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    error = e.message ?: "保存失败"
                )
            }
        }
    }
}
