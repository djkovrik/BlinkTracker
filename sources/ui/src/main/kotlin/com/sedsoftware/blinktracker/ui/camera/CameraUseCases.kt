package com.sedsoftware.blinktracker.ui.camera

import android.content.Context
import android.util.Size
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.google.mlkit.common.MlKitException
import com.sedsoftware.blinktracker.ui.camera.core.VisionImageProcessor
import kotlinx.coroutines.launch
import timber.log.Timber
import java.nio.BufferUnderflowException

@Suppress("MagicNumber")
suspend fun Context.bindCameraUseCases(
    previewView: PreviewView,
    cameraSelector: CameraSelector,
    imageProcessor: VisionImageProcessor,
    lifecycleOwner: LifecycleOwner,
) {
    try {
        val previewUseCase = Preview.Builder()
            .setTargetName("Preview")
            .build()
            .also { it.setSurfaceProvider(previewView.surfaceProvider) }

        val analysisUseCase = ImageAnalysis.Builder()
            .setTargetResolution(Size(640, 480))
            .build()

        analysisUseCase.setAnalyzer(ContextCompat.getMainExecutor(this)) { imageProxy: ImageProxy ->
            try {
                lifecycleOwner.lifecycleScope.launch {
                    imageProcessor.process(imageProxy)
                }
            } catch (e: MlKitException) {
                Timber.e("Failed to process image. Error: ${e.localizedMessage}")
            } catch (e: BufferUnderflowException) {
                Timber.e("Failed to process image. Error: ${e.localizedMessage}")
            }
        }

        val cameraProvider = getCameraProvider()
        cameraProvider.unbindAll()
        cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, previewUseCase)
        cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, analysisUseCase)
    } catch (exception: IllegalStateException) {
        Timber.e("CameraX unable to bind preview use case", exception)
    } catch (exception: IllegalArgumentException) {
        Timber.e("CameraX unable to resolve camera", exception)
    }
}
