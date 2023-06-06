package com.sedsoftware.blinktracker

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.arkivanov.decompose.defaultComponentContext
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import com.google.mlkit.vision.face.FaceDetectorOptions
import com.sedsoftware.blinktracker.camera.core.FaceDetectorProcessor
import com.sedsoftware.blinktracker.camera.core.VisionImageProcessor
import com.sedsoftware.blinktracker.components.camera.model.CameraLens
import com.sedsoftware.blinktracker.root.BlinkRoot
import com.sedsoftware.blinktracker.root.integration.BlinkRootComponent
import com.sedsoftware.blinktracker.root.integration.ErrorHandler
import com.sedsoftware.blinktracker.settings.AppSettings
import com.sedsoftware.blinktracker.tools.AppErrorHandler
import com.sedsoftware.blinktracker.tools.AppNotificationsManager
import com.sedsoftware.blinktracker.ui.compose.BlinkRootContent
import com.sedsoftware.blinktracker.ui.theme.BlinkTrackerTheme

class MainActivity : ComponentActivity() {

    private var _imageProcessor: VisionImageProcessor? = null

    private val imageProcessor: VisionImageProcessor
        get() = _imageProcessor!!

    private var _root: BlinkRoot? = null

    private val root: BlinkRoot
        get() = _root!!

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                root.cameraComponent.onPermissionGranted()
                checkIfCamerasAvailable()
            } else {
                root.cameraComponent.onPermissionDenied()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val errorHandler: ErrorHandler = AppErrorHandler(this)

        val faceDetectorOptions: FaceDetectorOptions = FaceDetectorOptions.Builder()
            .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_NONE)
            .setContourMode(FaceDetectorOptions.CONTOUR_MODE_ALL)
            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
            .build()

        _imageProcessor = FaceDetectorProcessor(faceDetectorOptions)

        _root = BlinkRootComponent(
            componentContext = defaultComponentContext(),
            storeFactory = DefaultStoreFactory(),
            errorHandler = errorHandler,
            notificationsManager = AppNotificationsManager(this),
            settings = AppSettings(applicationContext)
        )

        setContent {
            BlinkTrackerTheme {
                BlinkRootContent(root, imageProcessor)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        checkCameraPermissions()
    }

    public override fun onDestroy() {
        super.onDestroy()
        _root = null
        _imageProcessor?.run { this.stop() }
        _imageProcessor = null
    }

    private fun checkCameraPermissions() {
        when {
            ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED -> {
                root.cameraComponent.onPermissionGranted()
                checkIfCamerasAvailable()
            }

            shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) -> {
                root.cameraComponent.onPermissionRationale()
            }

            else -> {
                requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }

    private fun checkIfCamerasAvailable() {
        when {
            applicationContext.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT) -> {
                root.cameraComponent.onCurrentLensChanged(CameraLens.FRONT)
            }

            applicationContext.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY) -> {
                root.cameraComponent.onCurrentLensChanged(CameraLens.BACK)
            }

            else -> {
                root.cameraComponent.onCurrentLensChanged(CameraLens.NOT_AVAILABLE)
            }
        }
    }
}
