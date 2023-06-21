package com.sedsoftware.blinktracker.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.sedsoftware.blinktracker.components.camera.BlinkCamera
import com.sedsoftware.blinktracker.components.preferences.BlinkPreferences
import com.sedsoftware.blinktracker.components.statistic.BlinkStatistic
import com.sedsoftware.blinktracker.components.tracker.BlinkTracker
import com.sedsoftware.blinktracker.root.BlinkRoot
import com.sedsoftware.blinktracker.ui.camera.CameraPreviewComposable
import com.sedsoftware.blinktracker.ui.camera.core.VisionImageProcessor
import com.sedsoftware.blinktracker.ui.component.CustomAppBar
import com.sedsoftware.blinktracker.ui.component.RightModalDrawer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

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

    val statsState: BlinkStatistic.Model by root.statsComponent.models
        .collectAsState(initial = root.statsComponent.initial)

    val snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(root.errorHandler) {
        root.errorHandler.messages.collect { message ->
            message?.let { snackbarHostState.showSnackbar(message = it) }
        }
    }

    val scope: CoroutineScope = rememberCoroutineScope()
    val drawerState: DrawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    if (!trackerState.isMinimized) {
        RightModalDrawer(
            drawerState = drawerState,
        ) {
            Scaffold(
                modifier = modifier,
                snackbarHost = {
                    SnackbarHost(hostState = snackbarHostState)
                },
                topBar = {
                    CustomAppBar(
                        cameraModel = cameraState,
                        trackerModel = trackerState,
                        modifier = modifier,
                        onHelpIconClick = {
                            scope.launch {
                                drawerState.open()
                            }
                        }
                    ) {
                        CameraPreviewComposable(
                            imageProcessor = processor,
                            lensFacing = cameraState.selectedLens,
                            modifier = modifier.fillMaxSize(),
                        )
                    }
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
                        stats = statsState,
                        modifier = modifier,
                        onStartClick = root.trackerComponent::onTrackingStarted,
                        onStopClick = root.trackerComponent::onTrackingStopped,
                        onMinimizeClick = root.trackerComponent::onMinimizeRequested,
                        onSettingsClick = root.trackerComponent::onPreferencesPanelToggle,
                        onThresholdChange = root.preferencesComponent::onMinimalThresholdChanged,
                        onLaunchMinimizedChange = root.preferencesComponent::onLaunchMinimizedChanged,
                        onNotifySoundChange = root.preferencesComponent::onNotifySoundChanged,
                        onNotifyVibroChange = root.preferencesComponent::onNotifyVibrationChanged
                    )
                }
            }
        }
    } else {
        BlinkMinimized(
            model = trackerState,
            modifier = modifier,
        )
    }
}
