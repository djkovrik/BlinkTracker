package com.sedsoftware.blinktracker.components.camera.integration

import com.sedsoftware.blinktracker.components.camera.BlinkCamera.Model
import com.sedsoftware.blinktracker.components.camera.store.BlinkCameraStore.State

internal val stateToModel: (State) -> Model =
    {
        Model(
            currentPermissionState = it.permissionState,
            selectedLens = it.lensFacing,
        )
    }
