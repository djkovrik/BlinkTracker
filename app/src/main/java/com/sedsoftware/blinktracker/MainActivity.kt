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
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
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
import com.sedsoftware.blinktracker.tools.AppErrorHandler
import com.sedsoftware.blinktracker.tools.AppMinimizedLauncher
import com.sedsoftware.blinktracker.tools.AppNotificationsManager
import com.sedsoftware.blinktracker.ui.BlinkRootContent
import com.sedsoftware.blinktracker.ui.Constants
import com.sedsoftware.blinktracker.ui.camera.core.FaceDetectorProcessor
import com.sedsoftware.blinktracker.ui.camera.core.VisionImageProcessor
import com.sedsoftware.blinktracker.ui.theme.BlinkTrackerTheme
import kotlinx.coroutines.launch
import timber.log.Timber

class MainActivity : ComponentActivity(), PictureInPictureLauncher {

    private var _imageProcessor: VisionImageProcessor? = null
    private var _lifecycleRegistry: LifecycleRegistry? = null

    private val imageProcessor: VisionImageProcessor
        get() = _imageProcessor!!

    private val lifecycleRegistry: LifecycleRegistry
        get() = _lifecycleRegistry!!

    private var _root: BlinkRoot? = null

    private val root: BlinkRoot
        get() = _root!!

    private val cameraPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                root.onCameraPermissionGranted()
                checkIfCamerasAvailable()
            } else {
                root.onCameraPermissionDenied()
            }
        }

    private val notificationsPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                root.onNotificationPermissionGranted()
            } else {
                root.onNotificationPermissionDenied()
            }
        }

    private val customLifecycleOwner: LifecycleOwner
        get() = object : LifecycleOwner {
            override val lifecycle: Lifecycle
                get() = lifecycleRegistry
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
            notificationsManager = AppNotificationsManager(applicationContext),
            settings = AppSettings(applicationContext),
            repo = StatisticsRepositoryReal(applicationContext),
            pipLauncher = this,
            minimizedLauncher = AppMinimizedLauncher(applicationContext),
        )

        lifecycleScope.launch {
            imageProcessor.faceData.collect {
                root.onFaceDataChanged(it)
            }
        }

        setContent {
            CompositionLocalProvider(LocalLifecycleOwner provides customLifecycleOwner) {
                BlinkTrackerTheme {
                    BlinkRootContent(
                        component = root,
                        processor = imageProcessor,
                        onNotificationPermission = ::checkNotificationPermission,
                    )
                }
            }
        }

        _lifecycleRegistry = LifecycleRegistry(this)
//        lifecycleRegistry.currentState = Lifecycle.State.CREATED
//        lifecycleRegistry.currentState = Lifecycle.State.STARTED
        lifecycleRegistry.currentState = Lifecycle.State.RESUMED
    }

    override fun onStart() {
        super.onStart()
        checkCameraPermission()
        enableKeepScreenOn(true)

    }

    override fun onStop() {
        enableKeepScreenOn(false)
        super.onStop()
    }

    public override fun onDestroy() {
//        lifecycleRegistry.currentState = Lifecycle.State.RESUMED
//        lifecycleRegistry.currentState = Lifecycle.State.STARTED
        lifecycleRegistry.currentState = Lifecycle.State.DESTROYED
        _root = null
        _imageProcessor?.run { this.stop() }
        _imageProcessor = null
        _lifecycleRegistry = null
        super.onDestroy()
    }

    override fun onPictureInPictureModeChanged(isInPictureInPictureMode: Boolean, newConfig: Configuration) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig)
        root.onPictureInPictureChanged(enabled = isInPictureInPictureMode)
    }

    override fun launchPictureInPicture() {
        enterPictureInPictureMode(getPictureInPictureParams())
    }

    private fun checkCameraPermission() {
        when {
            ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED -> {
                root.onCameraPermissionGranted()
                checkIfCamerasAvailable()
            }

            shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) -> {
                root.onCameraPermissionRationale()
            }

            else -> {
                cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }

    private fun checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                root.onNotificationPermissionGranted()
            } else {
                notificationsPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        } else {
            root.onNotificationPermissionGranted()
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

}
