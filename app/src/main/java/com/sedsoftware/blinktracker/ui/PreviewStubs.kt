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
object PreviewStubs {
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

    val prefsAllDisabled = BlinkPreferences.Model(
        selectedThreshold = 12f,
        notifySoundChecked = false,
        notifyVibrationChecked = false,
        launchMinimized = false,
    )

    val prefsMixed = BlinkPreferences.Model(
        selectedThreshold = 16f,
        notifySoundChecked = false,
        notifyVibrationChecked = true,
        launchMinimized = true,
    )

    val trackerNotActiveNoFaceNoPrefs = BlinkTracker.Model(
        isTrackingActive = false,
        timerLabel = "00:00",
        blinksPerLastMinute = 0,
        blinksTotal = 0,
        isMinimized = false,
        hasFaceDetected = false,
        isPreferencesPanelVisible = false,
    )

    val trackerNotActiveWithFaceNoPrefs = trackerNotActiveNoFaceNoPrefs.copy(
        hasFaceDetected = true,
    )

    val trackerNotActiveWithFaceAndPrefs = trackerNotActiveNoFaceNoPrefs.copy(
        hasFaceDetected = true,
        isPreferencesPanelVisible = true,
    )

    val trackerActiveWithFace = BlinkTracker.Model(
        isTrackingActive = true,
        timerLabel = "02:45",
        blinksPerLastMinute = 12,
        blinksTotal = 15,
        isMinimized = false,
        hasFaceDetected = true,
        isPreferencesPanelVisible = false,
    )
}

@Composable
fun CameraStub() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Gray)
    )
}
