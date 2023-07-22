package com.sedsoftware.blinktracker.components.preferences.integration

import com.sedsoftware.blinktracker.components.preferences.BlinkPreferences.Model
import com.sedsoftware.blinktracker.components.preferences.store.BlinkPreferencesStore.State
import com.sedsoftware.blinktracker.components.preferences.store.BlinkPreferencesStoreProvider.Companion.OPACITY_PERCENTS_DIVIDER

internal val stateToModel: (State) -> Model =
    {
        Model(
            selectedThreshold = it.minimalMinuteThreshold,
            notifySoundChecked = it.notifySound,
            notifyVibrationChecked = it.notifyVibration,
            launchMinimized = it.launchMinimized,
            minimizedOpacityPercent = it.minimizedOpacity * OPACITY_PERCENTS_DIVIDER,
        )
    }
