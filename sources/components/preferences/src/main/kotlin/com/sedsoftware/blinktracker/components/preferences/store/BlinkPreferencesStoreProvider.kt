@file:OptIn(ExperimentalMviKotlinApi::class)

package com.sedsoftware.blinktracker.components.preferences.store

import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.core.utils.ExperimentalMviKotlinApi
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutorScope
import com.arkivanov.mvikotlin.extensions.coroutines.coroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.coroutineExecutorFactory
import com.sedsoftware.blinktracker.components.preferences.store.BlinkPreferencesStore.Intent
import com.sedsoftware.blinktracker.components.preferences.store.BlinkPreferencesStore.Label
import com.sedsoftware.blinktracker.components.preferences.store.BlinkPreferencesStore.Label.ErrorCaught
import com.sedsoftware.blinktracker.components.preferences.store.BlinkPreferencesStore.State
import com.sedsoftware.blinktracker.settings.Settings
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

internal class BlinkPreferencesStoreProvider(
    private val storeFactory: StoreFactory,
    private val settings: Settings,
) {

    fun create(): BlinkPreferencesStore =
        object : BlinkPreferencesStore, Store<Intent, State, Label> by storeFactory.create<Intent, Action, Msg, State, Label>(
            name = "BlinkPreferencesStore",
            initialState = State(),
            bootstrapper = coroutineBootstrapper {
                launch {
                    dispatch(Action.ObserveThresholdOption)
                    dispatch(Action.ObserveNotifySoundOption)
                    dispatch(Action.ObserveNotifyVibrationOption)
                }
            },
            executorFactory = coroutineExecutorFactory {
                onAction<Action.ObserveThresholdOption> {
                    launch(getExceptionHandler(this)) {
                        settings.getPerMinuteThreshold()
                            .onEach { Msg.ThresholdOptionChanged(it) }
                    }
                }
                onAction<Action.ObserveNotifySoundOption> {
                    launch(getExceptionHandler(this)) {
                        settings.getNotifySoundEnabled()
                            .onEach { Msg.SoundOptionChanged(it) }
                    }
                }
                onAction<Action.ObserveNotifyVibrationOption> {
                    launch(getExceptionHandler(this)) {
                        settings.getNotifyVibrationEnabled()
                            .onEach { Msg.VibrationOptionChanged(it) }
                    }
                }

                onIntent<Intent.MinimalThresholdChanged> {
                    launch(getExceptionHandler(this)) {
                        settings.setPerMinuteThreshold(it.value)
                    }
                }

                onIntent<Intent.NotifySoundChanged> {
                    launch(getExceptionHandler(this)) {
                        settings.setNotifySoundEnabled(it.value)
                    }
                }

                onIntent<Intent.NotifyVibrationChanged> {
                    launch(getExceptionHandler(this)) {
                        settings.setNotifyVibrationEnabled(it.value)
                    }
                }

                onIntent<Intent.SettingsRequested> {
                    dispatch(Msg.SettingsVisibilityChanged(true))
                }

                onIntent<Intent.SettingsClosed> {
                    dispatch(Msg.SettingsVisibilityChanged(false))
                }
            },
            reducer = { msg ->
                when (msg) {
                    is Msg.ThresholdOptionChanged -> {
                        copy(minimalMinuteThreshold = msg.newValue)
                    }

                    is Msg.SoundOptionChanged -> {
                        copy(notifySound = msg.newValue)
                    }

                    is Msg.VibrationOptionChanged -> {
                        copy(notifyVibration = msg.newValue)
                    }

                    is Msg.SettingsVisibilityChanged -> {
                        copy(settingsPanelVisible = msg.visible)
                    }
                }
            }
        ) {}

    private sealed interface Action {
        object ObserveThresholdOption : Action
        object ObserveNotifySoundOption : Action
        object ObserveNotifyVibrationOption : Action
    }

    private sealed interface Msg {
        data class ThresholdOptionChanged(val newValue: Int) : Msg
        data class SoundOptionChanged(val newValue: Boolean) : Msg
        data class VibrationOptionChanged(val newValue: Boolean) : Msg
        data class SettingsVisibilityChanged(val visible: Boolean) : Msg
    }

    private fun getExceptionHandler(scope: CoroutineExecutorScope<State, Msg, Label>): CoroutineExceptionHandler =
        CoroutineExceptionHandler { _, throwable ->
            scope.publish(ErrorCaught(throwable))
        }
}
