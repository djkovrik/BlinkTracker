package com.sedsoftware.blinktracker.components.camera.store

import com.arkivanov.mvikotlin.core.store.Store
import com.sedsoftware.blinktracker.components.camera.model.CameraLens
import com.sedsoftware.blinktracker.components.camera.model.CameraState
import com.sedsoftware.blinktracker.components.camera.model.PermissionState
import com.sedsoftware.blinktracker.components.camera.store.BlinkCameraStore.Intent
import com.sedsoftware.blinktracker.components.camera.store.BlinkCameraStore.Label
import com.sedsoftware.blinktracker.components.camera.store.BlinkCameraStore.State

internal interface BlinkCameraStore : Store<Intent, State, Label> {

    sealed class Intent {
        object OnPermissionGrant : Intent()
        object OnPermissionDeny : Intent()
        object OnPermissionRationale : Intent()
        data class OnLensChange(val lens: CameraLens) : Intent()
    }

    data class State(
        val permissionState: PermissionState = PermissionState.NOT_ASKED,
        val lensFacing: CameraLens = CameraLens.NOT_AVAILABLE,
        val cameraState: CameraState = CameraState.NOT_CHECKED,
    )

    class Label
}
