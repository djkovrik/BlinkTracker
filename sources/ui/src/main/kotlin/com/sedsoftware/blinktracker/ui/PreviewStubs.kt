package com.sedsoftware.blinktracker.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.sedsoftware.blinktracker.components.camera.BlinkCamera
import com.sedsoftware.blinktracker.components.camera.model.CameraLens
import com.sedsoftware.blinktracker.components.camera.model.CameraState
import com.sedsoftware.blinktracker.components.camera.model.PermissionState
import com.sedsoftware.blinktracker.components.preferences.BlinkPreferences
import com.sedsoftware.blinktracker.components.statistic.BlinkStatistic
import com.sedsoftware.blinktracker.components.statistic.model.StatRecord
import com.sedsoftware.blinktracker.components.tracker.BlinkTracker
import kotlinx.datetime.LocalDateTime

@Suppress("MemberVisibilityCanBePrivate")
object PreviewStubs {
    val cameraPermissionDenied = BlinkCamera.Model(
        currentPermissionState = PermissionState.DENIED,
        selectedLens = CameraLens.NOT_AVAILABLE,
        cameraState = CameraState.NOT_DETECTED,
    )

    val cameraPermissionGranted = BlinkCamera.Model(
        currentPermissionState = PermissionState.GRANTED,
        selectedLens = CameraLens.FRONT,
        CameraState.DETECTED,
    )

    val cameraPermissionGrantedNoCamera = BlinkCamera.Model(
        currentPermissionState = PermissionState.GRANTED,
        selectedLens = CameraLens.NOT_AVAILABLE,
        CameraState.NOT_DETECTED,
    )

    val cameraPermissionRationale = BlinkCamera.Model(
        currentPermissionState = PermissionState.RATIONALE,
        selectedLens = CameraLens.NOT_AVAILABLE,
        CameraState.NOT_DETECTED,
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

    val trackerActiveWithNoFace = trackerActiveWithFace.copy(
        hasFaceDetected = false,
    )

    private val dummyRecords = listOf(
        StatRecord(blinks = 14, dateTime = LocalDateTime(2023, 6, 13, 12, 34)),
        StatRecord(blinks = 12, dateTime = LocalDateTime(2023, 6, 13, 12, 35)),
        StatRecord(blinks = 18, dateTime = LocalDateTime(2023, 6, 13, 12, 36)),
        StatRecord(blinks = 21, dateTime = LocalDateTime(2023, 6, 13, 12, 37)),
        StatRecord(blinks = 12, dateTime = LocalDateTime(2023, 6, 13, 12, 38)),
        StatRecord(blinks = 13, dateTime = LocalDateTime(2023, 6, 13, 12, 39)),
        StatRecord(blinks = 13, dateTime = LocalDateTime(2023, 6, 13, 12, 40)),
        StatRecord(blinks = 17, dateTime = LocalDateTime(2023, 6, 13, 13, 11)),
        StatRecord(blinks = 16, dateTime = LocalDateTime(2023, 6, 13, 12, 12)),
        StatRecord(blinks = 10, dateTime = LocalDateTime(2023, 6, 13, 12, 13)),
        StatRecord(blinks = 18, dateTime = LocalDateTime(2023, 6, 13, 12, 14)),
        StatRecord(blinks = 17, dateTime = LocalDateTime(2023, 6, 13, 12, 15)),
        StatRecord(blinks = 18, dateTime = LocalDateTime(2023, 6, 14, 15, 21)),
        StatRecord(blinks = 16, dateTime = LocalDateTime(2023, 6, 13, 12, 22)),
        StatRecord(blinks = 15, dateTime = LocalDateTime(2023, 6, 13, 12, 23)),
        StatRecord(blinks = 14, dateTime = LocalDateTime(2023, 6, 13, 12, 24)),
        StatRecord(blinks = 13, dateTime = LocalDateTime(2023, 6, 13, 12, 25)),
    )

    val statsEmptyNotChecked = BlinkStatistic.Model(
        records = emptyList(),
        average = 0f,
        min = 0,
        max = 0,
        checked = false,
        showPlaceholder = false,
    )

    val statsEmptyChecked = BlinkStatistic.Model(
        records = emptyList(),
        average = 0f,
        min = 0,
        max = 0,
        checked = true,
        showPlaceholder = true,
    )

    val statsOneRecord = BlinkStatistic.Model(
        average = 12f,
        min = 12,
        max = 15,
        checked = true,
        showPlaceholder = false,
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
