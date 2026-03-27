@file:OptIn(ExperimentalMviKotlinApi::class)

package com.sedsoftware.blinktracker.components.preferences.store

import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.core.utils.ExperimentalMviKotlinApi
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutorScope
import com.arkivanov.mvikotlin.extensions.coroutines.coroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.coroutineExecutorFactory
import com.sedsoftware.blinktracker.components.preferences.integration.OverlayPermissionChecker
import com.sedsoftware.blinktracker.components.preferences.store.BlinkPreferencesStore.Intent
import com.sedsoftware.blinktracker.components.preferences.store.BlinkPreferencesStore.Label
import com.sedsoftware.blinktracker.components.preferences.store.BlinkPreferencesStore.State
import com.sedsoftware.blinktracker.settings.Settings
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch

internal class BlinkPreferencesStoreProvider(
    private val storeFactory: StoreFactory,
    private val permissionChecker: OverlayPermissionChecker,
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
                dispatch(Action.ObserveMinimizedOpacityOption)
                dispatch(Action.ObserveAutoStartOption)
                dispatch(Action.GetInitialPermissionValue)
            },
            executorFactory = coroutineExecutorFactory {
                onAction<Action.GetInitialPermissionValue> {
                    launch(getExceptionHandler(this)) {
                        val granted = permissionChecker.isPermissionGranted()
                        dispatch(Msg.OverlayPermissionChanged(granted))
                    }
                }

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

                onAction<Action.ObserveMinimizedOpacityOption> {
                    launch(getExceptionHandler(this)) {
                        settings.getMinimizedOpacity()
                            .collect { dispatch(Msg.MinimizedOpacityChanged(it)) }
                    }
                }

                onAction<Action.ObserveAutoStartOption> {
                    launch(getExceptionHandler(this)) {
                        settings.getAutoStartEnabled()
                            .collect { dispatch(Msg.AutoStartOptionChanged(it)) }
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
                    }
                }

                onIntent<Intent.OnLaunchMinimizedChange> {
                    launch(getExceptionHandler(this)) {
                        settings.setLaunchMinimizedEnabled(it.value)
                    }
                }

                onIntent<Intent.OnMinimizedOpacityChange> {
                    launch(getExceptionHandler(this)) {
                        val newValue = it.value / OPACITY_PERCENTS_DIVIDER
                        settings.setMinimizedOpacity(newValue)
                    }
                }

                onIntent<Intent.OnAutoStartChanged> {
                    launch(getExceptionHandler(this)) {
                        settings.setAutoStartEnabled(it.value)
                    }
                }

                onIntent<Intent.CheckOverlayPermissionOnResume> {
                    launch(getExceptionHandler(this)) {
                        val granted = permissionChecker.isPermissionGranted()

                        when {
                            state().overlaySettingsDisplayed && granted -> {
                                dispatch(Msg.OverlaySettingsDisplayed(false))
                                dispatch(Msg.OverlayRationaleDisplayed(false))
                                dispatch(Msg.OverlayPermissionChanged(true))
                            }

                            state().overlaySettingsDisplayed && !granted -> {
                                dispatch(Msg.OverlaySettingsDisplayed(false))
                                dispatch(Msg.OverlayRationaleDisplayed(false))
                                dispatch(Msg.OverlayPermissionChanged(false))
                                settings.setAutoStartEnabled(false)
                            }
                        }
                    }
                }

                onIntent<Intent.AgreeToDisplayOverlaySettings> {
                    dispatch(Msg.OverlayRationaleDisplayed(false))
                    dispatch(Msg.OverlaySettingsDisplayed(true))
                    permissionChecker.requestPermission()
                }

                onIntent<Intent.DisagreeToDisplayOverlaySettings> {
                    launch(getExceptionHandler(this)) {
                        dispatch(Msg.OverlayRationaleDisplayed(false))
                        dispatch(Msg.OverlaySettingsDisplayed(false))
                        settings.setAutoStartEnabled(false)
                    }
                }
            },
            reducer = { msg ->
                when (msg) {
                    is Msg.ThresholdOptionChanged -> copy(
                        minimalMinuteThreshold = msg.newValue,
                    )

                    is Msg.SoundOptionChanged -> copy(
                        notifySound = msg.newValue,
                    )

                    is Msg.VibrationOptionChanged -> copy(
                        notifyVibration = msg.newValue,
                    )

                    is Msg.LaunchOptionChanged -> copy(
                        launchMinimized = msg.newValue,
                    )

                    is Msg.MinimizedOpacityChanged -> copy(
                        minimizedOpacity = msg.newValue,
                    )

                    is Msg.AutoStartOptionChanged -> copy(
                        autoStartOnUnlock = msg.newValue,
                        overlayRationaleDisplayed = msg.newValue && !overlayPermissionGranted,
                    )

                    is Msg.OverlaySettingsDisplayed -> copy(
                        overlaySettingsDisplayed = msg.newValue,
                    )

                    is Msg.OverlayRationaleDisplayed -> copy(
                        overlayRationaleDisplayed = msg.newValue,
                    )

                    is Msg.OverlayPermissionChanged -> copy(
                        overlayPermissionGranted = msg.newValue,
                    )
                }
            }
        ) {}

    private interface Action {
        data object ObserveThresholdOption : Action
        data object ObserveNotifySoundOption : Action
        data object ObserveNotifyVibrationOption : Action
        data object ObserveLaunchOption : Action
        data object ObserveMinimizedOpacityOption : Action
        data object ObserveAutoStartOption : Action
        data object GetInitialPermissionValue : Action
    }

    private sealed interface Msg {
        data class ThresholdOptionChanged(val newValue: Float) : Msg
        data class SoundOptionChanged(val newValue: Boolean) : Msg
        data class VibrationOptionChanged(val newValue: Boolean) : Msg
        data class LaunchOptionChanged(val newValue: Boolean) : Msg
        data class MinimizedOpacityChanged(val newValue: Float) : Msg
        data class AutoStartOptionChanged(val newValue: Boolean) : Msg
        data class OverlaySettingsDisplayed(val newValue: Boolean) : Msg
        data class OverlayRationaleDisplayed(val newValue: Boolean) : Msg
        data class OverlayPermissionChanged(val newValue: Boolean) : Msg
    }

    private fun getExceptionHandler(scope: CoroutineExecutorScope<State, Msg, Action, Label>): CoroutineExceptionHandler =
        CoroutineExceptionHandler { _, throwable ->
            scope.publish(Label.ErrorCaught(throwable))
        }

    internal companion object {
        const val OPACITY_PERCENTS_DIVIDER = 100f
    }
}
