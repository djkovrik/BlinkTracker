package com.sedsoftware.blinktracker.camera

import android.content.Context
import android.util.Size
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.google.mlkit.common.MlKitException
import com.sedsoftware.blinktracker.camera.core.VisionImageProcessor
import timber.log.Timber

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
                imageProcessor.process(imageProxy)
            } catch (e: MlKitException) {
                Timber.e("Failed to process image. Error: ${e.localizedMessage}")
            }
        }

        val cameraProvider = getCameraProvider()
        cameraProvider.unbindAll()
        cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, previewUseCase)
        cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, analysisUseCase)
    } catch (exception: IllegalStateException) {
        Timber.e("CameraX unable to bind preview use case")
    } catch (exception: IllegalArgumentException) {
        Timber.e("CameraX unable to resolve camera")
    } catch (exception: Exception) {
        Timber.e("CameraX unable to bind use cases")
    }
}
