package com.sedsoftware.blinktracker.tools

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.core.app.NotificationCompat
import com.sedsoftware.blinktracker.MainActivity
import com.sedsoftware.blinktracker.R
import com.sedsoftware.blinktracker.components.home.integration.NotificationsManager
import timber.log.Timber

@Suppress("DEPRECATION")
class AppNotificationsManager(
    private val context: Context,
) : NotificationsManager {

    private var vibrator: Vibrator? = null

    private var vibratorManager: VibratorManager? = null

    private val notificationManager: NotificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    private var builder = NotificationCompat.Builder(context, BLINKZ_NOTIFICATION_CHANNEL_ID)
        .setSmallIcon(R.drawable.ic_track_notification)
        .setContentTitle(getTitle(false))
        .setContentText(getContent("", 0))
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setContentIntent(
            PendingIntent.getActivity(
                context, 0,
                Intent(context, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                }, PendingIntent.FLAG_IMMUTABLE
            )
        )
        .setSilent(true)
        .setAutoCancel(false)

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager?
            vibrator = vibratorManager?.defaultVibrator
        } else {
            vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator?
        }

        createNotificationChannel()
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

    override fun showTrackingNotification(active: Boolean, timer: String, blinks: Int) {
        builder.setContentTitle(getTitle(active))
        builder.setContentText(getContent(timer, blinks))
        notificationManager.notify(BLINKZ_NOTIFICATION_ID, builder.build())
    }

    override fun clearNotification() {
        notificationManager.cancel(BLINKZ_NOTIFICATION_ID)
    }

    private fun getTitle(active: Boolean): String = if (active) {
        context.getString(R.string.notification_tracking_active)
    } else {
        context.getString(R.string.notification_tracking_not_active)
    }

    private fun getContent(timer: String, blinks: Int): String =
        "${context.getString(R.string.notification_tracking_time)}: $timer | " +
            "${context.getString(R.string.notification_tracking_blinks)}: $blinks"

    private fun createNotificationChannel() {
        if (notificationManager.getNotificationChannel(BLINKZ_NOTIFICATION_CHANNEL_ID) == null) {
            val name = context.getString(R.string.notification_channel)
            val descriptionText = context.getString(R.string.notification_channel_desc)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(BLINKZ_NOTIFICATION_CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }

            notificationManager.createNotificationChannel(channel)
        }
    }

    private companion object {
        const val VIBRATION_DURATION = 1000L
        const val BLINKZ_NOTIFICATION_ID = 1213
        const val BLINKZ_NOTIFICATION_CHANNEL_ID = "blinkz_notification_channel"
    }
}
