package com.sedsoftware.blinktracker.components.home.integration

interface NotificationsManager {
    fun notifyWithSound()
    fun notifyWithVibro()
    fun showTrackingNotification(active: Boolean, timer: String, blinks: Int)
    fun clearNotification()
}
