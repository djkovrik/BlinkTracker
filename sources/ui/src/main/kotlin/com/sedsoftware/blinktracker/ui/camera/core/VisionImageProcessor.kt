package com.sedsoftware.blinktracker.ui.camera.core

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import android.util.Log
import android.widget.Toast
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageProxy
import com.google.android.gms.tasks.TaskExecutors
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.common.internal.ImageConvertUtils
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetector
import com.google.mlkit.vision.face.FaceDetectorOptions
import com.sedsoftware.blinktracker.components.tracker.model.VisionFaceData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import timber.log.Timber
import java.nio.ByteBuffer
import java.util.concurrent.TimeUnit
import kotlin.math.round


interface VisionImageProcessor {
    val faceData: Flow<VisionFaceData>

    suspend fun process(image: ImageProxy)

    fun stop()
}

class FaceDetectorProcessor(detectorOptions: FaceDetectorOptions, context: Context) : VisionImageProcessor {
    private val detector: FaceDetector = FaceDetection.getClient(detectorOptions)
    private val executor: ScopedExecutor = ScopedExecutor(TaskExecutors.MAIN_THREAD)
    private val emptyData: VisionFaceData = VisionFaceData()
    private val _faceData: MutableStateFlow<VisionFaceData> = MutableStateFlow(emptyData)
    private var isShutdown: Boolean = false

    private var lastAnalyzedTimestamp = 0L
    private val lowLightThreshold = 105.0

    private var myContext = context

    override val faceData: Flow<VisionFaceData>
        get() = _faceData

    @ExperimentalGetImage
    override suspend fun process(image: ImageProxy) {
        if (isShutdown) {
            return
        }

        // Get average luminosity and if light is too dim, brighten image
        var inputImage = InputImage.fromMediaImage(image.image!!, image.imageInfo.rotationDegrees)
        val currentTimestamp = System.currentTimeMillis()
        if (currentTimestamp - lastAnalyzedTimestamp >=
            TimeUnit.SECONDS.toMillis(1)) {
            val buffer = image.planes[0].buffer
            val data = buffer.toByteArray()
            val pixels = data.map { it.toInt() and 0xFF }
            val luma = pixels.average()
            lastAnalyzedTimestamp = currentTimestamp

            if (luma <= lowLightThreshold) {
                val originalToBitmap = ImageConvertUtils.getInstance().getUpRightBitmap(inputImage)
                // Change to average luminosity
                val brighterBitmap = changeBitmapContrastBrightness(originalToBitmap, 1f, luma.toFloat())
//                val brighterBitmap = changeBitmapContrastBrightness(originalToBitmap, 1f, 100f)
                // Rotation degrees 0 because upright bitmap
                inputImage = InputImage.fromBitmap(brighterBitmap, 0)
                Toast.makeText(myContext, "Low light detected: $luma", Toast.LENGTH_SHORT).show()
            }
        }

        detector.process(inputImage)
            .addOnSuccessListener(executor) { results: List<Face> ->
                if (results.isNotEmpty()) {
                    val face = results.first()
                    _faceData.value = VisionFaceData(
                        leftEye = face.leftEyeOpenProbability.roundTo(PRECISION),
                        rightEye = face.rightEyeOpenProbability.roundTo(PRECISION),
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

    private fun ByteBuffer.toByteArray(): ByteArray {
        rewind()
        val data = ByteArray(remaining())
        get(data)
        return data
    }

    /**
     *
     * @param bmp input bitmap
     * @param contrast 0..10 1 is default
     * @param brightness -255..255 0 is default
     * @return new bitmap
     */
    private fun changeBitmapContrastBrightness(bmp: Bitmap, contrast: Float, brightness: Float): Bitmap {
        val cm = ColorMatrix(
            floatArrayOf(
                contrast, 0f, 0f, 0f, brightness,
                0f, contrast, 0f, 0f, brightness,
                0f, 0f, contrast, 0f, brightness,
                0f, 0f, 0f, 1f, 0f
            )
        )
        val ret = Bitmap.createBitmap(bmp.width, bmp.height, bmp.config)
        val canvas = Canvas(ret)
        val paint = Paint()
        paint.setColorFilter(ColorMatrixColorFilter(cm))
        canvas.drawBitmap(bmp, 0f, 0f, paint)
        return ret
    }

    override fun stop() {
        executor.shutdown()
        isShutdown = true
        detector.close()
    }

    @Suppress("MagicNumber")
    private fun Float?.roundTo(decimals: Int): Float? {
        if (this == null) return null
        var multiplier = 1.0f
        repeat(decimals) { multiplier *= 10f }
        return round(this * multiplier) / multiplier
    }

    private companion object {
        const val PRECISION = 4
    }
}
