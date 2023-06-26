package com.sedsoftware.blinktracker.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CropFree
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.sedsoftware.blinktracker.components.tracker.BlinkTracker
import com.sedsoftware.blinktracker.ui.R
import com.sedsoftware.blinktracker.ui.extension.withSound
import com.sedsoftware.blinktracker.ui.preview.PreviewStubs
import com.sedsoftware.blinktracker.ui.theme.BlinkTrackerTheme

@Composable
fun TrackingControls(
    model: BlinkTracker.Model,
    modifier: Modifier = Modifier,
    onStartClick: () -> Unit = {},
    onStopClick: () -> Unit = {},
    onMinimizeClick: () -> Unit = {},
) {

    val minButtonWidth: Dp = 130.dp

    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.Bottom,
        modifier = modifier.fillMaxWidth()
    ) {
        Button(
            onClick = if (model.isTrackingActive) {
                onStopClick.withSound(LocalContext.current)
            } else {
                onStartClick.withSound(LocalContext.current)
            },
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .defaultMinSize(minWidth = minButtonWidth),
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
            onClick = onMinimizeClick.withSound(LocalContext.current),
            enabled = model.isTrackingActive,
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
            border = BorderStroke(
                width = 1.dp,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            ),
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .defaultMinSize(minWidth = minButtonWidth)
                .alpha(alpha = if (model.isTrackingActive) 1.0f else 0.4f)
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
