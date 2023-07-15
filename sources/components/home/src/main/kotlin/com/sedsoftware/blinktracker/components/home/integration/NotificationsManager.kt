package com.sedsoftware.blinktracker.components.home.integration

import com.sedsoftware.blinktracker.components.tracker.model.NotificationInfoData

interface NotificationsManager {
    fun notifyWithSound()
    fun notifyWithVibro()
    fun showTrackingNotification(data: NotificationInfoData)
}
