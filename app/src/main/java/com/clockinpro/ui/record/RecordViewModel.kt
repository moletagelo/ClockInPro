package com.clockinpro.ui.record

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clockinpro.data.repository.CheckRecordRepository
import com.clockinpro.data.repository.UserRepository
import com.clockinpro.domain.model.CheckRecord
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

data class RecordUiState(
    val records: List<CheckRecord> = emptyList(),
    val selectedDate: Long = System.currentTimeMillis(),
    val checkedDates: Set<String> = emptySet(),
    val monthStats: MonthStats = MonthStats(),
    val isLoading: Boolean = true,
    val error: String? = null
)

data class MonthStats(
    val totalDays: Int = 0,
    val checkInDays: Int = 0,
    val totalCheckIns: Int = 0
)

@HiltViewModel
class RecordViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val checkRecordRepository: CheckRecordRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RecordUiState())
    val uiState: StateFlow<RecordUiState> = _uiState.asStateFlow()

    init {
        loadRecords()
    }

    private fun loadRecords() {
        viewModelScope.launch {
            userRepository.getCurrentUserId().collect { userId ->
                if (userId != null) {
                    loadUserRecords(userId)
                }
            }
        }
    }

    private suspend fun loadUserRecords(userId: Long) {
        try {
            val checkedDates = checkRecordRepository.getCheckedDates(userId)
            _uiState.value = _uiState.value.copy(checkedDates = checkedDates)

            val calendar = Calendar.getInstance()
            calendar.timeInMillis = _uiState.value.selectedDate
            calendar.set(Calendar.DAY_OF_MONTH, 1)
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            val startOfMonth = calendar.timeInMillis

            calendar.add(Calendar.MONTH, 1)
            val endOfMonth = calendar.timeInMillis

            checkRecordRepository.getRecordsByDateRange(userId, startOfMonth, endOfMonth).collect { records ->
                val monthStats = calculateMonthStats(records)
                _uiState.value = _uiState.value.copy(
                    records = records,
                    monthStats = monthStats,
                    isLoading = false
                )
            }
        } catch (e: Exception) {
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                error = e.message
            )
        }
    }

    fun selectDate(date: Long) {
        _uiState.value = _uiState.value.copy(selectedDate = date)
        loadRecordsForDate(date)
    }

    private fun loadRecordsForDate(date: Long) {
        viewModelScope.launch {
            userRepository.getCurrentUserId().first()?.let { userId ->
                val calendar = Calendar.getInstance()
                calendar.timeInMillis = date
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
                val startOfDay = calendar.timeInMillis

                calendar.add(Calendar.DAY_OF_MONTH, 1)
                val endOfDay = calendar.timeInMillis

                checkRecordRepository.getRecordsByDateRange(userId, startOfDay, endOfDay).collect { records ->
                    _uiState.value = _uiState.value.copy(records = records)
                }
            }
        }
    }

    fun changeMonth(delta: Int) {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = _uiState.value.selectedDate
        calendar.add(Calendar.MONTH, delta)
        _uiState.value = _uiState.value.copy(selectedDate = calendar.timeInMillis)
        loadRecords()
    }

    private fun calculateMonthStats(records: List<CheckRecord>): MonthStats {
        val checkInDays = records.map { it.timestamp }.map { timestamp ->
            val cal = Calendar.getInstance()
            cal.timeInMillis = timestamp
            "${cal.get(Calendar.YEAR)}-${cal.get(Calendar.MONTH)}-${cal.get(Calendar.DAY_OF_MONTH)}"
        }.toSet().size

        return MonthStats(
            totalDays = Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH),
            checkInDays = checkInDays,
            totalCheckIns = records.size
        )
    }
}
