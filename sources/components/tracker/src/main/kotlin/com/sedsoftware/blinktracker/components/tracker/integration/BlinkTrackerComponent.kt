package com.sedsoftware.blinktracker.components.tracker.integration

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.extensions.coroutines.states
import com.sedsoftware.blinktracker.components.tracker.BlinkTracker
import com.sedsoftware.blinktracker.components.tracker.BlinkTracker.Model
import com.sedsoftware.blinktracker.components.tracker.model.VisionFaceData
import com.sedsoftware.blinktracker.components.tracker.store.BlinkTrackerStore
import com.sedsoftware.blinktracker.components.tracker.store.BlinkTrackerStoreProvider
import com.sedsoftware.blinktracker.settings.Settings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

class BlinkTrackerComponent(
    private val componentContext: ComponentContext,
    private val storeFactory: StoreFactory,
    private val settings: Settings,
    private val output: (BlinkTracker.Output) -> Unit,
) : BlinkTracker, ComponentContext by componentContext {

    private val store: BlinkTrackerStore =
        instanceKeeper.getStore {
            BlinkTrackerStoreProvider(
                storeFactory = storeFactory,
                settings = settings,
            ).provide()
        }

    init {
        store.labels.onEach { label ->
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
            }
        }
    }

    override val models: Flow<Model> = store.states.map { stateToModel(it) }

    override val initial: Model = stateToModel(BlinkTrackerStore.State())

    override fun onTrackingStarted() {
        store.accept(BlinkTrackerStore.Intent.SettingsPanelClosed)
        store.accept(BlinkTrackerStore.Intent.TrackingStarted)
    }

    override fun onTrackingStopped() {
        store.accept(BlinkTrackerStore.Intent.TrackingStopped)
    }

    override fun onFaceDataChanged(data: VisionFaceData) {
        store.accept(BlinkTrackerStore.Intent.FaceDataChanged(data))
    }

    override fun onMinimizeActivated() {
        store.accept(BlinkTrackerStore.Intent.MinimizedStateChanged(true))
    }

    override fun onMinimizeDeactivated() {
        store.accept(BlinkTrackerStore.Intent.MinimizedStateChanged(false))
    }

    override fun showPreferencesPanel() {
        store.accept(BlinkTrackerStore.Intent.SettingsPanelRequested)
    }

    override fun closePreferencesPanel() {
        store.accept(BlinkTrackerStore.Intent.SettingsPanelClosed)
    }
}
