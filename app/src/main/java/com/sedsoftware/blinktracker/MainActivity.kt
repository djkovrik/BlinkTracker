package com.sedsoftware.blinktracker

import android.Manifest
import android.app.PictureInPictureParams
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.util.Rational
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.arkivanov.decompose.defaultComponentContext
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import com.google.mlkit.vision.face.FaceDetectorOptions
import com.sedsoftware.blinktracker.components.camera.model.CameraLens
import com.sedsoftware.blinktracker.components.home.integration.ErrorHandler
import com.sedsoftware.blinktracker.components.tracker.tools.PictureInPictureLauncher
import com.sedsoftware.blinktracker.database.StatisticsRepositoryReal
import com.sedsoftware.blinktracker.root.BlinkRoot
import com.sedsoftware.blinktracker.root.integration.BlinkRootComponent
import com.sedsoftware.blinktracker.settings.AppSettings
import com.sedsoftware.blinktracker.settings.Settings
import com.sedsoftware.blinktracker.tools.AppErrorHandler
import com.sedsoftware.blinktracker.tools.AppNotificationsManager
import com.sedsoftware.blinktracker.ui.BlinkRootContent
import com.sedsoftware.blinktracker.ui.Constants
import com.sedsoftware.blinktracker.ui.camera.core.FaceDetectorProcessor
import com.sedsoftware.blinktracker.ui.camera.core.VisionImageProcessor
import com.sedsoftware.blinktracker.ui.theme.BlinkTrackerTheme
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import timber.log.Timber

class MainActivity : ComponentActivity(), PictureInPictureLauncher {

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
                root.onPermissionGranted()
                checkIfCamerasAvailable()
            } else {
                root.onPermissionDenied()
            }
        }

    private var currentWindowAlpha: Float = 1f
    private var settings: Settings? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val errorHandler: ErrorHandler = AppErrorHandler(this)

        val faceDetectorOptions: FaceDetectorOptions = FaceDetectorOptions.Builder()
            .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_NONE)
            .setContourMode(FaceDetectorOptions.CONTOUR_MODE_ALL)
            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
            .build()

        _imageProcessor = FaceDetectorProcessor(faceDetectorOptions, this)
        settings = AppSettings(applicationContext)

        _root = BlinkRootComponent(
            componentContext = defaultComponentContext(),
            storeFactory = DefaultStoreFactory(),
            errorHandler = errorHandler,
            notificationsManager = AppNotificationsManager(this),
            settings = settings!!,
            repo = StatisticsRepositoryReal(applicationContext),
            pipLauncher = this,
        )

        imageProcessor.faceData
            .onEach { root.onFaceDataChanged(it) }
            .launchIn(lifecycleScope)

        settings?.observableOpacity
            ?.onEach { currentWindowAlpha = it }
            ?.launchIn(lifecycleScope)

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

    override fun onResume() {
        super.onResume()
        enableKeepScreenOn(true)
        changeMinimizedAlpha(enabled = false)
    }

    override fun onPause() {
        super.onPause()
        enableKeepScreenOn(false)
    }

    public override fun onDestroy() {
        super.onDestroy()
        _root = null
        _imageProcessor?.run { this.stop() }
        _imageProcessor = null
        settings = null
        enableKeepScreenOn(false)
    }

    override fun onPictureInPictureModeChanged(isInPictureInPictureMode: Boolean, newConfig: Configuration) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig)
        root.onPictureInPictureChanged(enabled = isInPictureInPictureMode)
        changeMinimizedAlpha(enabled = isInPictureInPictureMode)
    }

    override fun launchPictureInPicture() {
        enterPictureInPictureMode(getPictureInPictureParams())
    }

    private fun checkCameraPermissions() {
        when {
            ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED -> {
                root.onPermissionGranted()
                checkIfCamerasAvailable()
            }

            shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) -> {
                root.onPermissionRationale()
            }

            else -> {
                requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }

    private fun checkIfCamerasAvailable() {
        if (applicationContext.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT)) {
            root.onCurrentLensChanged(CameraLens.FRONT)
        } else {
            root.onCurrentLensChanged(CameraLens.NOT_AVAILABLE)
        }
    }

    private fun getPictureInPictureParams(): PictureInPictureParams {
        val params = PictureInPictureParams.Builder()
            .setAspectRatio(Rational(Constants.PIP_RATIO_WIDTH, Constants.PIP_RATIO_HEIGHT))
            .apply {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    setAutoEnterEnabled(false)
                    setSeamlessResizeEnabled(false)
                }
            }
            .build()
        setPictureInPictureParams(params)
        return params
    }

    private fun enableKeepScreenOn(enabled: Boolean) {
        if (enabled) {
            this.window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        } else {
            this.window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
        Timber.i("Screen always awake - $enabled")
    }

    private fun changeMinimizedAlpha(enabled: Boolean) {
        val currentAlpha = if (enabled) currentWindowAlpha else 1f
        val params = window.attributes
        params.alpha = currentAlpha
        window.attributes = params
    }
}
