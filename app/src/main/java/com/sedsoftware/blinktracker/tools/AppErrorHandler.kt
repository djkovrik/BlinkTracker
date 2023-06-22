package com.sedsoftware.blinktracker.tools

import android.content.Context
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import com.sedsoftware.blinktracker.root.integration.ErrorHandler
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
        // TODO split by throwable type
        context
        _messages.value = "Unknown error"
        Timber.e("Blink tracker error: ${throwable.message}", throwable)
        crashlytics.recordException(throwable)
    }
}
