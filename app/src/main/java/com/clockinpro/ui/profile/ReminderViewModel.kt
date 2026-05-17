package com.clockinpro.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clockinpro.data.repository.ReminderRepository
import com.clockinpro.data.repository.UserRepository
import com.clockinpro.domain.model.Reminder
import com.clockinpro.domain.model.RepeatType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ReminderUiState(
    val reminders: List<Reminder> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)

@HiltViewModel
class ReminderViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val reminderRepository: ReminderRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReminderUiState())
    val uiState: StateFlow<ReminderUiState> = _uiState.asStateFlow()

    init {
        loadReminders()
    }

    private fun loadReminders() {
        viewModelScope.launch {
            userRepository.getCurrentUserId().collect { userId ->
                if (userId != null) {
                    reminderRepository.getAllReminders(userId).collect { reminders ->
                        _uiState.value = _uiState.value.copy(
                            reminders = reminders,
                            isLoading = false
                        )
                    }
                }
            }
        }
    }

    fun addReminder(hour: Int, minute: Int, repeatType: RepeatType) {
        viewModelScope.launch {
            userRepository.getCurrentUserId().first()?.let { userId ->
                val reminder = Reminder(
                    userId = userId,
                    timeHour = hour,
                    timeMinute = minute,
                    repeatType = repeatType,
                    isEnabled = true
                )
                reminderRepository.insertReminder(reminder)
            }
        }
    }

    fun toggleReminder(reminder: Reminder) {
        viewModelScope.launch {
            reminderRepository.setReminderEnabled(reminder.id, !reminder.isEnabled)
        }
    }

    fun deleteReminder(reminder: Reminder) {
        viewModelScope.launch {
            reminderRepository.deleteReminder(reminder)
        }
    }
}
