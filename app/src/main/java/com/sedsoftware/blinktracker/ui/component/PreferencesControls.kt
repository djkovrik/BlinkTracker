package com.sedsoftware.blinktracker.ui.component

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sedsoftware.blinktracker.R
import com.sedsoftware.blinktracker.components.preferences.BlinkPreferences
import com.sedsoftware.blinktracker.ui.PreviewStubs
import com.sedsoftware.blinktracker.ui.theme.BlinkTrackerTheme

@Composable
fun PreferencesControls(
    model: BlinkPreferences.Model,
    modifier: Modifier = Modifier,
    onThresholdChange: (Float) -> Unit = {},
    onLaunchMinimizedChange: (Boolean) -> Unit = {},
    onNotifySoundChange: (Boolean) -> Unit = {},
    onNotifyVibroChange: (Boolean) -> Unit = {},
) {
    Column(modifier = modifier.fillMaxWidth()) {
        PrefsOptionSwitch(
            modifier = modifier,
            isChecked = model.launchMinimized,
            labelRes = R.string.prefs_minimize_on_start,
            onValueChanged = onLaunchMinimizedChange,
        )
        PrefsOptionSwitch(
            modifier = modifier,
            isChecked = model.notifySoundChecked,
            labelRes = R.string.prefs_notify_sound,
            onValueChanged = onNotifySoundChange,
        )
        PrefsOptionSwitch(
            modifier = modifier,
            isChecked = model.notifyVibrationChecked,
            labelRes = R.string.prefs_notify_vibro,
            onValueChanged = onNotifyVibroChange,
        )
        Spacer(modifier = modifier.height(16.dp))
        PrefsOptionSlider(
            modifier = modifier,
            value = model.selectedThreshold,
            labelRes = R.string.prefs_threshold,
            onValueChanged = onThresholdChange,
        )
    }
}

@Composable
private fun PrefsOptionSwitch(
    modifier: Modifier,
    isChecked: Boolean,
    @StringRes labelRes: Int,
    onValueChanged: (Boolean) -> Unit
) {
    Row(
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            text = stringResource(id = labelRes),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .weight(1f)
                .align(Alignment.CenterVertically),
        )

        Switch(
            checked = isChecked,
            onCheckedChange = onValueChanged,
            modifier = Modifier,
            thumbContent = {
                if (isChecked) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Allowed",
                        modifier = Modifier.size(SwitchDefaults.IconSize)
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Denied",
                        modifier = Modifier.size(SwitchDefaults.IconSize)
                    )
                }
            }
        )
    }
}

@Composable
private fun PrefsOptionSlider(
    modifier: Modifier,
    value: Float,
    @StringRes labelRes: Int,
    onValueChanged: (Float) -> Unit
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "${stringResource(id = labelRes)}: ${value.toInt()}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier,
        )

        Slider(
            value = value,
            valueRange = 1f..25f,
            onValueChange = onValueChanged,
        )
    }
}

@Composable
@Preview(showBackground = true)
fun PreviewPreferencesAllDisabledLight() {
    BlinkTrackerTheme(darkTheme = false) {
        Surface {
            PreferencesControls(
                model = PreviewStubs.prefsAllDisabled,
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
fun PreviewPreferencesAllEnabledLight() {
    BlinkTrackerTheme(darkTheme = false) {
        Surface {
            PreferencesControls(
                model = PreviewStubs.prefsMixed,
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
fun PreviewPreferencesAllDisabledDark() {
    BlinkTrackerTheme(darkTheme = true) {
        Surface {
            PreferencesControls(
                model = PreviewStubs.prefsAllDisabled,
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
fun PreviewPreferencesAllEnabledDark() {
    BlinkTrackerTheme(darkTheme = true) {
        Surface {
            PreferencesControls(
                model = PreviewStubs.prefsMixed,
            )
        }
    }
}
