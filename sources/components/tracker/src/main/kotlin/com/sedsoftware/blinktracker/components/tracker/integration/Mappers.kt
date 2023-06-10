package com.sedsoftware.blinktracker.components.tracker.integration

import com.sedsoftware.blinktracker.components.tracker.BlinkTracker.Model
import com.sedsoftware.blinktracker.components.tracker.store.BlinkTrackerStore.State

internal val stateToModel: (State) -> Model =
    {
        Model(
            isTrackingActive = it.active,
            timerLabel = buildStringFromTimer(it.timer),
            blinksPerLastMinute = it.blinkLastMinute,
            blinksTotal = it.blinksTotal,
            isMinimized = it.minimized,
            hasFaceDetected = it.faceDetected,
        )
    }

private fun buildStringFromTimer(timer: Int): String {
    val minutes = timer / 60
    val seconds = timer - minutes * 60

    return when {
        seconds < 10 && minutes == 10 -> "$minutes:0$seconds"
        seconds < 10 && minutes != 10 -> "0$minutes:0$seconds"
        else -> "0$minutes:$seconds"
    }
}
