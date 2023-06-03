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

            },
            executorFactory = coroutineExecutorFactory {
                onIntent<Intent.MinimalThresholdChanged> {
                    launch(getExceptionHandler(this)) {
                        settings.setPerMinuteThreshold(it.value)
                        dispatch(Msg.ThresholdOptionChanged(it.value))
                    }
                }

                onIntent<Intent.NotifySoundChanged> {
                    launch(getExceptionHandler(this)) {
                        settings.setNotifySoundEnabled(it.value)
                        dispatch(Msg.SoundOptionChanged(it.value))
                    }
                }

                onIntent<Intent.NotifyVibrationChanged> {
                    launch(getExceptionHandler(this)) {
                        settings.setNotifyVibrationEnabled(it.value)
                        dispatch(Msg.VibrationOptionChanged(it.value))
                    }
                }

                onIntent<Intent.SettingsPanelRequested> {
                    dispatch(Msg.SettingsVisibilityChanged(true))
                }

                onIntent<Intent.SettingsPanelClosed> {
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

    private interface Action

    private sealed interface Msg {
        data class ThresholdOptionChanged(val newValue: Int) : Msg
        data class SoundOptionChanged(val newValue: Boolean) : Msg
        data class VibrationOptionChanged(val newValue: Boolean) : Msg
        data class SettingsVisibilityChanged(val visible: Boolean) : Msg
    }

    private fun getExceptionHandler(scope: CoroutineExecutorScope<State, Msg, Label>): CoroutineExceptionHandler =
        CoroutineExceptionHandler { _, throwable ->
            scope.publish(Label.ErrorCaught(throwable))
        }
}
