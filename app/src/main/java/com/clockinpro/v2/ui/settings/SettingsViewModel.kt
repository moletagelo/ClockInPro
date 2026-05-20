package com.clockinpro.v2.ui.settings

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clockinpro.v2.data.backup.BackupCodec
import com.clockinpro.v2.data.repository.TargetRepository
import com.clockinpro.v2.reminder.TargetReminderScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class SettingsUiState(
    val isBusy: Boolean = false,
    val message: String? = null
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val repository: TargetRepository,
    private val reminderScheduler: TargetReminderScheduler
) : ViewModel() {
    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    fun exportBackup(uri: Uri) {
        viewModelScope.launch {
            _uiState.value = SettingsUiState(isBusy = true)
            runCatching {
                withContext(Dispatchers.IO) {
                    val json = BackupCodec.encode(repository.exportBackup())
                    context.contentResolver.openOutputStream(uri)?.use { output ->
                        output.write(json.toByteArray(Charsets.UTF_8))
                    } ?: error("Unable to open export destination")
                }
            }.onSuccess {
                _uiState.value = SettingsUiState(message = "Backup exported")
            }.onFailure { throwable ->
                _uiState.value = SettingsUiState(message = throwable.message ?: "Export failed")
            }
        }
    }

    fun importBackup(uri: Uri) {
        viewModelScope.launch {
            _uiState.value = SettingsUiState(isBusy = true)
            runCatching {
                withContext(Dispatchers.IO) {
                    val previousReminderTargets = repository.getReminderTargets()
                    val json = context.contentResolver.openInputStream(uri)?.use { input ->
                        input.readBytes().toString(Charsets.UTF_8)
                    } ?: error("Unable to open import file")
                    val payload = BackupCodec.decode(json)
                    repository.importBackup(payload)
                    previousReminderTargets.forEach { reminderTarget ->
                        reminderScheduler.cancel(reminderTarget.id)
                    }
                    reminderScheduler.reschedule(repository.getReminderTargets())
                }
            }.onSuccess {
                _uiState.value = SettingsUiState(message = "Backup imported")
            }.onFailure { throwable ->
                _uiState.value = SettingsUiState(message = throwable.message ?: "Import failed")
            }
        }
    }

    fun clearMessage() {
        _uiState.value = _uiState.value.copy(message = null)
    }
}
