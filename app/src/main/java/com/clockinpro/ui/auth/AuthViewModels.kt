package com.clockinpro.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clockinpro.data.repository.UserRepository
import com.clockinpro.domain.model.User
import com.clockinpro.util.SecurityUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AuthUiState(
    val phone: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val verificationCode: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false,
    val countdown: Int = 0
)

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun updatePhone(phone: String) {
        _uiState.value = _uiState.value.copy(phone = phone, error = null)
    }

    fun updatePassword(password: String) {
        _uiState.value = _uiState.value.copy(password = password, error = null)
    }

    fun login(onSuccess: () -> Unit) {
        val state = _uiState.value

        if (state.phone.isBlank()) {
            _uiState.value = state.copy(error = "请输入手机号")
            return
        }

        if (state.password.isBlank()) {
            _uiState.value = state.copy(error = "请输入密码")
            return
        }

        viewModelScope.launch {
            _uiState.value = state.copy(isLoading = true, error = null)

            try {
                val user = userRepository.getUserByPhone(state.phone)
                if (user == null) {
                    _uiState.value = _uiState.value.copy(isLoading = false, error = "用户不存在")
                    return@launch
                }

                val hashedInput = SecurityUtil.hashPassword(state.password)
                if (user.passwordHash != hashedInput) {
                    _uiState.value = _uiState.value.copy(isLoading = false, error = "密码错误")
                    return@launch
                }

                userRepository.login(user.id)
                _uiState.value = _uiState.value.copy(isLoading = false, isSuccess = true)
                onSuccess()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "登录失败"
                )
            }
        }
    }
}

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun updatePhone(phone: String) {
        _uiState.value = _uiState.value.copy(phone = phone, error = null)
    }

    fun updatePassword(password: String) {
        _uiState.value = _uiState.value.copy(password = password, error = null)
    }

    fun updateConfirmPassword(confirmPassword: String) {
        _uiState.value = _uiState.value.copy(confirmPassword = confirmPassword, error = null)
    }

    fun updateVerificationCode(code: String) {
        _uiState.value = _uiState.value.copy(verificationCode = code, error = null)
    }

    fun register(onSuccess: () -> Unit) {
        val state = _uiState.value

        if (state.phone.isBlank() || state.phone.length != 11) {
            _uiState.value = state.copy(error = "请输入正确的手机号")
            return
        }

        if (state.password.isBlank() || state.password.length < 6) {
            _uiState.value = state.copy(error = "密码至少6位")
            return
        }

        if (state.password != state.confirmPassword) {
            _uiState.value = state.copy(error = "两次密码不一致")
            return
        }

        if (state.verificationCode.isBlank()) {
            _uiState.value = state.copy(error = "请输入验证码")
            return
        }

        viewModelScope.launch {
            _uiState.value = state.copy(isLoading = true, error = null)

            try {
                val existingUser = userRepository.getUserByPhone(state.phone)
                if (existingUser != null) {
                    _uiState.value = _uiState.value.copy(isLoading = false, error = "该手机号已注册")
                    return@launch
                }

                val hashedPassword = SecurityUtil.hashPassword(state.password)
                val user = User(
                    phone = state.phone,
                    passwordHash = hashedPassword,
                    nickname = "用户${state.phone.takeLast(4)}"
                )

                val userId = userRepository.insertUser(user)
                userRepository.login(userId)
                _uiState.value = _uiState.value.copy(isLoading = false, isSuccess = true)
                onSuccess()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "注册失败"
                )
            }
        }
    }
}

@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun updatePhone(phone: String) {
        _uiState.value = _uiState.value.copy(phone = phone, error = null)
    }

    fun updatePassword(password: String) {
        _uiState.value = _uiState.value.copy(password = password, error = null)
    }

    fun updateVerificationCode(code: String) {
        _uiState.value = _uiState.value.copy(verificationCode = code, error = null)
    }

    fun resetPassword(onSuccess: () -> Unit) {
        val state = _uiState.value

        if (state.phone.isBlank() || state.phone.length != 11) {
            _uiState.value = state.copy(error = "请输入正确的手机号")
            return
        }

        if (state.verificationCode.isBlank()) {
            _uiState.value = state.copy(error = "请输入验证码")
            return
        }

        if (state.password.isBlank() || state.password.length < 6) {
            _uiState.value = state.copy(error = "密码至少6位")
            return
        }

        viewModelScope.launch {
            _uiState.value = state.copy(isLoading = true, error = null)

            try {
                val user = userRepository.getUserByPhone(state.phone)
                if (user == null) {
                    _uiState.value = _uiState.value.copy(isLoading = false, error = "用户不存在")
                    return@launch
                }

                val hashedPassword = SecurityUtil.hashPassword(state.password)
                val updatedUser = user.copy(
                    passwordHash = hashedPassword,
                    updatedAt = System.currentTimeMillis()
                )
                userRepository.updateUser(updatedUser)

                _uiState.value = _uiState.value.copy(isLoading = false, isSuccess = true)
                onSuccess()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "重置失败"
                )
            }
        }
    }
}
