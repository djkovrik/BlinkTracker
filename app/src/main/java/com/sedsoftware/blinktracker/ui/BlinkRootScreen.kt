package com.sedsoftware.blinktracker.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import com.sedsoftware.blinktracker.R
import com.sedsoftware.blinktracker.components.camera.BlinkCamera
import com.sedsoftware.blinktracker.components.camera.model.PermissionState
import com.sedsoftware.blinktracker.components.preferences.BlinkPreferences
import com.sedsoftware.blinktracker.components.tracker.BlinkTracker
import com.sedsoftware.blinktracker.ui.component.BottomControlPanel
import com.sedsoftware.blinktracker.ui.component.FullScreenMessageInfo
import com.sedsoftware.blinktracker.ui.theme.BlinkTrackerTheme

@Composable
fun BlinkRootScreen(
    camera: BlinkCamera.Model,
    preferences: BlinkPreferences.Model,
    tracker: BlinkTracker.Model,
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
                iconRes = R.drawable.ic_selfie,
                modifier = modifier,
            )

        PermissionState.RATIONALE ->
            FullScreenMessageInfo(
                messageRes = R.string.permissions_rationale_info,
                iconRes = R.drawable.ic_permission,
                modifier = modifier,
            )

        PermissionState.GRANTED ->
            if (camera.cameraAvailable) {
                Box(modifier = modifier.fillMaxSize()) {
                    Column(modifier = modifier.align(Alignment.TopCenter)) {
                        Card(
                            shape = RoundedCornerShape(size = 16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                contentColor = MaterialTheme.colorScheme.primaryContainer,
                            ),
                            elevation = CardDefaults.cardElevation(
                                defaultElevation = 16.dp,
                            ),
                            modifier = Modifier
                                .padding(horizontal = 16.dp, vertical = 8.dp)
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
            } else {
                FullScreenMessageInfo(
                    messageRes = R.string.camera_not_detected,
                    iconRes = R.drawable.ic_no_camera,
                    modifier = modifier,
                )
            }

        else -> {}
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
            )
        }
    }
}
