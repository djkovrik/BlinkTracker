package com.sedsoftware.blinktracker.components.preferences.store

import com.arkivanov.mvikotlin.core.store.Store
import com.sedsoftware.blinktracker.components.preferences.model.PermissionStateNotification
import com.sedsoftware.blinktracker.components.preferences.store.BlinkPreferencesStore.Intent
import com.sedsoftware.blinktracker.components.preferences.store.BlinkPreferencesStore.Label
import com.sedsoftware.blinktracker.components.preferences.store.BlinkPreferencesStore.State

internal interface BlinkPreferencesStore : Store<Intent, State, Label> {

    sealed class Intent {
        data class OnMinimalThresholdChange(val value: Float) : Intent()
        data class OnNotifySoundChange(val value: Boolean) : Intent()
        data class OnNotifyVibrationChange(val value: Boolean) : Intent()
        data class OnLaunchMinimizedChange(val value: Boolean) : Intent()
        data class OnReplacePipChange(val value: Boolean) : Intent()
        object OnPermissionGranted: Intent()
        object OnPermissionDenied: Intent()
    }

    data class State(
        val minimalMinuteThreshold: Float = -1f,
        val notifySound: Boolean = false,
        val notifyVibration: Boolean = false,
        val launchMinimized: Boolean = false,
        val replacePip: Boolean = false,
        val permissionState: PermissionStateNotification = PermissionStateNotification.NOT_ASKED,
        val showRationale: Boolean = false,
    )

    sealed class Label {
        data class ErrorCaught(val throwable: Throwable) : Label()
    }
}
