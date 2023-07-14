package com.sedsoftware.blinktracker.ui.preview

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.sedsoftware.blinktracker.components.camera.BlinkCamera
import com.sedsoftware.blinktracker.components.camera.model.CameraLens
import com.sedsoftware.blinktracker.components.camera.model.CameraState
import com.sedsoftware.blinktracker.components.camera.model.PermissionStateCamera
import com.sedsoftware.blinktracker.components.preferences.BlinkPreferences
import com.sedsoftware.blinktracker.components.preferences.model.PermissionStateNotification
import com.sedsoftware.blinktracker.components.statistic.BlinkStatistic
import com.sedsoftware.blinktracker.components.statistic.model.CustomChartEntry
import com.sedsoftware.blinktracker.components.statistic.model.DisplayedPeriod
import com.sedsoftware.blinktracker.components.tracker.BlinkTracker

@Suppress("MemberVisibilityCanBePrivate")
object PreviewStubs {
    val cameraPermissionDenied = BlinkCamera.Model(
        currentPermissionState = PermissionStateCamera.DENIED,
        selectedLens = CameraLens.NOT_AVAILABLE,
        cameraState = CameraState.NOT_DETECTED,
    )

    val cameraPermissionGranted = BlinkCamera.Model(
        currentPermissionState = PermissionStateCamera.GRANTED,
        selectedLens = CameraLens.FRONT,
        CameraState.DETECTED,
    )

    val cameraPermissionGrantedNoCamera = BlinkCamera.Model(
        currentPermissionState = PermissionStateCamera.GRANTED,
        selectedLens = CameraLens.NOT_AVAILABLE,
        CameraState.NOT_DETECTED,
    )

    val cameraPermissionRationale = BlinkCamera.Model(
        currentPermissionState = PermissionStateCamera.RATIONALE,
        selectedLens = CameraLens.NOT_AVAILABLE,
        CameraState.NOT_DETECTED,
    )

    val prefsAllDisabled = BlinkPreferences.Model(
        selectedThreshold = 12f,
        notifySoundChecked = false,
        notifyVibrationChecked = false,
        launchMinimized = false,
        replacePip = false,
        permissionState = PermissionStateNotification.DENIED,
        showRationale = false,
    )

    val prefsMixed = BlinkPreferences.Model(
        selectedThreshold = 16f,
        notifySoundChecked = false,
        notifyVibrationChecked = true,
        launchMinimized = true,
        replacePip = false,
        permissionState = PermissionStateNotification.GRANTED,
        showRationale = true,
    )

    val trackerNotActiveNoFaceNoPrefs = BlinkTracker.Model(
        isTrackingActive = false,
        timerLabel = "00:00",
        blinksPerLastMinute = 0,
        blinksTotal = 0,
        isMinimized = false,
        hasFaceDetected = false,
    )

    val trackerNotActiveWithFaceNoPrefs = trackerNotActiveNoFaceNoPrefs.copy(
        hasFaceDetected = true,
    )

    val trackerNotActiveWithFaceAndPrefs = trackerNotActiveNoFaceNoPrefs.copy(
        hasFaceDetected = true,
    )

    val trackerActiveWithFace = BlinkTracker.Model(
        isTrackingActive = true,
        timerLabel = "02:45",
        blinksPerLastMinute = 12,
        blinksTotal = 15,
        isMinimized = false,
        hasFaceDetected = true,
    )

    val trackerActiveWithNoFace = trackerActiveWithFace.copy(
        hasFaceDetected = false,
    )

    private val dummyRecords = listOf(
        CustomChartEntry(label = "11:12", x = 1f, y = 12f),
        CustomChartEntry(label = "11:13", x = 2f, y = 17f),
        CustomChartEntry(label = "11:14", x = 3f, y = 16f),
        CustomChartEntry(label = "11:15", x = 4f, y = 13f),
        CustomChartEntry(label = "11:16", x = 5f, y = 14f),
        CustomChartEntry(label = "11:17", x = 6f, y = 18f),
    )

    val statsEmptyNotChecked = BlinkStatistic.Model(
        records = emptyList(),
        period = DisplayedPeriod.MINUTE,
        average = 0f,
        min = 0f,
        max = 0f,
        isLoading = true,
        isEmpty = false,
    )

    val statsEmptyChecked = BlinkStatistic.Model(
        records = emptyList(),
        period = DisplayedPeriod.MINUTE,
        average = 0f,
        min = 0f,
        max = 0f,
        isLoading = false,
        isEmpty = true,
    )

    val statsOneRecord = BlinkStatistic.Model(
        isEmpty = false,
        isLoading = false,
        period = DisplayedPeriod.HOUR,
        average = 12f,
        min = 12f,
        max = 15f,
        records = dummyRecords.take(1)
    )

    val statsFull = statsOneRecord.copy(
        records = dummyRecords,
        average = 12.34f,
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
