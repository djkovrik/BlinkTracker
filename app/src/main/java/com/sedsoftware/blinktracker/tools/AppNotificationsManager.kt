package com.sedsoftware.blinktracker.tools

import android.content.Context
import android.media.RingtoneManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import com.sedsoftware.blinktracker.components.home.integration.NotificationsManager
import timber.log.Timber

@Suppress("DEPRECATION")
class AppNotificationsManager(
    private val context: Context,
) : NotificationsManager {

    private var vibrator: Vibrator? = null
    private var vibratorManager: VibratorManager? = null

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager?
            vibrator = vibratorManager?.defaultVibrator
        } else {
            vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator?
        }
    }

    @Suppress("TooGenericExceptionCaught")
    override fun notifyWithSound() {
        try {
            val notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val ringtone = RingtoneManager.getRingtone(context, notification)
            ringtone.play()
        } catch (exception: Exception) {
            Timber.e("Failed to notify with sound", exception)
        }
    }

    override fun notifyWithVibro() {
        vibrator?.vibrate(VibrationEffect.createOneShot(VIBRATION_DURATION, VibrationEffect.DEFAULT_AMPLITUDE))
    }

    private companion object {
        const val VIBRATION_DURATION = 1000L
    }
}
