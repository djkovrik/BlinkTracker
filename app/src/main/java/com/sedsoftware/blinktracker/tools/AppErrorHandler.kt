package com.sedsoftware.blinktracker.tools

import android.content.Context
import com.sedsoftware.blinktracker.root.integration.ErrorHandler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class AppErrorHandler(
    private val context: Context,
) : ErrorHandler {

    private val _messages: MutableStateFlow<String?> = MutableStateFlow(null)

    override val messages: Flow<String?>
        get() = _messages

    override fun consume(throwable: Throwable) {
        // TODO split by throwable type
        _messages.value = "Unknown error"
    }
}
