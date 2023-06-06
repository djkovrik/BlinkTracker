package com.sedsoftware.blinktracker.tools

import android.content.Context
import com.sedsoftware.blinktracker.root.integration.ErrorHandler
import kotlinx.coroutines.flow.Flow

class AppErrorHandler(
    private val context: Context,
) : ErrorHandler {

    override val messages: Flow<String>
        get() = TODO("Not yet implemented")

    override fun consume(throwable: Throwable) {
        TODO("Not yet implemented")
    }
}
