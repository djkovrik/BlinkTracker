@file:OptIn(ExperimentalMviKotlinApi::class)

package com.sedsoftware.blinktracker.components.preferences.store

import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.core.utils.ExperimentalMviKotlinApi
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutorScope
import com.arkivanov.mvikotlin.extensions.coroutines.coroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.coroutineExecutorFactory
import com.sedsoftware.blinktracker.components.preferences.model.PermissionStateNotification
import com.sedsoftware.blinktracker.components.preferences.store.BlinkPreferencesStore.Intent
import com.sedsoftware.blinktracker.components.preferences.store.BlinkPreferencesStore.Label
import com.sedsoftware.blinktracker.components.preferences.store.BlinkPreferencesStore.State
import com.sedsoftware.blinktracker.settings.Settings
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch

internal class BlinkPreferencesStoreProvider(
    private val storeFactory: StoreFactory,
    private val settings: Settings,
) {

    fun provide(): BlinkPreferencesStore =
        object : BlinkPreferencesStore, Store<Intent, State, Label> by storeFactory.create<Intent, Action, Msg, State, Label>(
            name = "BlinkPreferencesStore",
            initialState = State(),
            bootstrapper = coroutineBootstrapper {
                dispatch(Action.ObserveThresholdOption)
                dispatch(Action.ObserveNotifySoundOption)
                dispatch(Action.ObserveNotifyVibrationOption)
                dispatch(Action.ObserveLaunchOption)
                dispatch(Action.ObserveReplacePipOption)
            },
            executorFactory = coroutineExecutorFactory {
                onAction<Action.ObserveThresholdOption> {
                    launch(getExceptionHandler(this)) {
                        settings.getPerMinuteThreshold()
                            .collect { dispatch(Msg.ThresholdOptionChanged(it)) }
                    }
                }

                onAction<Action.ObserveNotifySoundOption> {
                    launch(getExceptionHandler(this)) {
                        settings.getNotifySoundEnabled()
                            .collect { dispatch(Msg.SoundOptionChanged(it)) }
                    }
                }

                onAction<Action.ObserveNotifyVibrationOption> {
                    launch(getExceptionHandler(this)) {
                        settings.getNotifyVibrationEnabled()
                            .collect { dispatch(Msg.VibrationOptionChanged(it)) }
                    }
                }

                onAction<Action.ObserveLaunchOption> {
                    launch(getExceptionHandler(this)) {
                        settings.getLaunchMinimizedEnabled()
                            .collect { dispatch(Msg.LaunchOptionChanged(it)) }
                    }
                }

                onAction<Action.ObserveReplacePipOption> {
                    launch(getExceptionHandler(this)) {
                        settings.getReplacePipEnabled()
                            .collect { dispatch(Msg.ReplacePipOptionChanged(it)) }
                    }
                }

                onIntent<Intent.OnMinimalThresholdChange> {
                    launch(getExceptionHandler(this)) {
                        settings.setPerMinuteThreshold(it.value)
                    }
                }

                onIntent<Intent.OnNotifySoundChange> {
                    launch(getExceptionHandler(this)) {
                        settings.setNotifySoundEnabled(it.value)
                    }
                }

                onIntent<Intent.OnNotifyVibrationChange> {
                    launch(getExceptionHandler(this)) {
                        settings.setNotifyVibrationEnabled(it.value)

                        if (state.replacePip) {
                            settings.setReplacePipEnabled(false)
                        }
                    }
                }

                onIntent<Intent.OnLaunchMinimizedChange> {
                    launch(getExceptionHandler(this)) {
                        settings.setLaunchMinimizedEnabled(it.value)
                    }
                }

                onIntent<Intent.OnReplacePipChange> {
                    launch(getExceptionHandler(this)) {
                        settings.setReplacePipEnabled(it.value)
                        if (state.notifyVibration) {
                            settings.setNotifySoundEnabled(true)
                            settings.setNotifyVibrationEnabled(false)
                        }
                    }
                    dispatch(Msg.RationaleStateChanged(false))
                }

                onIntent<Intent.OnPermissionGranted> {
                    dispatch(Msg.PermissionStateChanged(true))
                    dispatch(Msg.RationaleStateChanged(false))
                }

                onIntent<Intent.OnPermissionDenied> {
                    dispatch(Msg.PermissionStateChanged(false))
                    dispatch(Msg.RationaleStateChanged(true))
                    launch(getExceptionHandler(this)) {
                        settings.setReplacePipEnabled(false)
                    }
                }
            },
            reducer = { msg ->
                when (msg) {
                    is Msg.ThresholdOptionChanged -> copy(
                        minimalMinuteThreshold = msg.newValue
                    )

                    is Msg.SoundOptionChanged -> copy(
                        notifySound = msg.newValue
                    )

                    is Msg.VibrationOptionChanged -> copy(
                        notifyVibration = msg.newValue
                    )

                    is Msg.LaunchOptionChanged -> copy(
                        launchMinimized = msg.newValue
                    )

                    is Msg.ReplacePipOptionChanged -> copy(
                        replacePip = msg.newValue
                    )

                    is Msg.PermissionStateChanged -> copy(
                        permissionState = if (msg.granted) {
                            PermissionStateNotification.GRANTED
                        } else {
                            PermissionStateNotification.DENIED
                        },
                    )

                    is Msg.RationaleStateChanged -> copy(
                        showRationale = msg.displayed,
                    )
                }
            }
        ) {}

    private interface Action {
        object ObserveThresholdOption : Action
        object ObserveNotifySoundOption : Action
        object ObserveNotifyVibrationOption : Action
        object ObserveLaunchOption : Action
        object ObserveReplacePipOption : Action
    }

    private sealed interface Msg {
        data class ThresholdOptionChanged(val newValue: Float) : Msg
        data class SoundOptionChanged(val newValue: Boolean) : Msg
        data class VibrationOptionChanged(val newValue: Boolean) : Msg
        data class LaunchOptionChanged(val newValue: Boolean) : Msg
        data class ReplacePipOptionChanged(val newValue: Boolean) : Msg
        data class PermissionStateChanged(val granted: Boolean) : Msg
        data class RationaleStateChanged(val displayed: Boolean) : Msg
    }

    private fun getExceptionHandler(scope: CoroutineExecutorScope<State, Msg, Label>): CoroutineExceptionHandler =
        CoroutineExceptionHandler { _, throwable ->
            scope.publish(Label.ErrorCaught(throwable))
        }
}
