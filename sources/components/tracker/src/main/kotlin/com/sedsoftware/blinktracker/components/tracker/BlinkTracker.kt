package com.sedsoftware.blinktracker.components.tracker

import com.arkivanov.decompose.value.Value

interface BlinkTracker {

    val models: Value<Model>

    fun onTrackingStarted()
    fun onTrackingStopped()
    fun onEyesProbabilityChanged(left: Float, right: Float)
    fun onFaceDetectionChanged(detected: Boolean)

    data class Model(
        val isTrackingActive: Boolean,
        val totalTrackedSeconds: Int,
        val blinksPerLastMinute: Int,
        val blinksTotal: Int,
        val isMinimized: Boolean,
        val hasFaceDetected: Boolean,
    )
}
