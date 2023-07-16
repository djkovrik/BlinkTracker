package com.sedsoftware.blinktracker.components.tracker

import com.sedsoftware.blinktracker.components.tracker.model.NotificationInfoData
import com.sedsoftware.blinktracker.components.tracker.model.VisionFaceData
import kotlinx.coroutines.flow.Flow

interface BlinkTracker {

    val models: Flow<Model>
    val initial: Model

    fun onTrackingStarted()
    fun onTrackingStopped()
    fun onFaceDataChanged(data: VisionFaceData)
    fun onMinimizeRequested()
    fun onPictureInPictureChanged(enabled: Boolean)

    data class Model(
        val isTrackingActive: Boolean,
        val timerLabel: String,
        val blinksPerLastMinute: Int,
        val blinksTotal: Int,
        val isMinimized: Boolean,
        val hasFaceDetected: Boolean
    )

    sealed class Output {
        object SoundNotificationTriggered : Output()
        object VibroNotificationTriggered : Output()
        data class ErrorCaught(val throwable: Throwable) : Output()
        data class BlinkedPerMinute(val value: Int) : Output()
        data class NotificationDataChanged(val data: NotificationInfoData) : Output()
        object TrackingStopped : Output()
    }
}
