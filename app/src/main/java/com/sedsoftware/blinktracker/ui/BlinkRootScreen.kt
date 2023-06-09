package com.sedsoftware.blinktracker.ui

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.sedsoftware.blinktracker.R
import com.sedsoftware.blinktracker.components.camera.BlinkCamera
import com.sedsoftware.blinktracker.components.camera.model.PermissionState
import com.sedsoftware.blinktracker.components.preferences.BlinkPreferences
import com.sedsoftware.blinktracker.components.tracker.BlinkTracker
import com.sedsoftware.blinktracker.ui.component.PermissionsInfo
import com.sedsoftware.blinktracker.ui.theme.BlinkTrackerTheme

@Composable
fun BlinkRootScreen(
    camera: BlinkCamera.Model,
    preferences: BlinkPreferences.Model,
    tracker: BlinkTracker.Model,
    modifier: Modifier = Modifier,
    preview: @Composable () -> Unit = {},
) {

    when (camera.currentPermissionState) {
        PermissionState.DENIED ->
            PermissionsInfo(
                message = stringResource(id = R.string.permissions_declined_info),
                modifier = modifier,
            )

        PermissionState.RATIONALE ->
            PermissionsInfo(
                message = stringResource(id = R.string.permissions_rationale_info),
                modifier = modifier,
            )

        PermissionState.GRANTED ->
            TODO()

        else -> {}
    }
}

@Composable
@Preview(showBackground = true)
fun PreviewPermissionsDeniedLight() {
    BlinkTrackerTheme(darkTheme = false) {
        Surface {
            BlinkRootScreen(
                camera = ComponentStubs.cameraPermissionDenied,
                preferences = ComponentStubs.prefsNotVisibleAllDisabled,
                tracker = ComponentStubs.trackerNotActiveNoFace,
            )
        }
    }
}
