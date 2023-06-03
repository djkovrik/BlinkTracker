package com.sedsoftware.blinktracker.components.preferences.integration

import com.sedsoftware.blinktracker.components.preferences.BlinkPreferences.Model
import com.sedsoftware.blinktracker.components.preferences.store.BlinkPreferencesStore.State

internal val stateToModel: (State) -> Model =
    {
        Model(
            settingsPanelVisible = it.settingsPanelVisible,
            selectedThreshold = it.minimalMinuteThreshold,
            notifySoundChecked = it.notifySound,
            notifyVibrationChecked = it.notifyVibration,
        )
    }
