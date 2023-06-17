@file:OptIn(ExperimentalAnimationApi::class)

package com.sedsoftware.blinktracker.ui.component

import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CropFree
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
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
        // Start/Stop
        Row(modifier = Modifier.weight(1f)) {
            Button(
                onClick = if (model.isTrackingActive) onStopClick else onStartClick,
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                modifier = Modifier
                    .defaultMinSize(minWidth = 120.dp)
                    .padding(horizontal = 8.dp)
                ,
            ) {
                if (model.isTrackingActive) {
                    Icon(
                        imageVector = Icons.Default.Stop,
                        contentDescription = "Stop",
                        tint = MaterialTheme.colorScheme.secondaryContainer,
                        modifier = Modifier
                    )
                    Text(
                        text = stringResource(id = R.string.button_stop),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        modifier = Modifier.padding(horizontal = 4.dp),
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Start",
                        tint = MaterialTheme.colorScheme.secondaryContainer,
                        modifier = Modifier
                    )
                    Text(
                        text = stringResource(id = R.string.button_start),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        modifier = Modifier.padding(horizontal = 4.dp),
                    )
                }
            }

            // Minimize
            OutlinedButton(
                onClick = onMinimizeClick,
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                border = BorderStroke(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                ),
                modifier = Modifier.padding(horizontal = 8.dp),
            ) {
                Icon(
                    imageVector = Icons.Default.CropFree,
                    contentDescription = "Minimize",
                    tint = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier
                )
                Text(
                    text = stringResource(id = R.string.button_minimize),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.padding(horizontal = 4.dp),
                )
            }
        }

        // Settings
        OutlinedIconButton(
            onClick = onSettingsClick,
            enabled = !model.isTrackingActive,
            border = BorderStroke(
                width = 1.dp,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            ),
            modifier = Modifier
                .padding(horizontal = 4.dp)
                .alpha(alpha = if (model.isTrackingActive) 0.4f else 1f)
            ,
        ) {
            Crossfade(
                label = "Settings icon",
                targetState = model.isPreferencesPanelVisible,
            ) { panelVisible ->
                if (panelVisible) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Settings",
                        tint = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings",
                        tint = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier
                    )
                }
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
fun PreviewPreferencesNotActiveLight() {
    BlinkTrackerTheme(darkTheme = false) {
        Surface(color = MaterialTheme.colorScheme.secondaryContainer) {
            TrackingControls(
                model = PreviewStubs.trackerNotActiveWithFaceNoPrefs,
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
fun PreviewPreferencesNotActiveOpenedLight() {
    BlinkTrackerTheme(darkTheme = false) {
        Surface(color = MaterialTheme.colorScheme.secondaryContainer) {
            TrackingControls(
                model = PreviewStubs.trackerNotActiveWithFaceAndPrefs,
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
fun PreviewPreferencesActiveLight() {
    BlinkTrackerTheme(darkTheme = false) {
        Surface(color = MaterialTheme.colorScheme.secondaryContainer) {
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
        Surface(color = MaterialTheme.colorScheme.secondaryContainer) {
            TrackingControls(
                model = PreviewStubs.trackerNotActiveWithFaceNoPrefs,
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
fun PreviewPreferencesNotActiveOpenedDark() {
    BlinkTrackerTheme(darkTheme = true) {
        Surface(color = MaterialTheme.colorScheme.secondaryContainer) {
            TrackingControls(
                model = PreviewStubs.trackerNotActiveWithFaceAndPrefs,
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
fun PreviewPreferencesActiveDark() {
    BlinkTrackerTheme(darkTheme = true) {
        Surface(color = MaterialTheme.colorScheme.secondaryContainer) {
            TrackingControls(
                model = PreviewStubs.trackerActiveWithFace,
            )
        }
    }
}
