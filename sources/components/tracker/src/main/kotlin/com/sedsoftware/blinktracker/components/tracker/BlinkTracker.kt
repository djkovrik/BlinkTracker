package com.sedsoftware.blinktracker.components.tracker

import com.arkivanov.decompose.value.Value

interface BlinkTracker {

    val models: Value<Model>

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
        data class ErrorCaught(val throwable: Throwable) : Output()
    }
}
