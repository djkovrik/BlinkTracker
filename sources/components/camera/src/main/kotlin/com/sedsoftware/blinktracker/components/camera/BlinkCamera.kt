package com.sedsoftware.blinktracker.components.camera

import com.sedsoftware.blinktracker.components.camera.model.CameraLens
import com.sedsoftware.blinktracker.components.camera.model.PermissionState
import kotlinx.coroutines.flow.Flow

interface BlinkCamera {

    val models: Flow<Model>
    val initial: Model

    fun onPermissionGranted()
    fun onPermissionDenied()
    fun onPermissionRationale()
    fun onCurrentLensChanged(lens: CameraLens)

    data class Model(
        val currentPermissionState: PermissionState,
        val selectedLens: Int,
        val cameraAvailable: Boolean,
    )
}
