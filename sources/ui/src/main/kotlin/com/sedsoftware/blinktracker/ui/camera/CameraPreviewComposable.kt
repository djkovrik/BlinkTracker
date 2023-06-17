package com.sedsoftware.blinktracker.ui.camera

import android.view.ViewGroup
import androidx.camera.core.CameraSelector
import androidx.camera.view.PreviewView
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.lifecycleScope
import com.sedsoftware.blinktracker.ui.camera.core.VisionImageProcessor
import com.sedsoftware.blinktracker.components.camera.model.CameraLens
import com.sedsoftware.blinktracker.components.camera.model.isNotValid
import kotlinx.coroutines.launch

@Composable
fun CameraPreviewComposable(
    imageProcessor: VisionImageProcessor,
    lensFacing: CameraLens,
    modifier: Modifier = Modifier
) {
    if (lensFacing.isNotValid) {
        return
    }

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

internal fun CameraLens.getLensFacing() = when (this) {
    CameraLens.FRONT -> CameraSelector.LENS_FACING_FRONT
    CameraLens.BACK -> CameraSelector.LENS_FACING_BACK
    else -> error("Wrong lens value")
}
