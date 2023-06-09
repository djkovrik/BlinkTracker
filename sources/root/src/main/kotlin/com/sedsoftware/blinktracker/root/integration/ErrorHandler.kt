package com.sedsoftware.blinktracker.root.integration

import kotlinx.coroutines.flow.Flow

interface ErrorHandler {
    val messages: Flow<String?>

    fun consume(throwable: Throwable)
}
