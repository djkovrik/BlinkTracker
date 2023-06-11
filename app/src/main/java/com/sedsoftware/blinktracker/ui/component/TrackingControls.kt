package com.sedsoftware.blinktracker.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CropFree
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sedsoftware.blinktracker.R
import com.sedsoftware.blinktracker.components.tracker.BlinkTracker
import com.sedsoftware.blinktracker.ui.PreviewStubs
import com.sedsoftware.blinktracker.ui.theme.BlinkTrackerTheme

@Composable
fun TrackingControls(
    model: BlinkTracker.Model,
    modifier: Modifier = Modifier,
    onStartClick: () -> Unit = {},
    onStopClick: () -> Unit = {},
    onMinimizeClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
) {
    Row(modifier = modifier.fillMaxWidth()) {
        // Start/Stop Minimize
        Row(modifier = Modifier.weight(1f)) {
            Button(
                onClick = if (model.isTrackingActive) onStopClick else onStartClick,
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                modifier = Modifier.padding(horizontal = 4.dp),
            ) {
                if (model.isTrackingActive) {
                    Icon(
                        imageVector = Icons.Default.Stop,
                        contentDescription = "Stop",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier
                    )
                    Text(
                        text = stringResource(id = R.string.button_stop),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.padding(horizontal = 4.dp),
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Start",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier
                    )
                    Text(
                        text = stringResource(id = R.string.button_start),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.padding(horizontal = 4.dp),
                    )
                }
            }

            OutlinedButton(
                onClick = onMinimizeClick,
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                border = BorderStroke(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier.padding(horizontal = 4.dp),
            ) {
                Icon(
                    imageVector = Icons.Default.CropFree,
                    contentDescription = "Minimize",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                )
                Text(
                    text = stringResource(id = R.string.button_minimize),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(horizontal = 4.dp),
                )
            }
        }

        // Settings
        OutlinedButton(
            onClick = onSettingsClick,
            enabled = !model.isTrackingActive,
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
            border = BorderStroke(
                width = 1.dp,
                color = MaterialTheme.colorScheme.primary
            ),
            modifier = Modifier.padding(horizontal = 4.dp),
        ) {
            Icon(
                imageVector = if (model.isPreferencesPanelVisible) {
                    Icons.Default.KeyboardArrowDown
                } else {
                    Icons.Default.Settings
                },
                contentDescription = "Settings",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
            )
            Text(
                text = stringResource(id = R.string.button_settings),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(horizontal = 4.dp),
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
fun PreviewPreferencesNotActiveLight() {
    BlinkTrackerTheme(darkTheme = false) {
        Surface {
            TrackingControls(
                model = PreviewStubs.trackerNotActiveWithFaceNoPrefs,
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
fun PreviewPreferencesActiveLight() {
    BlinkTrackerTheme(darkTheme = false) {
        Surface {
            TrackingControls(
                model = PreviewStubs.trackerActiveWithFace,
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
fun PreviewPreferencesNotActiveDark() {
    BlinkTrackerTheme(darkTheme = true) {
        Surface {
            TrackingControls(
                model = PreviewStubs.trackerNotActiveWithFaceNoPrefs,
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
fun PreviewPreferencesActiveDark() {
    BlinkTrackerTheme(darkTheme = true) {
        Surface {
            TrackingControls(
                model = PreviewStubs.trackerActiveWithFace,
            )
        }
    }
}
