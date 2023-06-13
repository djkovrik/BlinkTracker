package com.sedsoftware.blinktracker.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
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
import com.sedsoftware.blinktracker.R.string
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
    onNotifyVibroChange: (Boolean) -> Unit = {}
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
                    Column(modifier = modifier.align(Alignment.Center)) {
                        Row(modifier = Modifier.height(110.dp)) {
                            Card(
                                shape = RoundedCornerShape(size = 16.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surface,
                                    contentColor = MaterialTheme.colorScheme.surface,
                                ),
                                elevation = CardDefaults.cardElevation(
                                    defaultElevation = 16.dp,
                                ),
                                modifier = Modifier
                                    .padding(start = 16.dp, top = 16.dp, end = 8.dp, bottom = 8.dp)
                                    .fillMaxHeight()
                            ) {
                                Column(modifier = Modifier.padding(all = 16.dp)) {
                                    Text(
                                        text = stringResource(id = string.stats),
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.Medium,
                                    )

                                    Text(
                                        text = "${stringResource(id = string.blinks_last_minute)}: ${tracker.blinksPerLastMinute}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurface,
                                    )

                                    Text(
                                        text = "${stringResource(id = string.blinks_total)}: ${tracker.blinksTotal}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurface,
                                    )
                                }
                            }

                            Card(
                                shape = RoundedCornerShape(size = 16.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surface,
                                    contentColor = MaterialTheme.colorScheme.surface,
                                ),
                                elevation = CardDefaults.cardElevation(
                                    defaultElevation = 16.dp,
                                ),
                                modifier = Modifier
                                    .padding(start = 8.dp, top = 16.dp, end = 16.dp, bottom = 8.dp)
                                    .weight(1f)
                                    .fillMaxHeight()
                            ) {
                                Column(modifier = Modifier.padding(all = 16.dp)) {
                                    Text(
                                        text = stringResource(id = string.tracking),
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.Medium,
                                    )

                                    if (tracker.isTrackingActive) {
                                        Text(
                                            text = stringResource(id = string.active),
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurface,
                                        )
                                    } else {
                                        Text(
                                            text = stringResource(id = string.not_active),
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurface,
                                        )
                                    }
                                }
                            }
                        }

                        AnimatedVisibility(visible = !tracker.isPreferencesPanelVisible) {
                            Card(
                                shape = RoundedCornerShape(size = 16.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                    contentColor = MaterialTheme.colorScheme.surfaceVariant,
                                ),
                                elevation = CardDefaults.cardElevation(
                                    defaultElevation = 16.dp,
                                ),
                                modifier = Modifier.padding(all = 16.dp)
                            ) {
                                Column(modifier = Modifier.padding(all = 16.dp)) {
                                    Text(
                                        text = stringResource(id = R.string.info_title),
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.padding(bottom = 4.dp)
                                    )

                                    Text(
                                        text = stringResource(id = R.string.info),
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                }
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
