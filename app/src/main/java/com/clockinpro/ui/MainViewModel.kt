package com.clockinpro.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clockinpro.data.local.PreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class MainUiState(
    val isLoading: Boolean = true,
    val hasCompletedOnboarding: Boolean = false
)

@HiltViewModel
class MainViewModel @Inject constructor(
    preferencesManager: PreferencesManager
) : ViewModel() {
    val uiState: StateFlow<MainUiState> = preferencesManager.hasCompletedOnboarding
        .map { hasCompletedOnboarding ->
            MainUiState(
                isLoading = false,
                hasCompletedOnboarding = hasCompletedOnboarding
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = MainUiState()
        )
}
