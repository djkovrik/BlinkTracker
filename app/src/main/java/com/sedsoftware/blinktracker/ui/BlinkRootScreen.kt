package com.sedsoftware.blinktracker.ui

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.sedsoftware.blinktracker.R
import com.sedsoftware.blinktracker.components.camera.BlinkCamera
import com.sedsoftware.blinktracker.components.camera.model.PermissionState
import com.sedsoftware.blinktracker.components.preferences.BlinkPreferences
import com.sedsoftware.blinktracker.components.tracker.BlinkTracker
import com.sedsoftware.blinktracker.ui.component.FullScreenMessageInfo
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
            FullScreenMessageInfo(
                messageRes = R.string.permissions_declined_info,
                iconRes = R.drawable.ic_selfie,
                modifier = modifier,
            )

        PermissionState.RATIONALE ->
            FullScreenMessageInfo(
                messageRes = R.string.permissions_rationale_info,
                iconRes = R.drawable.ic_permission,
                modifier = modifier,
            )

        PermissionState.GRANTED ->
            if (camera.cameraAvailable) {

            } else {
                FullScreenMessageInfo(
                    messageRes = R.string.camera_not_detected,
                    iconRes = R.drawable.ic_no_camera,
                    modifier = modifier,
                )
            }

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

@Composable
@Preview(showBackground = true)
fun PreviewPermissionsRationaleLight() {
    BlinkTrackerTheme(darkTheme = false) {
        Surface {
            BlinkRootScreen(
                camera = ComponentStubs.cameraPermissionRationale,
                preferences = ComponentStubs.prefsNotVisibleAllDisabled,
                tracker = ComponentStubs.trackerNotActiveNoFace,
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
fun PreviewPermissionsNoCameraLight() {
    BlinkTrackerTheme(darkTheme = false) {
        Surface {
            BlinkRootScreen(
                camera = ComponentStubs.cameraPermissionGrantedNoCamera,
                preferences = ComponentStubs.prefsNotVisibleAllDisabled,
                tracker = ComponentStubs.trackerNotActiveNoFace,
            )
        }
    }
}
