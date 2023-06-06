package com.sedsoftware.blinktracker.camera.core

import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageProxy
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.TaskExecutors
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetector
import com.google.mlkit.vision.face.FaceDetectorOptions
import timber.log.Timber

class FaceDetectorProcessor(detectorOptions: FaceDetectorOptions) : VisionImageProcessor {
    private val detector: FaceDetector = FaceDetection.getClient(detectorOptions)
    private val executor: ScopedExecutor = ScopedExecutor(TaskExecutors.MAIN_THREAD)
    private var isShutdown: Boolean = false

    @ExperimentalGetImage
    override fun process(image: ImageProxy) {
        if (isShutdown) {
            return
        }
        requestDetectInImage(InputImage.fromMediaImage(image.image!!, image.imageInfo.rotationDegrees))
            .addOnCompleteListener { image.close() }
    }

    override fun stop() {
        executor.shutdown()
        isShutdown = true
        detector.close()
    }

    private fun requestDetectInImage(image: InputImage): Task<List<Face>> =
        setUpListener(detector.process(image))

    private fun setUpListener(task: Task<List<Face>>): Task<List<Face>> =
        task
            .addOnSuccessListener(executor) { results: List<Face> ->
                for (face in results) {
                    Timber.d("${face.trackingId} - LEFT: ${face.leftEyeOpenProbability} RIGHT: ${face.rightEyeOpenProbability}")
                }
            }
            .addOnFailureListener(executor) { e: Exception ->
                e.printStackTrace()
                Timber.e("Face detection failed ${e.message}")
            }
}
