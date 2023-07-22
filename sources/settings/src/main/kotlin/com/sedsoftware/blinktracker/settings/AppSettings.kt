package com.sedsoftware.blinktracker.settings

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AppSettings(
    private val context: Context,
) : Settings {

    internal object Store {
        private var datastore: DataStore<Preferences>? = null

        fun get(context: Context): DataStore<Preferences> {
            if (datastore == null) {
                datastore = context.dataStore
            }

            return datastore!!
        }
    }

    private val perMinuteThresholdKey: Preferences.Key<Float> = floatPreferencesKey(PreferenceKey.THRESHOLD)
    private val notifySoundEnabledKey: Preferences.Key<Boolean> = booleanPreferencesKey(PreferenceKey.NOTIFY_SOUND)
    private val notifyVibrationEnabledKey: Preferences.Key<Boolean> = booleanPreferencesKey(PreferenceKey.NOTIFY_VIBRO)
    private val launchMinimizedEnabledKey: Preferences.Key<Boolean> = booleanPreferencesKey(PreferenceKey.LAUNCH_MINIMIZED)
    private val minimizedOpacityKey: Preferences.Key<Float> = floatPreferencesKey(PreferenceKey.OPACITY)

    override suspend fun getPerMinuteThreshold(): Flow<Float> =
        getPrefsValue(perMinuteThresholdKey, PER_MINUTE_THRESHOLD_DEFAULT)

    override suspend fun getNotifySoundEnabled(): Flow<Boolean> =
        getPrefsValue(notifySoundEnabledKey, NOTIFY_SOUND_DEFAULT)

    override suspend fun getNotifyVibrationEnabled(): Flow<Boolean> =
        getPrefsValue(notifyVibrationEnabledKey, NOTIFY_VIBRATION_DEFAULT)

    override suspend fun getLaunchMinimizedEnabled(): Flow<Boolean> =
        getPrefsValue(launchMinimizedEnabledKey, LAUNCH_MINIMIZED_DEFAULT)

    override suspend fun getMinimizedOpacity(): Flow<Float> =
        getPrefsValue(minimizedOpacityKey, MINIMIZED_OPACITY_DEFAULT)

    override suspend fun setPerMinuteThreshold(value: Float) =
        setPrefsValue(perMinuteThresholdKey, value)

    override suspend fun setNotifySoundEnabled(value: Boolean) =
        setPrefsValue(notifySoundEnabledKey, value)

    override suspend fun setNotifyVibrationEnabled(value: Boolean) =
        setPrefsValue(notifyVibrationEnabledKey, value)

    override suspend fun setLaunchMinimizedEnabled(value: Boolean) =
        setPrefsValue(launchMinimizedEnabledKey, value)

    override suspend fun setMinimizedOpacity(value: Float) =
        setPrefsValue(minimizedOpacityKey, value)

    private fun <T> getPrefsValue(key: Preferences.Key<T>, default: T): Flow<T> =
        Store.get(context).data.map { it[key] ?: default }

    private suspend fun <T> setPrefsValue(key: Preferences.Key<T>, value: T) {
        Store.get(context).edit { settings ->
            settings[key] = value
        }
    }

    private companion object {
        const val PER_MINUTE_THRESHOLD_DEFAULT = 12f
        const val NOTIFY_SOUND_DEFAULT = false
        const val NOTIFY_VIBRATION_DEFAULT = true
        const val LAUNCH_MINIMIZED_DEFAULT = false
        const val MINIMIZED_OPACITY_DEFAULT = 1f
    }
}
