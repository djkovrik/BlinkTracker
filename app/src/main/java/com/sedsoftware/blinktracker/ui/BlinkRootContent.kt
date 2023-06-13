package com.sedsoftware.blinktracker.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.sedsoftware.blinktracker.camera.core.VisionImageProcessor
import com.sedsoftware.blinktracker.components.camera.BlinkCamera
import com.sedsoftware.blinktracker.components.preferences.BlinkPreferences
import com.sedsoftware.blinktracker.components.tracker.BlinkTracker
import com.sedsoftware.blinktracker.root.BlinkRoot

@Composable
fun BlinkRootContent(
    root: BlinkRoot,
    processor: VisionImageProcessor,
    modifier: Modifier = Modifier,
) {
    val cameraState: BlinkCamera.Model by root.cameraComponent.models
        .collectAsState(initial = root.cameraComponent.initial)

    val preferencesState: BlinkPreferences.Model by root.preferencesComponent.models
        .collectAsState(initial = root.preferencesComponent.initial)

    val trackerState: BlinkTracker.Model by root.trackerComponent.models
        .collectAsState(initial = root.trackerComponent.initial)

    val snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(root.errorHandler) {
        root.errorHandler.messages.collect { message ->
            message?.let { snackbarHostState.showSnackbar(message = it) }
        }
    }

    Scaffold(
        modifier = modifier,
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
    ) { contentPadding: PaddingValues ->
        Surface(
            color = MaterialTheme.colorScheme.surface,
            modifier = modifier
                .fillMaxSize()
                .padding(contentPadding),
        ) {
            BlinkRootScreen(
                camera = cameraState,
                preferences = preferencesState,
                tracker = trackerState,
                modifier = modifier,
                onStartClick = root.trackerComponent::onTrackingStarted,
                onStopClick = root.trackerComponent::onTrackingStopped,
                onMinimizeClick = {
                    if (trackerState.isMinimized) {
                        root.trackerComponent.onMinimizeDeactivated()
                    } else {
                        root.trackerComponent.onMinimizeActivated()
                    }
                },
                onSettingsClick = {
                    if (trackerState.isPreferencesPanelVisible) {
                        root.trackerComponent.closePreferencesPanel()
                    } else {
                        root.trackerComponent.showPreferencesPanel()
                    }
                },
                onThresholdChange = { root.preferencesComponent.onMinimalThresholdChanged(it) },
                onLaunchMinimizedChange = { root.preferencesComponent.onLaunchMinimizedChanged(it) },
                onNotifySoundChange = { root.preferencesComponent.onNotifySoundChanged(it) },
                onNotifyVibroChange = { root.preferencesComponent.onNotifyVibrationChanged(it) }
            )
        }
    }
}
