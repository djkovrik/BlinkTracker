package com.sedsoftware.blinktracker.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sedsoftware.blinktracker.components.tracker.BlinkTracker
import com.sedsoftware.blinktracker.ui.Constants
import com.sedsoftware.blinktracker.ui.preview.PreviewStubs
import com.sedsoftware.blinktracker.ui.theme.BlinkTrackerTheme

@Composable
fun MainScreenMinimized(
    model: BlinkTracker.Model,
    modifier: Modifier = Modifier,
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .aspectRatio(ratio = Constants.PIP_RATIO_WIDTH.toFloat() / Constants.PIP_RATIO_HEIGHT.toFloat())
            .background(
                color = if (model.hasFaceDetected) {
                    MaterialTheme.colorScheme.primaryContainer
                } else {
                    MaterialTheme.colorScheme.errorContainer
                }
            )
            .fillMaxSize()
    ) {
        Column {
            Text(
                text = model.timerLabel,
                style = MaterialTheme.typography.displayLarge,
                color = if (model.hasFaceDetected) {
                    MaterialTheme.colorScheme.onPrimaryContainer
                } else {
                    MaterialTheme.colorScheme.onErrorContainer
                },
                textAlign = TextAlign.Center,
                modifier = modifier
                    .padding(horizontal = 32.dp, vertical = 8.dp)
                    .fillMaxWidth(),
            )

            Text(
                text = model.blinksPerLastMinute.toString(),
                style = MaterialTheme.typography.displayMedium,
                color = if (model.hasFaceDetected) {
                    MaterialTheme.colorScheme.onPrimaryContainer
                } else {
                    MaterialTheme.colorScheme.onErrorContainer
                },
                textAlign = TextAlign.Center,
                modifier = modifier
                    .padding(horizontal = 32.dp, vertical = 8.dp)
                    .fillMaxWidth(),
            )
        }
    }
}

@Composable
@Preview(showBackground = true, widthDp = 150, heightDp = 200)
fun BlinkMinimizedPreviewWithFaceLight() {
    BlinkTrackerTheme(darkTheme = false) {
        Surface {
            MainScreenMinimized(
                model = PreviewStubs.trackerActiveWithFace,
            )
        }
    }
}

@Composable
@Preview(showBackground = true, widthDp = 150, heightDp = 200)
fun BlinkMinimizedPreviewWithFaceDark() {
    BlinkTrackerTheme(darkTheme = true) {
        Surface {
            MainScreenMinimized(
                model = PreviewStubs.trackerActiveWithFace,
            )
        }
    }
}

@Composable
@Preview(showBackground = true, widthDp = 150, heightDp = 200)
fun BlinkMinimizedPreviewWithNoFaceLight() {
    BlinkTrackerTheme(darkTheme = false) {
        Surface {
            MainScreenMinimized(
                model = PreviewStubs.trackerActiveWithNoFace,
            )
        }
    }
}

@Composable
@Preview(showBackground = true, widthDp = 150, heightDp = 200)
fun BlinkMinimizedPreviewWithNoFaceDark() {
    BlinkTrackerTheme(darkTheme = true) {
        Surface {
            MainScreenMinimized(
                model = PreviewStubs.trackerActiveWithNoFace,
            )
        }
    }
}
