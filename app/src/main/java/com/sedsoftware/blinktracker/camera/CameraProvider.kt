package com.sedsoftware.blinktracker.camera

import android.content.Context
import android.hardware.camera2.CameraAccessException
import androidx.camera.core.CameraUnavailableException
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import timber.log.Timber
import java.util.concurrent.ExecutionException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

suspend fun Context.getCameraProvider(): ProcessCameraProvider = suspendCoroutine { continuation ->
    ProcessCameraProvider.getInstance(this).also { future ->
        try {
            future.addListener({ continuation.resume(future.get()) }, ContextCompat.getMainExecutor(this))
        } catch (exception: ExecutionException) {
            Timber.e("Camera provider future is not available: ${exception.message}")
            continuation.resumeWithException(exception)
        } catch (exception: CameraAccessException) {
            Timber.e("Failed to access camera: ${exception.message}")
            continuation.resumeWithException(exception)
        } catch (exception: CameraUnavailableException) {
            Timber.e("Failed to open camera: ${exception.message}")
            continuation.resumeWithException(exception)
        } catch (exception: Exception) {
            Timber.e("Camera provider future is not available: ${exception.message}")
            continuation.resumeWithException(exception)
        }
    }
}
