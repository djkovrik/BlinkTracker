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
    fun onAutoStartChanged(value: Boolean)
    fun onResumedFromOverlay()
    fun onOverlaySettingsRequested()
    fun onOverlaySettingsCanceled()

    data class Model(
        val selectedThreshold: Float,
        val notifySoundChecked: Boolean,
        val notifyVibrationChecked: Boolean,
        val launchMinimized: Boolean,
        val minimizedOpacityPercent: Float,
        val autoStartChecked: Boolean,
        val rationaleDisplayed: Boolean,
    )

    sealed class Output {
        data class ErrorCaught(val throwable: Throwable) : Output()
    }
}
