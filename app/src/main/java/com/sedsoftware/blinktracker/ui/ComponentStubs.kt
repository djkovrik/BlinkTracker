package com.sedsoftware.blinktracker.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.sedsoftware.blinktracker.components.camera.BlinkCamera
import com.sedsoftware.blinktracker.components.camera.model.CameraLens
import com.sedsoftware.blinktracker.components.camera.model.PermissionState
import com.sedsoftware.blinktracker.components.preferences.BlinkPreferences
import com.sedsoftware.blinktracker.components.tracker.BlinkTracker

@Suppress("MemberVisibilityCanBePrivate")
object ComponentStubs {
    val cameraPermissionDenied = BlinkCamera.Model(
        currentPermissionState = PermissionState.DENIED,
        selectedLens = CameraLens.NOT_AVAILABLE,
        cameraAvailable = false,
    )

    val cameraPermissionGranted = BlinkCamera.Model(
        currentPermissionState = PermissionState.GRANTED,
        selectedLens = CameraLens.FRONT,
        cameraAvailable = true,
    )

    val cameraPermissionGrantedNoCamera = BlinkCamera.Model(
        currentPermissionState = PermissionState.GRANTED,
        selectedLens = CameraLens.NOT_AVAILABLE,
        cameraAvailable = false,
    )

    val cameraPermissionRationale = BlinkCamera.Model(
        currentPermissionState = PermissionState.RATIONALE,
        selectedLens = CameraLens.NOT_AVAILABLE,
        cameraAvailable = false,
    )

    val trackerNotActiveNoFace = BlinkTracker.Model(
        isTrackingActive = false,
        totalTrackedSeconds = 0,
        blinksPerLastMinute = 0,
        blinksTotal = 0,
        isMinimized = false,
        hasFaceDetected = false,
    )

    val prefsNotVisibleAllDisabled = BlinkPreferences.Model(
        settingsPanelVisible = false,
        selectedThreshold = 12,
        notifySoundChecked = false,
        notifyVibrationChecked = false,
    )

    val prefsVisibleAllDisabled = BlinkPreferences.Model(
        settingsPanelVisible = true,
        selectedThreshold = 12,
        notifySoundChecked = false,
        notifyVibrationChecked = false,
    )

    val prefsVisibleAllEnabled = BlinkPreferences.Model(
        settingsPanelVisible = true,
        selectedThreshold = 12,
        notifySoundChecked = true,
        notifyVibrationChecked = true,
    )

    val trackerNotActiveWithFace = trackerNotActiveNoFace.copy(
        hasFaceDetected = true,
    )

    val trackerActiveNoFace = BlinkTracker.Model(
        isTrackingActive = true,
        totalTrackedSeconds = 123,
        blinksPerLastMinute = 12,
        blinksTotal = 15,
        isMinimized = false,
        hasFaceDetected = false,
    )

    val trackerActiveWithFace = BlinkTracker.Model(
        isTrackingActive = true,
        totalTrackedSeconds = 123,
        blinksPerLastMinute = 12,
        blinksTotal = 15,
        isMinimized = false,
        hasFaceDetected = true,
    )
}

@Composable
fun CameraStub() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Green)
    )
}
