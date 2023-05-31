package com.sedsoftware.blinktracker

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import com.sedsoftware.blinktracker.ui.theme.BlinkTrackerTheme

class MainActivity : ComponentActivity() {

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                // TODO GRANTED
                log("GRANTED!")
            } else {
                // TODO DECLINED
                log("DECLINED!")
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BlinkTrackerTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    Greeting("Android")
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        checkCameraPermissions()
    }

    private fun checkCameraPermissions() {
        log("CHECKING...")
        when {
            ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED -> {
                // You can use the API that requires the permission.
                log("GRANTED!")
            }

            shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) -> {
                // In an educational UI, explain to the user why your app requires this
                // permission for a specific feature to behave as expected, and what
                // features are disabled if it's declined. In this UI, include a
                // "cancel" or "no thanks" button that lets the user continue
                // using your app without granting the permission.
                // SHOW EXPLANATION
                log("DECLINED - SHOW RATIONALE")
            }

            else -> {
                // You can directly ask for the permission.
                // The registered ActivityResultCallback gets the result of this request.
                log("REQUESTING...")
                requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }

    @Suppress("UnusedPrivateMember")
    private fun runIfHasFrontCamera(callback: () -> Unit) {
        if (applicationContext.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT)) {
            callback.invoke()
        } else {
            // TODO NOTIFY THAT THERE IS NO FRONT CAMERA
        }
    }

    private fun log(str: String) {
        Log.d("CameraDebug", "Camera: $str")
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    BlinkTrackerTheme {
        Greeting("Android")
    }
}
