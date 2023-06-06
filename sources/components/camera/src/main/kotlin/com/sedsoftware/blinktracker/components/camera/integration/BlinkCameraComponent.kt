package com.sedsoftware.blinktracker.components.camera.integration

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.states
import com.sedsoftware.blinktracker.components.camera.BlinkCamera
import com.sedsoftware.blinktracker.components.camera.BlinkCamera.Model
import com.sedsoftware.blinktracker.components.camera.model.CameraLens
import com.sedsoftware.blinktracker.components.camera.store.BlinkCameraStore
import com.sedsoftware.blinktracker.components.camera.store.BlinkCameraStoreProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class BlinkCameraComponent(
    private val componentContext: ComponentContext,
    private val storeFactory: StoreFactory,
) : BlinkCamera, ComponentContext by componentContext {

    private val store: BlinkCameraStore =
        instanceKeeper.getStore {
            BlinkCameraStoreProvider(
                storeFactory = storeFactory,
            ).provide()
        }

    override val models: Flow<Model> = store.states.map { stateToModel(it) }

    override val initial: Model = stateToModel(BlinkCameraStore.State())

    override fun onPermissionGranted() {
        store.accept(BlinkCameraStore.Intent.PermissionGranted)
    }

    override fun onPermissionDenied() {
        store.accept(BlinkCameraStore.Intent.PermissionDenied)
    }

    override fun onPermissionRationale() {
        store.accept(BlinkCameraStore.Intent.PermissionRationale)
    }

    override fun onCurrentLensChanged(lens: CameraLens) {
        store.accept(BlinkCameraStore.Intent.LensChanged(lens))
    }
}
