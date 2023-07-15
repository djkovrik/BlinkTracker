package com.sedsoftware.blinktracker.components.tracker.model

data class NotificationInfoData(
    val replace: Boolean,
    val active: Boolean,
    val timer: String,
    val blinks: Int,
)
