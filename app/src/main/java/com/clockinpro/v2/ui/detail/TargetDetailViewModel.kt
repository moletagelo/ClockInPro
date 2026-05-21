package com.clockinpro.v2.ui.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clockinpro.v2.data.repository.TargetRepository
import com.clockinpro.v2.domain.model.SaveTargetRequest
import com.clockinpro.v2.domain.model.TargetDetail
import com.clockinpro.v2.reminder.TargetReminderScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
import java.time.YearMonth
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class CalendarDayUiState(
    val date: LocalDate?,
    val label: String,
    val isCompleted: Boolean,
    val isToday: Boolean
)

data class TargetDetailUiState(
    val detail: TargetDetail? = null,
    val month: YearMonth = YearMonth.now(),
    val calendarDays: List<CalendarDayUiState> = emptyList()
)

@HiltViewModel
class TargetDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: TargetRepository,
    private val reminderScheduler: TargetReminderScheduler
) : ViewModel() {
    private val targetId: Long = checkNotNull(savedStateHandle.get<Long>("targetId"))
    private val selectedMonth = MutableStateFlow(YearMonth.now())

    val uiState: StateFlow<TargetDetailUiState> = combine(
        repository.observeTargetDetail(targetId),
        selectedMonth
    ) { detail, month ->
        TargetDetailUiState(
            detail = detail,
            month = month,
            calendarDays = detail?.let { buildCalendarDays(month, it) }.orEmpty()
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = TargetDetailUiState(month = YearMonth.now())
    )

    fun showPreviousMonth() {
        selectedMonth.value = selectedMonth.value.minusMonths(1)
    }

    fun showNextMonth() {
        selectedMonth.value = selectedMonth.value.plusMonths(1)
    }

    suspend fun saveTarget(request: SaveTargetRequest) {
        val savedId = repository.saveTarget(request)
        repository.getTarget(savedId)?.let(reminderScheduler::schedule)
    }

    suspend fun deleteTarget() {
        repository.deleteTarget(targetId)
        reminderScheduler.cancel(targetId)
    }

    private fun buildCalendarDays(
        month: YearMonth,
        detail: TargetDetail
    ): List<CalendarDayUiState> {
        val completedDates = detail.completions.map { it.date }.toSet()
        val firstDay = month.atDay(1)
        val leadingBlanks = firstDay.dayOfWeek.value % 7
        val totalDays = month.lengthOfMonth()
        val today = LocalDate.now()
        val cells = mutableListOf<CalendarDayUiState>()

        repeat(leadingBlanks) {
            cells += CalendarDayUiState(null, "", false, false)
        }

        for (day in 1..totalDays) {
            val date = month.atDay(day)
            cells += CalendarDayUiState(
                date = date,
                label = day.toString(),
                isCompleted = completedDates.contains(date),
                isToday = today == date
            )
        }

        while (cells.size % 7 != 0) {
            cells += CalendarDayUiState(null, "", false, false)
        }

        return cells
    }
}
