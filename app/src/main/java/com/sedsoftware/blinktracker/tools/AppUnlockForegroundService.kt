package com.sedsoftware.blinktracker.tools

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import com.sedsoftware.blinktracker.MainActivity
import com.sedsoftware.blinktracker.R
import timber.log.Timber

class AppUnlockForegroundService : Service() {

    private val crashlytics: FirebaseCrashlytics = Firebase.crashlytics

    companion object {
        const val ACTION_START_ON_UNLOCK = "ACTION_START_ON_UNLOCK"
        private const val NOTIFICATION_ID = 1787
        private const val CHANNEL_ID = "blinkz_unlock_channel"
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.action

        if (action == ACTION_START_ON_UNLOCK) {
            startMainActivity()
        }

        val notification = buildNotification()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            startForeground(
                NOTIFICATION_ID,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE
            )
        } else {
            startForeground(NOTIFICATION_ID, notification)
        }

        stopSelf()

        return START_NOT_STICKY
    }

    private fun startMainActivity() {
        val intent = Intent(this, MainActivity::class.java).apply {
            addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK or
                        Intent.FLAG_ACTIVITY_CLEAR_TOP or
                        Intent.FLAG_ACTIVITY_SINGLE_TOP or
                        Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
            )
        }

        try {
            startActivity(intent)
        } catch (e: Exception) {
            Timber.e(e, "Failed to start activity from service")
            crashlytics.recordException(e)
        }
    }

    private fun buildNotification(): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(resources.getString(R.string.app_name))
            .setContentText(resources.getString(R.string.app_notification_text))
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(false)
            .build()
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            resources.getString(R.string.app_notification_channel),
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = resources.getString(R.string.app_notification_channel_description)
        }

        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
