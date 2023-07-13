package com.sedsoftware.blinktracker.tools

import android.content.Context
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import com.sedsoftware.blinktracker.R
import com.sedsoftware.blinktracker.components.home.integration.ErrorHandler
import com.sedsoftware.blinktracker.components.preferences.infrastructure.NotificationPermissionDeniedException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import timber.log.Timber

class AppErrorHandler(
    private val context: Context,
) : ErrorHandler {

    private val _messages: MutableStateFlow<String?> = MutableStateFlow(null)
    private val crashlytics: FirebaseCrashlytics = Firebase.crashlytics

    override val messages: Flow<String?>
        get() = _messages

    override fun consume(throwable: Throwable) {
        val message = when (throwable) {
            is NotificationPermissionDeniedException -> {
                context.getString(R.string.error_notif_permission)
            }

            else -> {
                crashlytics.recordException(throwable)
                context.getString(R.string.error_unknown)
            }
        }


        _messages.value = message
        Timber.e("Blink tracker error: ${throwable.message}", throwable)
    }
}
