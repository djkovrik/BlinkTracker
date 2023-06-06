package com.sedsoftware.blinktracker.ui.compose

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sedsoftware.blinktracker.camera.CameraPreviewComposable
import com.sedsoftware.blinktracker.camera.core.VisionImageProcessor
import com.sedsoftware.blinktracker.camera.getLensFacing
import com.sedsoftware.blinktracker.components.camera.BlinkCamera
import com.sedsoftware.blinktracker.components.preferences.BlinkPreferences
import com.sedsoftware.blinktracker.components.tracker.BlinkTracker
import com.sedsoftware.blinktracker.root.BlinkRoot

@Composable
fun BlinkRootContent(
    root: BlinkRoot,
    processor: VisionImageProcessor,
    modifier: Modifier = Modifier,
) {
    Surface(modifier = modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        val camera: BlinkCamera = root.cameraComponent
        val preferences: BlinkPreferences = root.preferencesComponent
        val tracker: BlinkTracker = root.trackerComponent

        val cameraState by camera.models.collectAsState(initial = camera.initial)
        val preferencesState by preferences.models.collectAsState(initial = preferences.initial)
        val trackerState by tracker.models.collectAsState(initial = tracker.initial)

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
        ) {
            if (cameraState.cameraAvailable) {
                Card(
                    shape = RoundedCornerShape(size = 16.dp),
                    modifier = modifier
                        .padding(all = 64.dp)
                        .aspectRatio(ratio = 1.0f)
                ) {
                    CameraPreviewComposable(
                        imageProcessor = processor,
                        lensFacing = cameraState.selectedLens.getLensFacing(),
                        modifier = modifier.fillMaxSize(),
                    )
                }
            } else {
                Text(text = "Not available")
            }
        }
    }
}
