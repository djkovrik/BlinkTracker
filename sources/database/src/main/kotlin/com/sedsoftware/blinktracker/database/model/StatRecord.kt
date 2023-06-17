package com.sedsoftware.blinktracker.database.model

import kotlinx.datetime.LocalDateTime

data class StatRecord(
    val blinks: Int,
    val date: LocalDateTime,
)
