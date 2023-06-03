package com.sedsoftware.blinktracker.settings

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AppSettings(
    private val context: Context,
) : Settings {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
    private val perMinuteThresholdKey: Preferences.Key<Int> = intPreferencesKey("per_minute_threshold")
    private val notifySoundEnabledKey: Preferences.Key<Boolean> = booleanPreferencesKey("notify_sound_enabled")
    private val notifyVibrationEnabledKey: Preferences.Key<Boolean> = booleanPreferencesKey("notify_vibration_enabled")

    override suspend fun getPerMinuteThreshold(): Flow<Int> =
        getPrefsValue(perMinuteThresholdKey, PER_MINUTE_THRESHOLD_DEFAULT)

    override suspend fun getNotifySoundEnabled(): Flow<Boolean> =
        getPrefsValue(notifySoundEnabledKey, NOTIFY_SOUND_DEFAULT)

    override suspend fun getNotifyVibrationEnabled(): Flow<Boolean> =
        getPrefsValue(notifyVibrationEnabledKey, NOTIFY_VIBRATION_DEFAULT)

    override suspend fun setPerMinuteThreshold(value: Int) =
        setPrefsValue(perMinuteThresholdKey, value)

    override suspend fun setNotifySoundEnabled(value: Boolean) =
        setPrefsValue(notifySoundEnabledKey, NOTIFY_SOUND_DEFAULT)

    override suspend fun setNotifyVibrationEnabled(value: Boolean) =
        setPrefsValue(notifyVibrationEnabledKey, NOTIFY_VIBRATION_DEFAULT)

    private fun <T> getPrefsValue(key: Preferences.Key<T>, default: T): Flow<T> =
        context.dataStore.data.map { it[key] ?: default }

    private suspend fun <T> setPrefsValue(key: Preferences.Key<T>, value: T) {
        context.dataStore.edit { settings ->
            settings[key] = value
        }
    }

    private companion object {
        const val PER_MINUTE_THRESHOLD_DEFAULT = 12
        const val NOTIFY_SOUND_DEFAULT = false
        const val NOTIFY_VIBRATION_DEFAULT = true
    }
}
