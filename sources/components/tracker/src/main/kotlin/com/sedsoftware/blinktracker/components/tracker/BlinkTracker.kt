package com.sedsoftware.blinktracker.components.tracker

import kotlinx.coroutines.flow.Flow

interface BlinkTracker {

    val models: Flow<Model>

    fun onTrackingStarted()
    fun onTrackingStopped()
    fun onEyesProbabilityChanged(left: Float, right: Float)
    fun onFaceDetectionChanged(detected: Boolean)
    fun onPreferencesPanelRequested()
    fun onPreferencesPanelClosed()

    data class Model(
        val isTrackingActive: Boolean,
        val totalTrackedSeconds: Int,
        val blinksPerLastMinute: Int,
        val blinksTotal: Int,
        val isMinimized: Boolean,
        val hasFaceDetected: Boolean,
    )

    sealed class Output {
        object PreferencesPanelRequested : Output()
        object PreferencesPanelClosed : Output()
        object SoundNotificationTriggered : Output()
        object VibroNotificationTriggered : Output()
        data class ErrorCaught(val throwable: Throwable) : Output()
    }
}
