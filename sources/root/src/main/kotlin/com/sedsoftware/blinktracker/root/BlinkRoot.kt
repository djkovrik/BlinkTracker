package com.sedsoftware.blinktracker.root

import com.sedsoftware.blinktracker.components.camera.BlinkCamera
import com.sedsoftware.blinktracker.components.preferences.BlinkPreferences
import com.sedsoftware.blinktracker.components.tracker.BlinkTracker
import com.sedsoftware.blinktracker.root.integration.ErrorHandler
import com.sedsoftware.blinktracker.root.integration.NotificationsManager

interface BlinkRoot {
    val errorHandler: ErrorHandler
    val notificationsManager: NotificationsManager
    val cameraComponent: BlinkCamera
    val preferencesComponent: BlinkPreferences
    val trackerComponent: BlinkTracker
}
