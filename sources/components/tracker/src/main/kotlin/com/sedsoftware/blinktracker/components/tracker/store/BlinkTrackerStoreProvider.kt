@file:OptIn(ExperimentalMviKotlinApi::class)

package com.sedsoftware.blinktracker.components.tracker.store

import android.util.Log
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.core.utils.ExperimentalMviKotlinApi
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutorScope
import com.arkivanov.mvikotlin.extensions.coroutines.coroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.coroutineExecutorFactory
import com.sedsoftware.blinktracker.components.tracker.model.VisionFaceData
import com.sedsoftware.blinktracker.components.tracker.store.BlinkTrackerStore.Intent
import com.sedsoftware.blinktracker.components.tracker.store.BlinkTrackerStore.Label
import com.sedsoftware.blinktracker.components.tracker.store.BlinkTrackerStore.State
import com.sedsoftware.blinktracker.settings.Settings
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

internal class BlinkTrackerStoreProvider(
    private val storeFactory: StoreFactory,
    private val settings: Settings,
) {

    fun provide(): BlinkTrackerStore =
        object : BlinkTrackerStore, Store<Intent, State, Label> by storeFactory.create<Intent, Action, Msg, State, Label>(
            name = "BlinkTrackerStore",
            initialState = State(),
            bootstrapper = coroutineBootstrapper {
                launch {
                    dispatch(Action.ObserveThresholdOption)
                    dispatch(Action.ObserveNotifySoundOption)
                    dispatch(Action.ObserveNotifyVibrationOption)

                    (0..Int.MAX_VALUE)
                        .asSequence()
                        .asFlow()
                        .onEach {
                            delay(TIMER_DELAY)
                            dispatch(Action.OnTick)
                        }
                }
            },
            executorFactory = coroutineExecutorFactory {
                onAction<Action.ObserveThresholdOption> {
                    launch(getExceptionHandler(this)) {
                        settings.getPerMinuteThreshold()
                            .onEach { Msg.ObservedThresholdOptionChanged(it) }
                    }
                }
                onAction<Action.ObserveNotifySoundOption> {
                    launch(getExceptionHandler(this)) {
                        settings.getNotifySoundEnabled()
                            .onEach { Msg.ObservedSoundOptionChanged(it) }
                    }
                }
                onAction<Action.ObserveNotifyVibrationOption> {
                    launch(getExceptionHandler(this)) {
                        settings.getNotifyVibrationEnabled()
                            .onEach { Msg.ObservedVibrationOptionChanged(it) }
                    }
                }

                onAction<Action.OnTick> {
                    if (state.active) {
                        val counter = state.timer
                        dispatch(Msg.Tick(counter + 1))

                        // TODO CHECK STATS
                    }
                }

                onIntent<Intent.TrackingStarted> {
                    dispatch(Msg.TrackerStateChangedStarted(true))
                }

                onIntent<Intent.TrackingStopped> {
                    dispatch(Msg.TrackerStateChangedStarted(false))
                }

                onIntent<Intent.FaceDataChanged> {
                    dispatch(Msg.FaceDataAvailable(it.data))

                    // TODO REGISTER BLINKS
                    Log.d("BLINKDEBUG", "${it.data.faceAvailable}: left ${it.data.leftEye} right ${it.data.rightEye}")
                    if (it.data.leftEye != null && it.data.leftEye < 0.8 && it.data.rightEye != null && it.data.rightEye < 0.8) {
                        Log.d("BLINKDEBUG", "BLINK!")
                    }
                }
            },
            reducer = { msg ->
                when (msg) {
                    is Msg.ObservedThresholdOptionChanged -> {
                        copy(threshold = msg.newValue)
                    }

                    is Msg.ObservedSoundOptionChanged -> {
                        copy(notifyWithSound = msg.newValue)
                    }

                    is Msg.ObservedVibrationOptionChanged -> {
                        copy(notifyWithVibration = msg.newValue)
                    }

                    is Msg.FaceDataAvailable -> {
                        copy(faceDetected = msg.data.faceAvailable)
                    }

                    is Msg.TrackerStateChangedStarted -> {
                        copy(active = msg.started)
                    }

                    is Msg.Tick -> {
                        copy(timer = msg.seconds)
                    }
                }
            }
        ) {}

    private sealed interface Action {
        object ObserveThresholdOption : Action
        object ObserveNotifySoundOption : Action
        object ObserveNotifyVibrationOption : Action
        object OnTick : Action
    }

    private sealed interface Msg {
        data class ObservedThresholdOptionChanged(val newValue: Int) : Msg
        data class ObservedSoundOptionChanged(val newValue: Boolean) : Msg
        data class ObservedVibrationOptionChanged(val newValue: Boolean) : Msg
        data class FaceDataAvailable(val data: VisionFaceData) : Msg
        data class TrackerStateChangedStarted(val started: Boolean) : Msg
        data class Tick(val seconds: Int) : Msg
    }

    private fun getExceptionHandler(scope: CoroutineExecutorScope<State, Msg, Label>): CoroutineExceptionHandler =
        CoroutineExceptionHandler { _, throwable ->
            scope.publish(Label.ErrorCaught(throwable))
        }

    private companion object {
        const val TIMER_DELAY = 1000L
    }
}
