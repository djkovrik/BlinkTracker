@file:OptIn(ExperimentalMviKotlinApi::class)

package com.sedsoftware.blinktracker.components.tracker.store

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
import com.sedsoftware.blinktracker.components.tracker.tools.PictureInPictureLauncher
import com.sedsoftware.blinktracker.settings.Settings
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock.System
import java.lang.ref.WeakReference
import kotlin.time.Duration.Companion.milliseconds

internal class BlinkTrackerStoreProvider(
    private val storeFactory: StoreFactory,
    private val settings: Settings,
    private val pipLauncher: WeakReference<PictureInPictureLauncher>,
) {

    @Suppress("CyclomaticComplexMethod")
    fun provide(): BlinkTrackerStore =
        object : BlinkTrackerStore, Store<Intent, State, Label> by storeFactory.create<Intent, Action, Msg, State, Label>(
            name = "BlinkTrackerStore",
            initialState = State(),
            bootstrapper = coroutineBootstrapper {
                launch {
                    dispatch(Action.ObserveThresholdOption)
                    dispatch(Action.ObserveNotifySoundOption)
                    dispatch(Action.ObserveNotifyVibrationOption)
                    dispatch(Action.ObserveLaunchOption)

                    (0..Int.MAX_VALUE)
                        .asSequence()
                        .asFlow()
                        .collect {
                            delay(TIMER_DELAY)
                            dispatch(Action.OnTick)
                        }
                }
            },
            executorFactory = coroutineExecutorFactory {
                onAction<Action.ObserveThresholdOption> {
                    launch(getExceptionHandler(this)) {
                        settings.getPerMinuteThreshold()
                            .collect { dispatch(Msg.ObservedThresholdOptionChanged(it.toInt())) }
                    }
                }

                onAction<Action.ObserveNotifySoundOption> {
                    launch(getExceptionHandler(this)) {
                        settings.getNotifySoundEnabled()
                            .collect { dispatch(Msg.ObservedSoundOptionChanged(it)) }
                    }
                }

                onAction<Action.ObserveNotifyVibrationOption> {
                    launch(getExceptionHandler(this)) {
                        settings.getNotifyVibrationEnabled()
                            .collect { dispatch(Msg.ObservedVibrationOptionChanged(it)) }
                    }
                }

                onAction<Action.ObserveLaunchOption> {
                    launch(getExceptionHandler(this)) {
                        settings.getLaunchMinimizedEnabled()
                            .collect { dispatch(Msg.ObservedLaunchOptionChanged(it)) }
                    }
                }

                onAction<Action.OnTick> {
                    val state = state()
                    if (state.active) {
                        val counter = state.timer

                        if (counter > 0 && counter % MEASURE_PERIOD_SEC == 0) {
                            if (state.blinkLastMinute < state.threshold) {
                                if (state.notifyWithSound) publish(Label.SoundNotificationTriggered)
                                if (state.notifyWithVibration) publish(Label.VibrationNotificationTriggered)
                            }
                            publish(Label.BlinksPerMinuteAvailable(state.blinkLastMinute))
                            dispatch(Msg.ResetMinute)
                        }

                        dispatch(Msg.Tick(counter + 1))
                    }
                }

                onIntent<Intent.OnTrackingStart> {
                    dispatch(Msg.TrackerStateChangedStarted(true))
                    val state = state()
                    if (state.shouldLaunchMinimized) {
                        pipLauncher.get()?.launchPictureInPicture()
                    }
                }

                onIntent<Intent.OnTrackingStop> {
                    dispatch(Msg.TrackerStateChangedStarted(false))
                }

                onIntent<Intent.FaceDataChanged> {
                    dispatch(Msg.FaceDataAvailable(it.data))
                    val state = state()
                    if (state.active && it.data.hasEyesData() && state.blinkPeriodEnded()) {
                        dispatch(Msg.Blink)
                    }
                }

                onIntent<Intent.MinimizedStateChanged> {
                    dispatch(Msg.MinimizedStateChanged(it.minimized))
                }

                onIntent<Intent.OnLaunchPip> {
                    pipLauncher.get()?.launchPictureInPicture()
                }
            },
            reducer = { msg ->
                when (msg) {
                    is Msg.ObservedThresholdOptionChanged -> copy(
                        threshold = msg.newValue
                    )

                    is Msg.ObservedSoundOptionChanged -> copy(
                        notifyWithSound = msg.newValue
                    )

                    is Msg.ObservedVibrationOptionChanged -> copy(
                        notifyWithVibration = msg.newValue
                    )

                    is Msg.ObservedLaunchOptionChanged -> copy(
                        shouldLaunchMinimized = msg.newValue
                    )

                    is Msg.FaceDataAvailable -> copy(
                        faceDetected = msg.data.faceAvailable
                    )

                    is Msg.TrackerStateChangedStarted -> copy(
                        active = msg.started
                    )

                    is Msg.Tick -> copy(
                        timer = msg.seconds
                    )

                    is Msg.Blink -> copy(
                        blinkLastMinute = this.blinkLastMinute + 1,
                        blinksTotal = this.blinksTotal + 1,
                        lastBlink = System.now(),
                    )

                    is Msg.ResetMinute -> copy(
                        blinkLastMinute = 0
                    )

                    is Msg.MinimizedStateChanged -> copy(
                        minimized = msg.minimized
                    )
                }
            }
        ) {}

    private sealed interface Action {
        data object ObserveThresholdOption : Action
        data object ObserveNotifySoundOption : Action
        data object ObserveNotifyVibrationOption : Action
        data object ObserveLaunchOption : Action
        data object OnTick : Action
    }

    private sealed interface Msg {
        data class ObservedThresholdOptionChanged(val newValue: Int) : Msg
        data class ObservedSoundOptionChanged(val newValue: Boolean) : Msg
        data class ObservedVibrationOptionChanged(val newValue: Boolean) : Msg
        data class ObservedLaunchOptionChanged(val newValue: Boolean) : Msg
        data class FaceDataAvailable(val data: VisionFaceData) : Msg
        data class TrackerStateChangedStarted(val started: Boolean) : Msg
        data class Tick(val seconds: Int) : Msg
        data object Blink : Msg
        data object ResetMinute : Msg
        data class MinimizedStateChanged(val minimized: Boolean) : Msg
    }

    private fun getExceptionHandler(scope: CoroutineExecutorScope<State, Msg, Action, Label>): CoroutineExceptionHandler =
        CoroutineExceptionHandler { _, throwable ->
            scope.publish(Label.ErrorCaught(throwable))
        }

    private fun VisionFaceData.hasEyesData(): Boolean =
        this.leftEye != null && this.leftEye < BLINK_THRESHOLD && this.rightEye != null && this.rightEye < BLINK_THRESHOLD

    private fun State.blinkPeriodEnded(): Boolean =
        this.lastBlink < System.now().minus(BLINK_REGISTER_PERIOD_MS.milliseconds)

    private companion object {
        const val TIMER_DELAY = 1000L
        const val BLINK_THRESHOLD = 0.25f
        const val BLINK_REGISTER_PERIOD_MS = 500L
        const val MEASURE_PERIOD_SEC = 60
    }
}
