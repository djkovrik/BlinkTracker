package com.sedsoftware.blinktracker.ui.component

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.sedsoftware.blinktracker.components.preferences.BlinkPreferences
import com.sedsoftware.blinktracker.components.tracker.BlinkTracker
import com.sedsoftware.blinktracker.ui.PreviewStubs
import com.sedsoftware.blinktracker.ui.theme.BlinkTrackerTheme

@Composable
fun BottomControlPanel(
    trackerModel: BlinkTracker.Model,
    prefsModel: BlinkPreferences.Model,
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

    val offsetAnimation: Dp by animateDpAsState(
        targetValue = if (trackerModel.isPreferencesPanelVisible) 0.dp else 230.dp,
        label = "offsetAnimation",
    )

    Card(
        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
            contentColor = MaterialTheme.colorScheme.tertiaryContainer,
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 16.dp,
        ),
        modifier = modifier
            .fillMaxWidth()
            .absoluteOffset(y = offsetAnimation),
    ) {
        TrackingControls(
            model = trackerModel,
            modifier = modifier.padding(all = 8.dp),
            onStartClick = onStartClick,
            onStopClick = onStopClick,
            onMinimizeClick = onMinimizeClick,
            onSettingsClick = onSettingsClick,
        )

        PreferencesControls(
            model = prefsModel,
            modifier = modifier.padding(horizontal = 8.dp),
            onThresholdChange = onThresholdChange,
            onLaunchMinimizedChange = onLaunchMinimizedChange,
            onNotifySoundChange = onNotifySoundChange,
            onNotifyVibroChange = onNotifyVibroChange,
        )
    }
}

@Composable
@Preview(showBackground = true)
fun PreviewBottomPanelCollapsedLight() {
    BlinkTrackerTheme(darkTheme = false) {
        Surface {
            BottomControlPanel(
                trackerModel = PreviewStubs.trackerNotActiveWithFaceNoPrefs,
                prefsModel = PreviewStubs.prefsMixed,
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
fun PreviewBottomPanelExpandedLight() {
    BlinkTrackerTheme(darkTheme = false) {
        Surface {
            BottomControlPanel(
                trackerModel = PreviewStubs.trackerNotActiveWithFaceAndPrefs,
                prefsModel = PreviewStubs.prefsMixed,
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
fun PreviewBottomPanelCollapsedDark() {
    BlinkTrackerTheme(darkTheme = true) {
        Surface {
            BottomControlPanel(
                trackerModel = PreviewStubs.trackerNotActiveWithFaceNoPrefs,
                prefsModel = PreviewStubs.prefsMixed,
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
fun PreviewBottomPanelExpandedDark() {
    BlinkTrackerTheme(darkTheme = true) {
        Surface {
            BottomControlPanel(
                trackerModel = PreviewStubs.trackerNotActiveWithFaceAndPrefs,
                prefsModel = PreviewStubs.prefsMixed,
            )
        }
    }
}
