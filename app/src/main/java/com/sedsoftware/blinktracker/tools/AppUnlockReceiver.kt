package com.sedsoftware.blinktracker.tools

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import com.sedsoftware.blinktracker.settings.AppSettings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import timber.log.Timber

class AppUnlockReceiver : BroadcastReceiver() {

    private val receiverScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private val crashlytics: FirebaseCrashlytics = Firebase.crashlytics

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action ?: return

        when (action) {
            Intent.ACTION_USER_PRESENT,
            Intent.ACTION_BOOT_COMPLETED -> {
                checkAutoStartAndLaunchService(context)
            }
        }
    }

    private fun checkAutoStartAndLaunchService(context: Context) {
        receiverScope.launch {
            try {
                val isAutoStartEnabled = AppSettings(context).getAutoStartEnabled().first()
                if (isAutoStartEnabled) {
                    startForegroundService(context)
                }
            } catch (e: Exception) {
                Timber.e(e, "Failed to start service from receiver check")
                crashlytics.recordException(e)
            }
        }
    }

    private fun startForegroundService(context: Context) {
        val serviceIntent = Intent(context, AppUnlockForegroundService::class.java).apply {
            action = AppUnlockForegroundService.ACTION_START_ON_UNLOCK
        }

        try {
            ContextCompat.startForegroundService(context, serviceIntent)
        } catch (e: Exception) {
            Timber.e(e, "Failed to start service from receiver")
            crashlytics.recordException(e)
        }
    }
}
