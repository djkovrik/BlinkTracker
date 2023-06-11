package com.sedsoftware.blinktracker.ui.component

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sedsoftware.blinktracker.ui.BlinkRootScreen
import com.sedsoftware.blinktracker.ui.PreviewStubs
import com.sedsoftware.blinktracker.ui.theme.BlinkTrackerTheme

@Composable
fun FullScreenMessageInfo(
    @DrawableRes iconRes: Int,
    @StringRes messageRes: Int,
    modifier: Modifier = Modifier,
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxSize(),
    ) {
        Image(
            painter = painterResource(id = iconRes),
            contentDescription = "Camera icon",
            modifier = modifier
                .size(size = 176.dp)
                .padding(all = 16.dp)
        )

        Text(
            text = stringResource(id = messageRes),
            style = MaterialTheme.typography.displayMedium,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center,
            modifier = modifier.padding(horizontal = 32.dp),
        )
    }
}

@Composable
@Preview(showBackground = true)
fun PreviewPermissionsDeniedLight() {
    BlinkTrackerTheme(darkTheme = false) {
        Surface {
            BlinkRootScreen(
                camera = PreviewStubs.cameraPermissionDenied,
                preferences = PreviewStubs.prefsAllDisabled,
                tracker = PreviewStubs.trackerNotActiveNoFace,
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
                camera = PreviewStubs.cameraPermissionRationale,
                preferences = PreviewStubs.prefsAllDisabled,
                tracker = PreviewStubs.trackerNotActiveNoFace,
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
                camera = PreviewStubs.cameraPermissionGrantedNoCamera,
                preferences = PreviewStubs.prefsAllDisabled,
                tracker = PreviewStubs.trackerNotActiveNoFace,
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
fun PreviewPermissionsDeniedDark() {
    BlinkTrackerTheme(darkTheme = true) {
        Surface {
            BlinkRootScreen(
                camera = PreviewStubs.cameraPermissionDenied,
                preferences = PreviewStubs.prefsAllDisabled,
                tracker = PreviewStubs.trackerNotActiveNoFace,
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
fun PreviewPermissionsRationaleDark() {
    BlinkTrackerTheme(darkTheme = true) {
        Surface {
            BlinkRootScreen(
                camera = PreviewStubs.cameraPermissionRationale,
                preferences = PreviewStubs.prefsAllDisabled,
                tracker = PreviewStubs.trackerNotActiveNoFace,
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
fun PreviewPermissionsNoCameraDark() {
    BlinkTrackerTheme(darkTheme = true) {
        Surface {
            BlinkRootScreen(
                camera = PreviewStubs.cameraPermissionGrantedNoCamera,
                preferences = PreviewStubs.prefsAllDisabled,
                tracker = PreviewStubs.trackerNotActiveNoFace,
            )
        }
    }
}
