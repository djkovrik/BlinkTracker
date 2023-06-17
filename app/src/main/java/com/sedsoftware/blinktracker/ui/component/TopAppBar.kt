package com.sedsoftware.blinktracker.ui.component

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sedsoftware.blinktracker.R
import com.sedsoftware.blinktracker.components.camera.BlinkCamera
import com.sedsoftware.blinktracker.components.tracker.BlinkTracker
import com.sedsoftware.blinktracker.ui.CameraStub
import com.sedsoftware.blinktracker.ui.PreviewStubs
import com.sedsoftware.blinktracker.ui.theme.BlinkTrackerTheme

@Composable
fun TopAppBar(
    cameraModel: BlinkCamera.Model,
    trackerModel: BlinkTracker.Model,
    modifier: Modifier = Modifier,
    onHelpIconClick: () -> Unit = {},
    cameraPreview: @Composable () -> Unit = {},
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .height(96.dp)
    ) {
        if (cameraModel.cameraAvailable) {
            Box(
                modifier = Modifier
                    .aspectRatio(1f)
                    .padding(all = 16.dp)
                    .clip(shape = RoundedCornerShape(size = 8.dp))
                    .border(
                        width = 2.dp,
                        color = if (trackerModel.hasFaceDetected) {
                            MaterialTheme.colorScheme.tertiary
                        } else {
                            MaterialTheme.colorScheme.error
                        },
                        shape = RoundedCornerShape(size = 8.dp),
                    )

            ) {
                cameraPreview()
            }

            Text(
                text = trackerModel.timerLabel,
                style = MaterialTheme.typography.displayMedium,
                color = MaterialTheme.colorScheme.tertiary,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .weight(1f),
            )

            OutlinedIconButton(
                onClick = onHelpIconClick,
                border = BorderStroke(
                    width = 0.dp,
                    color = Color.Transparent
                ),
                colors = IconButtonDefaults.outlinedIconButtonColors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                ),
                modifier = Modifier.padding(horizontal = 8.dp),
            ) {
                Icon(
                    imageVector = Icons.Default.QuestionMark,
                    contentDescription = "Info",
                    tint = MaterialTheme.colorScheme.onTertiaryContainer,
                    modifier = Modifier
                )
            }
        } else {
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
    }
}

@Composable
@Preview(showBackground = true)
fun AppBarNoCameraLight() {
    BlinkTrackerTheme(darkTheme = false) {
        Surface {
            TopAppBar(
                cameraModel = PreviewStubs.cameraPermissionGranted.copy(
                    cameraAvailable = false
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
            TopAppBar(
                cameraModel = PreviewStubs.cameraPermissionGranted.copy(
                    cameraAvailable = false
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
            TopAppBar(
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
            TopAppBar(
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
            TopAppBar(
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
            TopAppBar(
                cameraModel = PreviewStubs.cameraPermissionGranted,
                trackerModel = PreviewStubs.trackerActiveWithFace,
            ) {
                CameraStub()
            }
        }
    }
}