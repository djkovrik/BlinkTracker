package com.sedsoftware.blinktracker.components.tracker.store

import com.arkivanov.mvikotlin.core.store.Store
import com.sedsoftware.blinktracker.components.tracker.store.BlinkTrackerStore.Intent
import com.sedsoftware.blinktracker.components.tracker.store.BlinkTrackerStore.Label
import com.sedsoftware.blinktracker.components.tracker.store.BlinkTrackerStore.State

internal interface BlinkTrackerStore : Store<Intent, State, Label> {

    sealed class Intent {
        object TrackingStarted : Intent()
        object TrackingStopped : Intent()
        data class FaceDetectedStateChanged(val detected: Boolean) : Intent()
        data class EyesProbabilityChanged(val left: Float, val right: Float) : Intent()
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
    )

    sealed class Label {
        object SoundNotificationTriggered : Label()
        object VibrationNotificationTriggered : Label()
        data class ErrorCaught(val throwable: Throwable) : Label()
    }
}
