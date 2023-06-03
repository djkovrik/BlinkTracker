package com.sedsoftware.blinktracker.components.preferences

import kotlinx.coroutines.flow.Flow

interface BlinkPreferences {

    val models: Flow<Model>

    fun onMinimalThresholdChanged(value: Int)
    fun onNotifySoundChanged(value: Boolean)
    fun onNotifyVibrationChanged(value: Boolean)

    data class Model(
        val settingsPanelVisible: Boolean,
        val selectedThreshold: Int,
        val notifySoundChecked: Boolean,
        val notifyVibrationChecked: Boolean,
    )

    sealed class Output {
        data class ErrorCaught(val throwable: Throwable) : Output()
    }
}
