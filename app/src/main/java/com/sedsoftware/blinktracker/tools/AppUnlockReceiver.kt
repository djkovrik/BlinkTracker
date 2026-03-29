package com.sedsoftware.blinktracker.tools

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Settings
import com.google.firebase.Firebase
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.crashlytics.crashlytics
import com.sedsoftware.blinktracker.MainActivity
import com.sedsoftware.blinktracker.settings.AppSettings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import timber.log.Timber

@Suppress("TooGenericExceptionCaught")
class AppUnlockReceiver : BroadcastReceiver() {

    private val receiverScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private val crashlytics: FirebaseCrashlytics = Firebase.crashlytics

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action ?: return

        when (action) {
            Intent.ACTION_USER_PRESENT,
            Intent.ACTION_BOOT_COMPLETED,
                -> {
                checkAutoStartAndLaunch(context)
            }
        }
    }

    private fun checkAutoStartAndLaunch(context: Context) {
        receiverScope.launch {
            try {
                val isEnabled = AppSettings(context).getAutoStartEnabled().first()
                if (!isEnabled) return@launch

                if (canLaunchActivityFromBackground(context)) {
                    launchMainActivity(context)
                } else {
                    Timber.d("Can't start activity as overlay")
                }
            } catch (e: Exception) {
                Timber.e(e, "Failed to start activity from receiver")
                crashlytics.recordException(e)
            }
        }
    }

    private fun canLaunchActivityFromBackground(context: Context): Boolean {
        return Settings.canDrawOverlays(context)
    }

    private fun launchMainActivity(context: Context) {
        val intent = Intent(context, MainActivity::class.java).apply {
            addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK or
                        Intent.FLAG_ACTIVITY_CLEAR_TOP or
                        Intent.FLAG_ACTIVITY_SINGLE_TOP or
                        Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
            )
        }

        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            Timber.e(e, "Failed to start activity even with overlay")
            crashlytics.recordException(e)
        }
    }
}
