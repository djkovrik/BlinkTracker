package com.sedsoftware.blinktracker.components.camera

import com.sedsoftware.blinktracker.components.camera.model.CameraLens
import com.sedsoftware.blinktracker.components.camera.model.CameraState
import com.sedsoftware.blinktracker.components.camera.model.PermissionStateCamera
import kotlinx.coroutines.flow.Flow

interface BlinkCamera {

    val models: Flow<Model>
    val initial: Model

    fun onPermissionGranted()
    fun onPermissionDenied()
    fun onPermissionRationale()
    fun onCurrentLensChanged(lens: CameraLens)

    data class Model(
        val currentPermissionState: PermissionStateCamera,
        val selectedLens: CameraLens,
        val cameraState: CameraState,
    )
}
