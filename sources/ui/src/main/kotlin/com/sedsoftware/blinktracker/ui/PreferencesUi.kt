@file:OptIn(ExperimentalMaterial3Api::class)

package com.sedsoftware.blinktracker.ui

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sedsoftware.blinktracker.components.preferences.BlinkPreferences
import com.sedsoftware.blinktracker.components.tracker.BlinkTracker
import com.sedsoftware.blinktracker.ui.component.AutostartRationale
import com.sedsoftware.blinktracker.ui.component.MainScreenMinimized
import com.sedsoftware.blinktracker.ui.preview.PreviewStubs
import com.sedsoftware.blinktracker.ui.theme.BlinkTrackerTheme

@Composable
fun BlinkPreferencesContent(
    component: BlinkPreferences,
    modifier: Modifier = Modifier,
    onBackClicked: () -> Unit = {},
) {
    val state: BlinkPreferences.Model by component.models.collectAsState(initial = component.initial)

    BlinkPreferencesScreen(
        model = state,
        modifier = modifier,
        onBackClicked = onBackClicked,
        onThresholdChange = component::onMinimalThresholdChanged,
        onLaunchMinimizedChange = component::onLaunchMinimizedChanged,
        onNotifySoundChange = component::onNotifySoundChanged,
        onNotifyVibroChange = component::onNotifyVibrationChanged,
        onMinimizedOpacityChange = component::onMinimizedOpacityChanged,
        onAutoStartChange = component::onAutoStartChanged,
        onDisplayOverlayAgree = component::onOverlaySettingsRequested,
        onDisplayOverlayDisagree = component::onOverlaySettingsCanceled,
    )
}

@Composable
private fun BlinkPreferencesScreen(
    model: BlinkPreferences.Model,
    modifier: Modifier = Modifier,
    onBackClicked: () -> Unit = {},
    onThresholdChange: (Float) -> Unit = {},
    onLaunchMinimizedChange: (Boolean) -> Unit = {},
    onNotifySoundChange: (Boolean) -> Unit = {},
    onNotifyVibroChange: (Boolean) -> Unit = {},
    onMinimizedOpacityChange: (Float) -> Unit = {},
    onAutoStartChange: (Boolean) -> Unit = {},
    onDisplayOverlayAgree: () -> Unit = {},
    onDisplayOverlayDisagree: () -> Unit = {},
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.settings),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBackClicked,
                        modifier = Modifier,
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(id = R.string.cd_back),
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier
                        )
                    }
                }
            )
        }
    ) { paddingValues: PaddingValues ->
        LazyColumn(
            contentPadding = PaddingValues(top = 8.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            item {
                PrefsOptionSwitch(
                    modifier = modifier,
                    isChecked = model.launchMinimized,
                    labelRes = R.string.prefs_minimize_on_start,
                    onValueChanged = onLaunchMinimizedChange,
                )
            }
            item {
                PrefsOptionSwitch(
                    modifier = modifier,
                    isChecked = model.notifySoundChecked,
                    labelRes = R.string.prefs_notify_sound,
                    onValueChanged = onNotifySoundChange,
                )
            }
            item {
                PrefsOptionSwitch(
                    modifier = modifier,
                    isChecked = model.autoStartChecked,
                    labelRes = R.string.prefs_auto_start,
                    onValueChanged = onAutoStartChange,
                )
            }

            if (model.rationaleDisplayed) {
                item {
                    AutostartRationale(
                        onAgree = onDisplayOverlayAgree,
                        onDisagree = onDisplayOverlayDisagree,
                    )
                }
            }

            item {
                PrefsOptionSwitch(
                    modifier = modifier,
                    isChecked = model.notifyVibrationChecked,
                    labelRes = R.string.prefs_notify_vibro,
                    onValueChanged = onNotifyVibroChange,
                )
            }
            item {
                PrefsOptionSlider(
                    modifier = modifier,
                    value = model.selectedThreshold,
                    valueRange = 5f..30f,
                    steps = 0,
                    labelRes = R.string.prefs_threshold,
                    onValueChanged = onThresholdChange,
                )
            }
            item {
                PrefsOptionSlider(
                    modifier = modifier,
                    value = model.minimizedOpacityPercent,
                    valueRange = 10f..100f,
                    steps = 17,
                    labelRes = R.string.prefs_minimized_opacity,
                    onValueChanged = onMinimizedOpacityChange,
                )

                Box(
                    modifier = Modifier
                        .padding(top = 12.dp)
                        .size(width = 120.dp, height = 180.dp)
                ) {
                    MainScreenMinimized(
                        model = BlinkTracker.Model(
                            isTrackingActive = true,
                            timerLabel = "12:34",
                            blinksPerLastMinute = 16,
                            blinksTotal = 16,
                            isMinimized = true,
                            hasFaceDetected = true,
                        ),
                        modifier = Modifier
                            .clip(shape = RoundedCornerShape(size = 8.dp))
                            .alpha(alpha = model.minimizedOpacityPercent / 100f)
                    )
                }
            }
        }
    }
}

@Composable
private fun PrefsOptionSwitch(
    modifier: Modifier,
    isChecked: Boolean,
    @StringRes labelRes: Int,
    onValueChanged: (Boolean) -> Unit,
) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant,
        contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        shape = MaterialTheme.shapes.medium,
        modifier = modifier.fillMaxWidth(),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
        ) {
            Text(
                text = stringResource(id = labelRes),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f),
            )

            Switch(
                checked = isChecked,
                onCheckedChange = onValueChanged,
                modifier = Modifier,
                thumbContent = {
                    if (isChecked) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = stringResource(id = R.string.cd_enabled),
                            modifier = Modifier.size(SwitchDefaults.IconSize)
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = stringResource(id = R.string.cd_disabled),
                            modifier = Modifier.size(SwitchDefaults.IconSize)
                        )
                    }
                }
            )
        }
    }
}

@Composable
private fun PrefsOptionSlider(
    modifier: Modifier,
    value: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    steps: Int,
    @StringRes labelRes: Int,
    onValueChanged: (Float) -> Unit,
) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant,
        contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        shape = MaterialTheme.shapes.medium,
        modifier = modifier.fillMaxWidth(),
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp)) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = stringResource(id = labelRes),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f),
                )

                Text(
                    text = value.toInt().toString(),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary,
                )
            }

            Slider(
                value = value,
                valueRange = valueRange,
                steps = steps,
                onValueChange = onValueChanged,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun PreviewPreferencesAllDisabledLight() {
    BlinkTrackerTheme(darkTheme = false) {
        Surface {
            BlinkPreferencesScreen(
                model = PreviewStubs.prefsAllDisabled,
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun PreviewPreferencesAllEnabledLight() {
    BlinkTrackerTheme(darkTheme = false) {
        Surface {
            BlinkPreferencesScreen(
                model = PreviewStubs.prefsMixed,
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun PreviewPreferencesRationaleLight() {
    BlinkTrackerTheme(darkTheme = false) {
        Surface {
            BlinkPreferencesScreen(
                model = PreviewStubs.prefsRationale,
            )
        }
    }
}


@Composable
@Preview(showBackground = true)
private fun PreviewPreferencesAllDisabledDark() {
    BlinkTrackerTheme(darkTheme = true) {
        Surface {
            BlinkPreferencesScreen(
                model = PreviewStubs.prefsAllDisabled,
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun PreviewPreferencesAllEnabledDark() {
    BlinkTrackerTheme(darkTheme = true) {
        Surface {
            BlinkPreferencesScreen(
                model = PreviewStubs.prefsMixed,
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun PreviewPreferencesRationaleDark() {
    BlinkTrackerTheme(darkTheme = true) {
        Surface {
            BlinkPreferencesScreen(
                model = PreviewStubs.prefsRationale,
            )
        }
    }
}
