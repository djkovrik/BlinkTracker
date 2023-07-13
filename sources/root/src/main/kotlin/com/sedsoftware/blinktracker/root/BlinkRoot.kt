package com.sedsoftware.blinktracker.root

import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.Value
import com.sedsoftware.blinktracker.components.camera.model.CameraLens
import com.sedsoftware.blinktracker.components.home.BlinkHome
import com.sedsoftware.blinktracker.components.preferences.BlinkPreferences
import com.sedsoftware.blinktracker.components.tracker.model.VisionFaceData

interface BlinkRoot {

    val childStack: Value<ChildStack<*, Child>>

    fun openPreferencesScreen()
    fun closePreferencesScreen()
    fun onFaceDataChanged(data: VisionFaceData)
    fun onPictureInPictureChanged(enabled: Boolean)
    fun onCameraPermissionGranted()
    fun onCameraPermissionDenied()
    fun onCameraPermissionRationale()
    fun onNotificationPermissionGranted()
    fun onNotificationPermissionRationale()
    fun onNotificationPermissionDenied()
    fun onCurrentLensChanged(lens: CameraLens)

    sealed class Child {
        data class Home(val component: BlinkHome) : Child()
        data class Preferences(val component: BlinkPreferences) : Child()
    }
}
