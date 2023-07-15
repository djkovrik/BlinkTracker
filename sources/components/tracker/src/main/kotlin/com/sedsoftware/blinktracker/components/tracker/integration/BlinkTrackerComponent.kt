package com.sedsoftware.blinktracker.components.tracker.integration

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.lifecycle.doOnDestroy
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.extensions.coroutines.states
import com.sedsoftware.blinktracker.components.tracker.BlinkTracker
import com.sedsoftware.blinktracker.components.tracker.BlinkTracker.Model
import com.sedsoftware.blinktracker.components.tracker.model.VisionFaceData
import com.sedsoftware.blinktracker.components.tracker.store.BlinkTrackerStore
import com.sedsoftware.blinktracker.components.tracker.store.BlinkTrackerStoreProvider
import com.sedsoftware.blinktracker.components.tracker.tools.MinimizedLauncher
import com.sedsoftware.blinktracker.components.tracker.tools.PictureInPictureLauncher
import com.sedsoftware.blinktracker.settings.Settings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import java.lang.ref.WeakReference

class BlinkTrackerComponent(
    private val componentContext: ComponentContext,
    private val storeFactory: StoreFactory,
    private val settings: Settings,
    private val pipLauncher: WeakReference<PictureInPictureLauncher>,
    private val minimizedLauncher: MinimizedLauncher,
    private val output: (BlinkTracker.Output) -> Unit,
) : BlinkTracker, ComponentContext by componentContext {

    private val store: BlinkTrackerStore =
        instanceKeeper.getStore {
            BlinkTrackerStoreProvider(
                storeFactory = storeFactory,
                settings = settings,
                pipLauncher = pipLauncher,
                minimizedLauncher = minimizedLauncher,
            ).provide()
        }

    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Main)

    init {
        store.labels
            .onEach { label ->
                when (label) {
                    is BlinkTrackerStore.Label.ErrorCaught -> {
                        output(BlinkTracker.Output.ErrorCaught(label.throwable))
                    }

                    is BlinkTrackerStore.Label.SoundNotificationTriggered -> {
                        output(BlinkTracker.Output.SoundNotificationTriggered)
                    }

                    is BlinkTrackerStore.Label.VibrationNotificationTriggered -> {
                        output(BlinkTracker.Output.VibroNotificationTriggered)
                    }

                    is BlinkTrackerStore.Label.BlinksPerMinuteAvailable -> {
                        output(BlinkTracker.Output.BlinkedPerMinute(label.value))
                    }

                    is BlinkTrackerStore.Label.NotificationDataAvailable -> {
                        output(BlinkTracker.Output.NotificationDataChanged(label.data))
                    }
                }
            }
            .launchIn(scope)

        lifecycle.doOnDestroy(scope::cancel)
    }

    override val models: Flow<Model> = store.states.map { stateToModel(it) }

    override val initial: Model = stateToModel(BlinkTrackerStore.State())

    override fun onTrackingStarted() {
        store.accept(BlinkTrackerStore.Intent.OnTrackingStart)
    }

    override fun onTrackingStopped() {
        store.accept(BlinkTrackerStore.Intent.OnTrackingStop)
    }

    override fun onFaceDataChanged(data: VisionFaceData) {
        store.accept(BlinkTrackerStore.Intent.FaceDataChanged(data))
    }

    override fun onMinimizeRequested() {
        store.accept(BlinkTrackerStore.Intent.OnMinimizeRequest)
    }

    override fun onPictureInPictureChanged(enabled: Boolean) {
        store.accept(BlinkTrackerStore.Intent.MinimizedStateChanged(enabled))
    }
}
