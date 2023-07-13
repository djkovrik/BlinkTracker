package com.sedsoftware.blinktracker.components.preferences.integration

import com.sedsoftware.blinktracker.components.preferences.BlinkPreferences.Model
import com.sedsoftware.blinktracker.components.preferences.store.BlinkPreferencesStore.State

internal val stateToModel: (State) -> Model =
    {
        Model(
            selectedThreshold = it.minimalMinuteThreshold,
            notifySoundChecked = it.notifySound,
            notifyVibrationChecked = it.notifyVibration,
            launchMinimized = it.launchMinimized,
            replacePip = it.replacePip,
            permissionState = it.permissionState,
        )
    }
