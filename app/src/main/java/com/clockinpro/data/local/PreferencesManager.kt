package com.clockinpro.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "clockin_prefs")

@Singleton
class PreferencesManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val dataStore = context.dataStore

    val isLoggedIn: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[KEY_IS_LOGGED_IN] ?: false
    }

    val currentUserId: Flow<Long?> = dataStore.data.map { preferences ->
        preferences[KEY_USER_ID]
    }

    val isDarkMode: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[KEY_DARK_MODE] ?: false
    }

    val lastSyncTime: Flow<Long> = dataStore.data.map { preferences ->
        preferences[KEY_LAST_SYNC_TIME] ?: 0L
    }

    suspend fun setLoggedIn(isLoggedIn: Boolean) {
        dataStore.edit { preferences ->
            preferences[KEY_IS_LOGGED_IN] = isLoggedIn
        }
    }

    suspend fun setCurrentUserId(userId: Long?) {
        dataStore.edit { preferences ->
            if (userId != null) {
                preferences[KEY_USER_ID] = userId
            } else {
                preferences.remove(KEY_USER_ID)
            }
        }
    }

    suspend fun setDarkMode(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[KEY_DARK_MODE] = enabled
        }
    }

    suspend fun setLastSyncTime(time: Long) {
        dataStore.edit { preferences ->
            preferences[KEY_LAST_SYNC_TIME] = time
        }
    }

    suspend fun clearSession() {
        dataStore.edit { preferences ->
            preferences.remove(KEY_IS_LOGGED_IN)
            preferences.remove(KEY_USER_ID)
        }
    }

    companion object {
        private val KEY_IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        private val KEY_USER_ID = longPreferencesKey("user_id")
        private val KEY_DARK_MODE = booleanPreferencesKey("dark_mode")
        private val KEY_LAST_SYNC_TIME = longPreferencesKey("last_sync_time")
    }
}
