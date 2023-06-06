package com.sedsoftware.blinktracker.components.camera.integration

import com.sedsoftware.blinktracker.components.camera.BlinkCamera.Model
import com.sedsoftware.blinktracker.components.camera.model.CameraLens
import com.sedsoftware.blinktracker.components.camera.store.BlinkCameraStore.State

internal val stateToModel: (State) -> Model =
    {
        Model(
            currentPermissionState = it.permissionState,
            selectedLens = when (it.lensFacing) {
                CameraLens.NOT_AVAILABLE -> -1
                CameraLens.FRONT -> 0
                CameraLens.BACK -> 1
            },
            cameraAvailable = it.cameraAvailable,
        )
    }
