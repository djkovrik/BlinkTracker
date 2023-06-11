package com.sedsoftware.blinktracker.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.sedsoftware.blinktracker.components.preferences.BlinkPreferences
import com.sedsoftware.blinktracker.components.tracker.BlinkTracker

@Composable
fun BottomControlPanel(
    prefsModel: BlinkPreferences.Model,
    trackerModel: BlinkTracker.Model,
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

}
