package com.sedsoftware.blinktracker.camera.core

import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageProxy
import com.google.android.gms.tasks.TaskExecutors
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetector
import com.google.mlkit.vision.face.FaceDetectorOptions
import com.sedsoftware.blinktracker.components.tracker.model.VisionFaceData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import timber.log.Timber
import kotlin.math.round

interface VisionImageProcessor {
    val faceData: Flow<VisionFaceData>

    suspend fun process(image: ImageProxy)

    fun stop()
}

class FaceDetectorProcessor(detectorOptions: FaceDetectorOptions) : VisionImageProcessor {
    private val detector: FaceDetector = FaceDetection.getClient(detectorOptions)
    private val executor: ScopedExecutor = ScopedExecutor(TaskExecutors.MAIN_THREAD)
    private val emptyData: VisionFaceData = VisionFaceData()
    private val _faceData: MutableStateFlow<VisionFaceData> = MutableStateFlow(emptyData)
    private var isShutdown: Boolean = false

    override val faceData: Flow<VisionFaceData>
        get() = _faceData

    @ExperimentalGetImage
    override suspend fun process(image: ImageProxy) {
        if (isShutdown) {
            return
        }

        detector.process(InputImage.fromMediaImage(image.image!!, image.imageInfo.rotationDegrees))
            .addOnSuccessListener(executor) { results: List<Face> ->
                if (results.isNotEmpty()) {
                    val face = results.first()
                    _faceData.value = VisionFaceData(
                        leftEye = face.leftEyeOpenProbability.roundTo(4),
                        rightEye = face.rightEyeOpenProbability.roundTo(4),
                        faceAvailable = face.leftEyeOpenProbability != null && face.rightEyeOpenProbability != null,
                    )
                } else {
                    _faceData.value = emptyData
                }
            }
            .addOnFailureListener(executor) { exception: Exception ->
                exception.printStackTrace()
                Timber.e("Face detection failed ${exception.message}")
                _faceData.value = emptyData
            }
            .addOnCompleteListener { image.close() }
    }

    override fun stop() {
        executor.shutdown()
        isShutdown = true
        detector.close()
    }

    private fun Float?.roundTo(decimals: Int): Float? {
        if (this == null) return null
        var multiplier = 1.0f
        repeat(decimals) { multiplier *= 10f }
        return round(this * multiplier) / multiplier
    }
}
