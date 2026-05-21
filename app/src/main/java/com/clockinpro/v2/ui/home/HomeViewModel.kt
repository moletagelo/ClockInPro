package com.clockinpro.v2.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clockinpro.v2.data.repository.TargetRepository
import com.clockinpro.v2.domain.model.SaveTargetRequest
import com.clockinpro.v2.domain.model.TargetSummary
import com.clockinpro.v2.reminder.TargetReminderScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class HomeUiState(
    val today: LocalDate = LocalDate.now(),
    val targets: List<TargetSummary> = emptyList()
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: TargetRepository,
    private val reminderScheduler: TargetReminderScheduler
) : ViewModel() {
    val uiState: StateFlow<HomeUiState> = repository.observeTargetSummaries()
        .map { targets ->
            HomeUiState(
                today = LocalDate.now(),
                targets = targets
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = HomeUiState(today = LocalDate.now())
        )

    suspend fun saveTarget(request: SaveTargetRequest) {
        val targetId = repository.saveTarget(request)
        repository.getTarget(targetId)?.let(reminderScheduler::schedule)
    }

    suspend fun deleteTarget(targetId: Long) {
        repository.deleteTarget(targetId)
        reminderScheduler.cancel(targetId)
    }

    suspend fun completeTarget(targetId: Long): Boolean = repository.markComplete(targetId)

    suspend fun undoCompleteTarget(targetId: Long) {
        repository.unmarkComplete(targetId)
    }
}
