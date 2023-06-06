package com.sedsoftware.blinktracker.camera

import android.view.ViewGroup
import androidx.camera.core.CameraSelector
import androidx.camera.view.PreviewView
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.lifecycleScope
import com.sedsoftware.blinktracker.camera.core.VisionImageProcessor
import kotlinx.coroutines.launch

@Composable
fun CameraPreviewComposable(
    imageProcessor: VisionImageProcessor,
    lensFacing: Int,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val previewView = PreviewView(context).apply {
        scaleType = PreviewView.ScaleType.FILL_CENTER
        layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT,
        )
        implementationMode = PreviewView.ImplementationMode.COMPATIBLE
    }

    val cameraSelector: CameraSelector = CameraSelector.Builder()
        .requireLensFacing(lensFacing.getLensFacing())
        .build()

    AndroidView(
        modifier = modifier,
        factory = { ctx ->
            lifecycleOwner.lifecycleScope.launch {
                ctx.bindCameraUseCases(
                    previewView = previewView,
                    cameraSelector = cameraSelector,
                    imageProcessor = imageProcessor,
                    lifecycleOwner = lifecycleOwner,
                )
            }

            previewView
        })
}

internal fun Int.getLensFacing() = when (this) {
    0 -> CameraSelector.LENS_FACING_FRONT
    1 -> CameraSelector.LENS_FACING_BACK
    else -> error("Wrong lens value")
}
