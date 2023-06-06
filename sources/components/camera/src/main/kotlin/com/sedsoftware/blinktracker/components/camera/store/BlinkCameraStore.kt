package com.sedsoftware.blinktracker.components.camera.store

import com.arkivanov.mvikotlin.core.store.Store
import com.sedsoftware.blinktracker.components.camera.model.CameraLens
import com.sedsoftware.blinktracker.components.camera.model.PermissionState
import com.sedsoftware.blinktracker.components.camera.store.BlinkCameraStore.Intent
import com.sedsoftware.blinktracker.components.camera.store.BlinkCameraStore.Label
import com.sedsoftware.blinktracker.components.camera.store.BlinkCameraStore.State

internal interface BlinkCameraStore : Store<Intent, State, Label> {

    sealed class Intent {
        object PermissionGranted : Intent()
        object PermissionDenied : Intent()
        object PermissionRationale : Intent()
        data class LensChanged(val lens: CameraLens) : Intent()
    }

    data class State(
        val permissionState: PermissionState = PermissionState.NOT_ASKED,
        val lensFacing: CameraLens = CameraLens.NOT_AVAILABLE,
        val cameraAvailable: Boolean = false,
    )

    class Label
}
