package com.sedsoftware.blinktracker.components.camera

import com.arkivanov.decompose.value.Value
import com.sedsoftware.blinktracker.components.camera.model.CameraLens
import com.sedsoftware.blinktracker.components.camera.model.PermissionState

interface BlinkCamera {

    val models: Value<Model>

    fun onPermissionGranted()
    fun onPermissionDenied()
    fun onPermissionRationale()
    fun onCurrentLensChanged(lens: CameraLens)

    data class Model(
        val currentPermissionState: PermissionState,
        val selectedLens: CameraLens,
    )

    sealed class Output {
        object PreferencesPanelRequested : Output()
        object PreferencesPanelClosed : Output()
    }
}
