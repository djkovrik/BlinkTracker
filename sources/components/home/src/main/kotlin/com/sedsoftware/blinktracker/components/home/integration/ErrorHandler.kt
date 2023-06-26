package com.sedsoftware.blinktracker.components.home.integration

import kotlinx.coroutines.flow.Flow

interface ErrorHandler {
    val messages: Flow<String?>

    fun consume(throwable: Throwable)
}
