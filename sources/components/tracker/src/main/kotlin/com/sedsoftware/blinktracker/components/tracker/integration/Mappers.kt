package com.sedsoftware.blinktracker.components.tracker.integration

import com.sedsoftware.blinktracker.components.tracker.BlinkTracker.Model
import com.sedsoftware.blinktracker.components.tracker.store.BlinkTrackerStore.State

internal val stateToModel: (State) -> Model =
    {
        Model(
            isTrackingActive = it.active,
            totalTrackedSeconds = it.timer,
            blinksPerLastMinute = it.blinkLastMinute,
            blinksTotal = it.blinksTotal,
            isMinimized = it.minimized,
            hasFaceDetected = it.faceDetected,
        )
    }
