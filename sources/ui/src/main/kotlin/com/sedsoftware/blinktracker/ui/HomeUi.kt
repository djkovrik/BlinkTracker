package com.sedsoftware.blinktracker.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sedsoftware.blinktracker.components.camera.BlinkCamera
import com.sedsoftware.blinktracker.components.camera.model.CameraState
import com.sedsoftware.blinktracker.components.camera.model.PermissionState
import com.sedsoftware.blinktracker.components.home.BlinkHome
import com.sedsoftware.blinktracker.components.statistic.BlinkStatistic
import com.sedsoftware.blinktracker.components.tracker.BlinkTracker
import com.sedsoftware.blinktracker.ui.R.drawable
import com.sedsoftware.blinktracker.ui.camera.CameraPreviewComposable
import com.sedsoftware.blinktracker.ui.camera.core.VisionImageProcessor
import com.sedsoftware.blinktracker.ui.component.CustomAppBar
import com.sedsoftware.blinktracker.ui.component.FullScreenMessageInfo
import com.sedsoftware.blinktracker.ui.component.MainScreenMinimized
import com.sedsoftware.blinktracker.ui.component.RightModalDrawer
import com.sedsoftware.blinktracker.ui.component.TrackingControls
import com.sedsoftware.blinktracker.ui.preview.PreviewStubs
import com.sedsoftware.blinktracker.ui.theme.BlinkTrackerTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun BlinkHomeContent(
    component: BlinkHome,
    processor: VisionImageProcessor,
    modifier: Modifier = Modifier,
    onPreferencesClicked: () -> Unit = {},
) {
    val cameraState: BlinkCamera.Model by component.cameraComponent.models
        .collectAsState(initial = component.cameraComponent.initial)

    val trackerState: BlinkTracker.Model by component.trackerComponent.models
        .collectAsState(initial = component.trackerComponent.initial)

    val statsState: BlinkStatistic.Model by component.statsComponent.models
        .collectAsState(initial = component.statsComponent.initial)

    val snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(component.errorHandler) {
        component.errorHandler.messages.collect { message ->
            message?.let { snackbarHostState.showSnackbar(message = it) }
        }
    }

    BlinkHomeScreen(
        camera = cameraState,
        tracker = trackerState,
        stats = statsState,
        snackbarHostState = snackbarHostState,
        modifier = modifier,
        onStartClick = component.trackerComponent::onTrackingStarted,
        onStopClick = component.trackerComponent::onTrackingStopped,
        onMinimizeClick = component.trackerComponent::onMinimizeRequested,
        onPreferencesClicked = onPreferencesClicked,
    ) {
        CameraPreviewComposable(
            imageProcessor = processor,
            lensFacing = cameraState.selectedLens,
            modifier = modifier.fillMaxSize(),
        )
    }
}

@Composable
private fun BlinkHomeScreen(
    camera: BlinkCamera.Model,
    tracker: BlinkTracker.Model,
    stats: BlinkStatistic.Model,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
    onStartClick: () -> Unit = {},
    onStopClick: () -> Unit = {},
    onMinimizeClick: () -> Unit = {},
    onPreferencesClicked: () -> Unit = {},
    cameraPreview: @Composable () -> Unit = {},
) {

    val scope: CoroutineScope = rememberCoroutineScope()
    val drawerState: DrawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    if (!tracker.isMinimized) {
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
                        cameraModel = camera,
                        trackerModel = tracker,
                        modifier = modifier,
                        onHelpIconClick = { scope.launch { drawerState.open() } },
                        onPreferencesIconClick = onPreferencesClicked,
                    ) {
                        cameraPreview()
                    }
                },
            ) { contentPadding: PaddingValues ->
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    modifier = modifier
                        .fillMaxSize()
                        .padding(contentPadding),
                ) {
                    MainScreenActive(
                        camera = camera,
                        tracker = tracker,
                        stats = stats,
                        modifier = modifier,
                        onStartClick = onStartClick,
                        onStopClick = onStopClick,
                        onMinimizeClick = onMinimizeClick,
                    )
                }
            }
        }
    } else {
        MainScreenMinimized(
            model = tracker,
            modifier = modifier,
        )
    }
}

@Composable
private fun MainScreenActive(
    camera: BlinkCamera.Model,
    tracker: BlinkTracker.Model,
    stats: BlinkStatistic.Model,
    modifier: Modifier = Modifier,
    onStartClick: () -> Unit = {},
    onStopClick: () -> Unit = {},
    onMinimizeClick: () -> Unit = {},
) {

    when (camera.currentPermissionState) {
        PermissionState.DENIED ->
            FullScreenMessageInfo(
                messageRes = R.string.permissions_declined_info,
                iconRes = drawable.ic_selfie,
                modifier = modifier,
            )

        PermissionState.RATIONALE ->
            FullScreenMessageInfo(
                messageRes = R.string.permissions_rationale_info,
                iconRes = drawable.ic_permission,
                modifier = modifier,
            )

        PermissionState.GRANTED ->
            when (camera.cameraState) {
                CameraState.DETECTED -> {
                    Box(modifier = modifier.fillMaxSize()) {
                        Column(modifier = modifier.align(Alignment.TopCenter)) {
                            Card(
                                shape = RoundedCornerShape(size = 16.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                                    contentColor = MaterialTheme.colorScheme.primaryContainer,
                                ),
                                elevation = CardDefaults.cardElevation(
                                    defaultElevation = 8.dp,
                                ),
                                modifier = Modifier
                                    .padding(all = 16.dp)
                                    .fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(all = 16.dp)) {
                                    Text(
                                        text = stringResource(id = R.string.tracking),
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.Medium,
                                        modifier = Modifier.padding(bottom = 4.dp)
                                    )

                                    if (tracker.isTrackingActive) {
                                        Text(
                                            text = stringResource(id = R.string.tracking_active),
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                                            fontWeight = FontWeight.Medium,
                                        )
                                    } else {
                                        Text(
                                            text = stringResource(id = R.string.tracking_not_active),
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.error,
                                            fontWeight = FontWeight.Medium,
                                        )
                                    }

                                    Text(
                                        text = "${stringResource(id = R.string.blinks_last_minute)}: ${tracker.blinksPerLastMinute}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    )

                                    Text(
                                        text = "${stringResource(id = R.string.blinks_total)}: ${tracker.blinksTotal}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    )
                                }
                            }

                            BlinkStatisticContent(
                                state = stats,
                                modifier = modifier
                                    .aspectRatio(ratio = 5f / 4f)
                                    .padding(horizontal = 16.dp),
                            )

                            TrackingControls(
                                model = tracker,
                                modifier = modifier
                                    .padding(all = 16.dp)
                                    .weight(1f),
                                onStartClick = onStartClick,
                                onStopClick = onStopClick,
                                onMinimizeClick = onMinimizeClick,
                            )
                        }
                    }
                }

                CameraState.NOT_DETECTED -> {
                    FullScreenMessageInfo(
                        messageRes = R.string.camera_not_detected,
                        iconRes = drawable.ic_no_camera,
                        modifier = modifier,
                    )
                }

                CameraState.NOT_CHECKED -> {

                }
            }

        else -> {}
    }
}

@Composable
@Preview(showBackground = true)
private fun HomePreviewLight() {
    BlinkTrackerTheme(darkTheme = false) {
        Surface {
            MainScreenActive(
                camera = PreviewStubs.cameraPermissionGranted,
                tracker = PreviewStubs.trackerActiveWithFace,
                stats = PreviewStubs.statsFull,
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun HomePreviewDark() {
    BlinkTrackerTheme(darkTheme = true) {
        Surface {
            MainScreenActive(
                camera = PreviewStubs.cameraPermissionGranted,
                tracker = PreviewStubs.trackerActiveWithFace,
                stats = PreviewStubs.statsFull,
            )
        }
    }
}
