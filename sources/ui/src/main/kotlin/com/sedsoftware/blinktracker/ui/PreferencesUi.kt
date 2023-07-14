@file:OptIn(ExperimentalMaterial3Api::class)

package com.sedsoftware.blinktracker.ui

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RichTooltipBox
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberRichTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sedsoftware.blinktracker.components.preferences.BlinkPreferences
import com.sedsoftware.blinktracker.components.preferences.model.PermissionStateNotification
import com.sedsoftware.blinktracker.ui.R.string
import com.sedsoftware.blinktracker.ui.preview.PreviewStubs
import com.sedsoftware.blinktracker.ui.theme.BlinkTrackerTheme
import kotlinx.coroutines.launch

@Composable
fun BlinkPreferencesContent(
    component: BlinkPreferences,
    modifier: Modifier = Modifier,
    onBackClicked: () -> Unit = {},
    onNotificationPermission: () -> Unit = {},
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
        onReplacePipChange = component::onReplacePipChanged,
        onNotificationPermission = onNotificationPermission,
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
    onReplacePipChange: (Boolean) -> Unit = {},
    onNotificationPermission: () -> Unit = {},
) {
    val permissionLauncherKey = model.replacePip && model.permissionState != PermissionStateNotification.GRANTED
    val snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
    val rationaleMessage = stringResource(id = string.prefs_replace_pip_rationale)
    LaunchedEffect(model.showRationale) {
        if (model.showRationale) {
            snackbarHostState.showSnackbar(message = rationaleMessage)
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
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
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier
                        )
                    }
                }
            )
        }
    ) { paddingValues: PaddingValues ->
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
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
            PrefsOptionSwitch(
                modifier = modifier,
                isChecked = model.replacePip,
                labelRes = R.string.prefs_replace_pip,
                descriptionRes = R.string.prefs_replace_pip_description,
                onValueChanged = onReplacePipChange,
            )
            Spacer(modifier = modifier.height(16.dp))
            PrefsOptionSlider(
                modifier = modifier,
                value = model.selectedThreshold,
                labelRes = R.string.prefs_threshold,
                onValueChanged = onThresholdChange,
            )

            LaunchedEffect(key1 = permissionLauncherKey) {
                if (permissionLauncherKey) {
                    onNotificationPermission.invoke()
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
    @StringRes descriptionRes: Int? = null,
    onValueChanged: (Boolean) -> Unit = {},
) {
    val tooltipState = rememberRichTooltipState(
        isPersistent = true
    )
    val scope = rememberCoroutineScope()

    Row(
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            text = stringResource(id = labelRes),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.align(Alignment.CenterVertically),
        )

        if (descriptionRes != null) {
            RichTooltipBox(
                shape = RoundedCornerShape(size = 16.dp),
                colors = TooltipDefaults.richTooltipColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondary,
                ),
                text = {
                    Text(
                        text = stringResource(id = descriptionRes),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.padding(all = 4.dp)
                    )
                },
                tooltipState = tooltipState,
            ) {
                IconButton(onClick = { scope.launch { tooltipState.show() } }) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Info",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

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
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier,
        )

        Slider(
            value = value,
            valueRange = 5f..30f,
            onValueChange = onValueChanged,
            modifier = Modifier.padding(horizontal = 8.dp)
        )
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
