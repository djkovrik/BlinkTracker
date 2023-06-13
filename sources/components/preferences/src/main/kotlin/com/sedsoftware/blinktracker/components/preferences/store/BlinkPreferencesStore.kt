package com.sedsoftware.blinktracker.components.preferences.store

import com.arkivanov.mvikotlin.core.store.Store
import com.sedsoftware.blinktracker.components.preferences.store.BlinkPreferencesStore.Intent
import com.sedsoftware.blinktracker.components.preferences.store.BlinkPreferencesStore.Label
import com.sedsoftware.blinktracker.components.preferences.store.BlinkPreferencesStore.State

internal interface BlinkPreferencesStore : Store<Intent, State, Label> {

    sealed class Intent {
        data class MinimalThresholdChanged(val value: Float) : Intent()
        data class NotifySoundChanged(val value: Boolean) : Intent()
        data class NotifyVibrationChanged(val value: Boolean) : Intent()
        data class LaunchMinimizedChanged(val value: Boolean) : Intent()
    }

    data class State(
        val minimalMinuteThreshold: Float = -1f,
        val notifySound: Boolean = false,
        val notifyVibration: Boolean = false,
        val launchMinimized: Boolean = false,
    )

    sealed class Label {
        data class ErrorCaught(val throwable: Throwable) : Label()
    }
}
