package com.sedsoftware.blinktracker.components.tracker.tools

interface OngoingNotificationManager {
    fun showNotification(initialMessage: String)
    fun refreshMessage(message: String)
    fun hideNotification()
}
