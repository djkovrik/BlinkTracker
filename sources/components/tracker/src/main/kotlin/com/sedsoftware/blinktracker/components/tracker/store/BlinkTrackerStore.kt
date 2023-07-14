package com.sedsoftware.blinktracker.components.tracker.store

import com.arkivanov.mvikotlin.core.store.Store
import com.sedsoftware.blinktracker.components.tracker.model.VisionFaceData
import com.sedsoftware.blinktracker.components.tracker.store.BlinkTrackerStore.Intent
import com.sedsoftware.blinktracker.components.tracker.store.BlinkTrackerStore.Label
import com.sedsoftware.blinktracker.components.tracker.store.BlinkTrackerStore.State
import kotlinx.datetime.Instant

internal interface BlinkTrackerStore : Store<Intent, State, Label> {

    sealed class Intent {
        object OnTrackingStart : Intent()
        object OnTrackingStop : Intent()
        object OnLaunchPip : Intent()
        data class FaceDataChanged(val data: VisionFaceData) : Intent()
        data class MinimizedStateChanged(val minimized: Boolean) : Intent()
    }

    data class State(
        val active: Boolean = false,
        val timer: Int = 0,
        val blinkLastMinute: Int = 0,
        val blinksTotal: Int = 0,
        val minimized: Boolean = false,
        val faceDetected: Boolean = false,
        val threshold: Int = Int.MAX_VALUE,
        val notifyWithSound: Boolean = false,
        val notifyWithVibration: Boolean = false,
        val shouldLaunchMinimized: Boolean = false,
        val shouldReplacePip: Boolean = false,
        val lastBlink: Instant = Instant.DISTANT_PAST,
    )

    sealed class Label {
        object SoundNotificationTriggered : Label()
        object VibrationNotificationTriggered : Label()
        data class ErrorCaught(val throwable: Throwable) : Label()
        data class BlinksPerMinuteAvailable(val value: Int) : Label()
    }
}
