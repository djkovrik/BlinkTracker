package com.sedsoftware.blinktracker.components.preferences

import kotlinx.coroutines.flow.Flow

interface BlinkPreferences {

    val models: Flow<Model>
    val initial: Model

    fun onMinimalThresholdChanged(value: Float)
    fun onNotifySoundChanged(value: Boolean)
    fun onNotifyVibrationChanged(value: Boolean)
    fun onLaunchMinimizedChanged(value: Boolean)
    fun onMinimizedOpacityChanged(value: Float)

    data class Model(
        val selectedThreshold: Float,
        val notifySoundChecked: Boolean,
        val notifyVibrationChecked: Boolean,
        val launchMinimized: Boolean,
        val minimizedOpacityPercent: Float,
    )

    sealed class Output {
        data class ErrorCaught(val throwable: Throwable) : Output()
    }
}
