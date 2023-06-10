package com.sedsoftware.blinktracker.components.preferences

import kotlinx.coroutines.flow.Flow

interface BlinkPreferences {

    val models: Flow<Model>
    val initial: Model

    fun onMinimalThresholdChanged(value: Float)
    fun onNotifySoundChanged(value: Boolean)
    fun onNotifyVibrationChanged(value: Boolean)
    fun onLaunchMinimizedChanged(value: Boolean)
    fun requestPreferencesPanel()
    fun closePreferencesPanel()

    data class Model(
        val settingsPanelVisible: Boolean,
        val selectedThreshold: Float,
        val notifySoundChecked: Boolean,
        val notifyVibrationChecked: Boolean,
        val launchMinimized: Boolean,
    )

    sealed class Output {
        data class ErrorCaught(val throwable: Throwable) : Output()
    }
}
