package com.clockinpro.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clockinpro.data.repository.CheckRecordRepository
import com.clockinpro.data.repository.UserRepository
import com.clockinpro.domain.model.CheckRecord
import com.clockinpro.domain.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val user: User? = null,
    val todayRecords: List<CheckRecord> = emptyList(),
    val consecutiveDays: Int = 0,
    val totalCheckIns: Int = 0,
    val isLoading: Boolean = true,
    val error: String? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val checkRecordRepository: CheckRecordRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            userRepository.getCurrentUserId().collect { userId ->
                if (userId != null) {
                    loadUserData(userId)
                } else {
                    _uiState.value = _uiState.value.copy(isLoading = false)
                }
            }
        }
    }

    private suspend fun loadUserData(userId: Long) {
        try {
            val user = userRepository.getUserById(userId)
            val consecutiveDays = checkRecordRepository.getConsecutiveDays(userId)
            val totalCheckIns = checkRecordRepository.getTotalCheckInCount(userId)

            _uiState.value = _uiState.value.copy(
                user = user,
                consecutiveDays = consecutiveDays,
                totalCheckIns = totalCheckIns,
                isLoading = false
            )

            checkRecordRepository.getTodayRecords(userId).collect { records ->
                _uiState.value = _uiState.value.copy(todayRecords = records)
            }
        } catch (e: Exception) {
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                error = e.message
            )
        }
    }

    fun refresh() {
        _uiState.value = _uiState.value.copy(isLoading = true)
        loadData()
    }
}
