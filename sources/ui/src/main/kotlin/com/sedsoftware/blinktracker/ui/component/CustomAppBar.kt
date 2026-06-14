package com.sedsoftware.blinktracker.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sedsoftware.blinktracker.components.camera.BlinkCamera
import com.sedsoftware.blinktracker.components.camera.model.CameraState
import com.sedsoftware.blinktracker.components.tracker.BlinkTracker
import com.sedsoftware.blinktracker.ui.R
import com.sedsoftware.blinktracker.ui.extension.withSound
import com.sedsoftware.blinktracker.ui.preview.CameraStub
import com.sedsoftware.blinktracker.ui.preview.PreviewStubs
import com.sedsoftware.blinktracker.ui.theme.BlinkTrackerTheme

@Composable
fun CustomAppBar(
    cameraModel: BlinkCamera.Model,
    trackerModel: BlinkTracker.Model,
    modifier: Modifier = Modifier,
    onHelpIconClick: () -> Unit = {},
    onPreferencesIconClick: () -> Unit = {},
    cameraPreview: @Composable () -> Unit = {},
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .height(height = 88.dp)
            .background(color = MaterialTheme.colorScheme.surface)
    ) {

        when (cameraModel.cameraState) {
            CameraState.DETECTED -> {
                val cameraPreviewDescription = stringResource(id = R.string.cd_camera_preview)

                Box(
                    modifier = Modifier
                        .aspectRatio(1f)
                        .padding(all = 14.dp)
                        .clip(shape = RoundedCornerShape(size = 8.dp))
                        .border(
                            width = 2.dp,
                            color = if (trackerModel.hasFaceDetected) {
                                MaterialTheme.colorScheme.secondary
                            } else {
                                MaterialTheme.colorScheme.error
                            },
                            shape = RoundedCornerShape(size = 8.dp),
                        )
                        .semantics {
                            contentDescription = cameraPreviewDescription
                        }

                ) {
                    cameraPreview()
                }

                Text(
                    text = trackerModel.timerLabel,
                    style = MaterialTheme.typography.displayLarge,
                    color = MaterialTheme.colorScheme.secondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .weight(1f),
                )
                // Prefs
                FilledTonalIconButton(
                    onClick = onPreferencesIconClick.withSound(LocalContext.current),
                    colors = IconButtonDefaults.filledTonalIconButtonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                    ),
                    modifier = Modifier.padding(horizontal = 8.dp),
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = stringResource(id = R.string.cd_settings),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier
                    )
                }
                // Help
                FilledTonalIconButton(
                    onClick = onHelpIconClick.withSound(LocalContext.current),
                    colors = IconButtonDefaults.filledTonalIconButtonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                    ),
                    modifier = Modifier.padding(end = 8.dp),
                ) {
                    Icon(
                        imageVector = Icons.Default.QuestionMark,
                        contentDescription = stringResource(id = R.string.cd_info),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier
                    )
                }
            }

            CameraState.NOT_DETECTED -> {
                Text(
                    text = stringResource(id = R.string.camera_not_detected_short),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Left,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp),
                )
            }

            else -> {}
        }
    }
}

@Composable
@Preview(showBackground = true)
fun AppBarNoCameraLight() {
    BlinkTrackerTheme(darkTheme = false) {
        Surface {
            CustomAppBar(
                cameraModel = PreviewStubs.cameraPermissionGranted.copy(
                    cameraState = CameraState.NOT_DETECTED
                ),
                trackerModel = PreviewStubs.trackerNotActiveWithFaceNoPrefs,
            ) {
                CameraStub()
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
fun AppBarNoCameraDark() {
    BlinkTrackerTheme(darkTheme = false) {
        Surface {
            CustomAppBar(
                cameraModel = PreviewStubs.cameraPermissionGranted.copy(
                    cameraState = CameraState.NOT_DETECTED
                ),
                trackerModel = PreviewStubs.trackerNotActiveWithFaceNoPrefs,
            ) {
                CameraStub()
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
fun AppBarNotActiveLight() {
    BlinkTrackerTheme(darkTheme = false) {
        Surface {
            CustomAppBar(
                cameraModel = PreviewStubs.cameraPermissionGranted,
                trackerModel = PreviewStubs.trackerNotActiveWithFaceNoPrefs,
            ) {
                CameraStub()
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
fun AppBarActiveLight() {
    BlinkTrackerTheme(darkTheme = false) {
        Surface {
            CustomAppBar(
                cameraModel = PreviewStubs.cameraPermissionGranted,
                trackerModel = PreviewStubs.trackerActiveWithFace,
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
fun AppBarNotActiveDark() {
    BlinkTrackerTheme(darkTheme = true) {
        Surface {
            CustomAppBar(
                cameraModel = PreviewStubs.cameraPermissionGranted,
                trackerModel = PreviewStubs.trackerNotActiveWithFaceNoPrefs,
            ) {
                CameraStub()
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
fun AppBarActiveDark() {
    BlinkTrackerTheme(darkTheme = true) {
        Surface {
            CustomAppBar(
                cameraModel = PreviewStubs.cameraPermissionGranted,
                trackerModel = PreviewStubs.trackerActiveWithFace,
            ) {
                CameraStub()
            }
        }
    }
}
