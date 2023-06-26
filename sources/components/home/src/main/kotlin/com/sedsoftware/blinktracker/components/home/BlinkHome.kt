package com.sedsoftware.blinktracker.components.home

import com.sedsoftware.blinktracker.components.camera.BlinkCamera
import com.sedsoftware.blinktracker.components.home.integration.ErrorHandler
import com.sedsoftware.blinktracker.components.home.integration.NotificationsManager
import com.sedsoftware.blinktracker.components.statistic.BlinkStatistic
import com.sedsoftware.blinktracker.components.tracker.BlinkTracker

interface BlinkHome {
    val errorHandler: ErrorHandler
    val notificationsManager: NotificationsManager
    val cameraComponent: BlinkCamera
    val trackerComponent: BlinkTracker
    val statsComponent: BlinkStatistic
}

