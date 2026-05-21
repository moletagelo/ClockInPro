package com.clockinpro.v2.ui.settings

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clockinpro.R
import com.clockinpro.data.local.PreferencesManager
import com.clockinpro.util.AppLanguage
import com.clockinpro.util.AppLocaleManager
import com.clockinpro.v2.data.backup.BackupCodec
import com.clockinpro.v2.data.repository.TargetRepository
import com.clockinpro.v2.reminder.TargetReminderScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class SettingsUiState(
    val isBusy: Boolean = false,
    val appLanguage: AppLanguage = AppLanguage.SYSTEM,
    val message: String? = null
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val preferencesManager: PreferencesManager,
    private val repository: TargetRepository,
    private val reminderScheduler: TargetReminderScheduler
) : ViewModel() {
    private val isBusy = MutableStateFlow(false)
    private val message = MutableStateFlow<String?>(null)
    val uiState: StateFlow<SettingsUiState> = combine(
        isBusy,
        preferencesManager.appLanguage,
        message
    ) { busy, appLanguage, snackbarMessage ->
        SettingsUiState(
            isBusy = busy,
            appLanguage = appLanguage,
            message = snackbarMessage
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = SettingsUiState()
    )

    fun exportBackup(uri: Uri) {
        viewModelScope.launch {
            isBusy.value = true
            runCatching {
                withContext(Dispatchers.IO) {
                    val json = BackupCodec.encode(repository.exportBackup())
                    context.contentResolver.openOutputStream(uri)?.use { output ->
                        output.write(json.toByteArray(Charsets.UTF_8))
                    } ?: error(context.getString(R.string.settings_backup_export_failed))
                }
            }.onSuccess {
                message.value = context.getString(R.string.settings_backup_exported)
            }.onFailure { throwable ->
                message.value = throwable.message ?: context.getString(R.string.settings_backup_export_failed)
            }.also {
                isBusy.value = false
            }
        }
    }

    fun importBackup(uri: Uri) {
        viewModelScope.launch {
            isBusy.value = true
            runCatching {
                withContext(Dispatchers.IO) {
                    val previousReminderTargets = repository.getReminderTargets()
                    val json = context.contentResolver.openInputStream(uri)?.use { input ->
                        input.readBytes().toString(Charsets.UTF_8)
                    } ?: error(context.getString(R.string.settings_backup_import_failed))
                    val payload = BackupCodec.decode(json)
                    repository.importBackup(payload)
                    previousReminderTargets.forEach { reminderTarget ->
                        reminderScheduler.cancel(reminderTarget.id)
                    }
                    reminderScheduler.reschedule(repository.getReminderTargets())
                }
            }.onSuccess {
                message.value = context.getString(R.string.settings_backup_imported)
            }.onFailure { throwable ->
                message.value = throwable.message ?: context.getString(R.string.settings_backup_import_failed)
            }.also {
                isBusy.value = false
            }
        }
    }

    fun setAppLanguage(language: AppLanguage) {
        viewModelScope.launch {
            preferencesManager.setAppLanguage(language)
            AppLocaleManager.apply(language)
        }
    }

    fun clearMessage() {
        message.value = null
    }
}
