package com.sedsoftware.blinktracker.components.tracker

import com.sedsoftware.blinktracker.components.tracker.model.VisionFaceData
import kotlinx.coroutines.flow.Flow

interface BlinkTracker {

    val models: Flow<Model>
    val initial: Model

    fun onTrackingStarted()
    fun onTrackingStopped()
    fun onFaceDataChanged(data: VisionFaceData)
    fun onMinimizedStateChanged(minimized: Boolean)
    fun showPreferencesPanel()
    fun closePreferencesPanel()

    data class Model(
        val isTrackingActive: Boolean,
        val timerLabel: String,
        val blinksPerLastMinute: Int,
        val blinksTotal: Int,
        val isMinimized: Boolean,
        val hasFaceDetected: Boolean,
        val isPreferencesPanelVisible: Boolean,
    )

    sealed class Output {
        object SoundNotificationTriggered : Output()
        object VibroNotificationTriggered : Output()
        data class ErrorCaught(val throwable: Throwable) : Output()
    }
}
