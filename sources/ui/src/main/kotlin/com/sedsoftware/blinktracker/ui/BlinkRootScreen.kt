package com.sedsoftware.blinktracker.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sedsoftware.blinktracker.components.camera.BlinkCamera
import com.sedsoftware.blinktracker.components.camera.model.CameraState
import com.sedsoftware.blinktracker.components.camera.model.PermissionState
import com.sedsoftware.blinktracker.components.preferences.BlinkPreferences
import com.sedsoftware.blinktracker.components.statistic.BlinkStatistic
import com.sedsoftware.blinktracker.components.tracker.BlinkTracker
import com.sedsoftware.blinktracker.ui.R.drawable
import com.sedsoftware.blinktracker.ui.component.BottomControlPanel
import com.sedsoftware.blinktracker.ui.component.FullScreenMessageInfo
import com.sedsoftware.blinktracker.ui.component.StatsPanel
import com.sedsoftware.blinktracker.ui.theme.BlinkTrackerTheme

@Composable
fun BlinkRootScreen(
    camera: BlinkCamera.Model,
    tracker: BlinkTracker.Model,
    preferences: BlinkPreferences.Model,
    stats: BlinkStatistic.Model,
    modifier: Modifier = Modifier,
    onStartClick: () -> Unit = {},
    onStopClick: () -> Unit = {},
    onMinimizeClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    onThresholdChange: (Float) -> Unit = {},
    onLaunchMinimizedChange: (Boolean) -> Unit = {},
    onNotifySoundChange: (Boolean) -> Unit = {},
    onNotifyVibroChange: (Boolean) -> Unit = {},
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
                    BaseContentScreen(
                        modifier = modifier,
                        tracker = tracker,
                        preferences = preferences,
                        stats = stats,
                        onStartClick = onStartClick,
                        onStopClick = onStopClick,
                        onMinimizeClick = onMinimizeClick,
                        onSettingsClick = onSettingsClick,
                        onThresholdChange = onThresholdChange,
                        onLaunchMinimizedChange = onLaunchMinimizedChange,
                        onNotifySoundChange = onNotifySoundChange,
                        onNotifyVibroChange = onNotifyVibroChange
                    )
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
private fun BaseContentScreen(
    modifier: Modifier,
    tracker: BlinkTracker.Model,
    preferences: BlinkPreferences.Model,
    stats: BlinkStatistic.Model,
    onStartClick: () -> Unit,
    onStopClick: () -> Unit,
    onMinimizeClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onThresholdChange: (Float) -> Unit,
    onLaunchMinimizedChange: (Boolean) -> Unit,
    onNotifySoundChange: (Boolean) -> Unit,
    onNotifyVibroChange: (Boolean) -> Unit
) {
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

            AnimatedVisibility(visible = !tracker.isPreferencesPanelVisible) {
                Card(
                    shape = RoundedCornerShape(size = 16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.secondaryContainer,
                    ),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 8.dp,
                    ),
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth()
                ) {
                    StatsPanel(
                        model = stats,
                        modifier = modifier.aspectRatio(ratio = 4f / 3f),
                    )
                }
            }
        }

        BottomControlPanel(
            trackerModel = tracker,
            prefsModel = preferences,
            modifier = modifier.align(Alignment.BottomCenter),
            onStartClick = onStartClick,
            onStopClick = onStopClick,
            onMinimizeClick = onMinimizeClick,
            onSettingsClick = onSettingsClick,
            onThresholdChange = onThresholdChange,
            onLaunchMinimizedChange = onLaunchMinimizedChange,
            onNotifySoundChange = onNotifySoundChange,
            onNotifyVibroChange = onNotifyVibroChange,
        )
    }
}

@Composable
@Preview(showBackground = true)
fun BlinkRootScreenPreviewNotActiveNoFaceNoPrefs() {
    BlinkTrackerTheme(darkTheme = false) {
        Surface {
            BlinkRootScreen(
                camera = PreviewStubs.cameraPermissionGranted,
                preferences = PreviewStubs.prefsMixed,
                tracker = PreviewStubs.trackerNotActiveNoFaceNoPrefs,
                stats = PreviewStubs.statsFull,
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
fun BlinkRootScreenPreviewNotActiveWithFaceNoPrefs() {
    BlinkTrackerTheme(darkTheme = false) {
        Surface {
            BlinkRootScreen(
                camera = PreviewStubs.cameraPermissionGranted,
                preferences = PreviewStubs.prefsMixed,
                tracker = PreviewStubs.trackerNotActiveWithFaceNoPrefs,
                stats = PreviewStubs.statsFull,
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
fun BlinkRootScreenPreviewNotActiveWithFaceWithPrefs() {
    BlinkTrackerTheme(darkTheme = false) {
        Surface {
            BlinkRootScreen(
                camera = PreviewStubs.cameraPermissionGranted,
                preferences = PreviewStubs.prefsMixed,
                tracker = PreviewStubs.trackerNotActiveWithFaceAndPrefs,
                stats = PreviewStubs.statsFull,
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
fun BlinkRootScreenPreviewActive() {
    BlinkTrackerTheme(darkTheme = false) {
        Surface {
            BlinkRootScreen(
                camera = PreviewStubs.cameraPermissionGranted,
                preferences = PreviewStubs.prefsMixed,
                tracker = PreviewStubs.trackerActiveWithFace,
                stats = PreviewStubs.statsFull,
            )
        }
    }
}


@Composable
@Preview(showBackground = true)
fun BlinkRootScreenPreviewNotActiveNoFaceNoPrefsDark() {
    BlinkTrackerTheme(darkTheme = true) {
        Surface {
            BlinkRootScreen(
                camera = PreviewStubs.cameraPermissionGranted,
                preferences = PreviewStubs.prefsMixed,
                tracker = PreviewStubs.trackerNotActiveNoFaceNoPrefs,
                stats = PreviewStubs.statsFull,
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
fun BlinkRootScreenPreviewNotActiveWithFaceNoPrefsDark() {
    BlinkTrackerTheme(darkTheme = true) {
        Surface {
            BlinkRootScreen(
                camera = PreviewStubs.cameraPermissionGranted,
                preferences = PreviewStubs.prefsMixed,
                tracker = PreviewStubs.trackerNotActiveWithFaceNoPrefs,
                stats = PreviewStubs.statsFull,
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
fun BlinkRootScreenPreviewNotActiveWithFaceWithPrefsDark() {
    BlinkTrackerTheme(darkTheme = true) {
        Surface {
            BlinkRootScreen(
                camera = PreviewStubs.cameraPermissionGranted,
                preferences = PreviewStubs.prefsMixed,
                tracker = PreviewStubs.trackerNotActiveWithFaceAndPrefs,
                stats = PreviewStubs.statsFull,
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
fun BlinkRootScreenPreviewActiveDark() {
    BlinkTrackerTheme(darkTheme = true) {
        Surface {
            BlinkRootScreen(
                camera = PreviewStubs.cameraPermissionGranted,
                preferences = PreviewStubs.prefsMixed,
                tracker = PreviewStubs.trackerActiveWithFace,
                stats = PreviewStubs.statsFull,
            )
        }
    }
}
