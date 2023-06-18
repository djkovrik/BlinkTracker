package com.sedsoftware.blinktracker.components.statistic.model

import kotlinx.datetime.LocalDateTime

data class StatRecord(
    val blinks: Int,
    val dateTime: LocalDateTime,
)

fun List<StatRecord>.getAverage(): Float =
    if (this.isNotEmpty()) {
        this.sumOf { it.blinks }.toFloat() / this.size
    } else {
        0f
    }
