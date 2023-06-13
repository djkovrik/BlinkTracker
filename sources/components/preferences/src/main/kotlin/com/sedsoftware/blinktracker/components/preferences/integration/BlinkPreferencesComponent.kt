package com.sedsoftware.blinktracker.components.preferences.integration

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.extensions.coroutines.states
import com.sedsoftware.blinktracker.components.preferences.BlinkPreferences
import com.sedsoftware.blinktracker.components.preferences.BlinkPreferences.Model
import com.sedsoftware.blinktracker.components.preferences.store.BlinkPreferencesStore
import com.sedsoftware.blinktracker.components.preferences.store.BlinkPreferencesStoreProvider
import com.sedsoftware.blinktracker.settings.Settings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

class BlinkPreferencesComponent(
    private val componentContext: ComponentContext,
    private val storeFactory: StoreFactory,
    private val settings: Settings,
    private val output: (BlinkPreferences.Output) -> Unit,
) : BlinkPreferences, ComponentContext by componentContext {

    private val store: BlinkPreferencesStore =
        instanceKeeper.getStore {
            BlinkPreferencesStoreProvider(
                storeFactory = storeFactory,
                settings = settings,
            ).provide()
        }

    init {
        store.labels.onEach { label ->
            when (label) {
                is BlinkPreferencesStore.Label.ErrorCaught -> {
                    output(BlinkPreferences.Output.ErrorCaught(label.throwable))
                }
            }
        }
    }

    override val models: Flow<Model> = store.states.map { stateToModel(it) }

    override val initial: Model = stateToModel(BlinkPreferencesStore.State())

    override fun onMinimalThresholdChanged(value: Float) {
        store.accept(BlinkPreferencesStore.Intent.MinimalThresholdChanged(value))
    }

    override fun onNotifySoundChanged(value: Boolean) {
        store.accept(BlinkPreferencesStore.Intent.NotifySoundChanged(value))
    }

    override fun onNotifyVibrationChanged(value: Boolean) {
        store.accept(BlinkPreferencesStore.Intent.NotifyVibrationChanged(value))
    }

    override fun onLaunchMinimizedChanged(value: Boolean) {
        store.accept(BlinkPreferencesStore.Intent.LaunchMinimizedChanged(value))
    }
}
