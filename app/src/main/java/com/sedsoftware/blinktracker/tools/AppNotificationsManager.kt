package com.sedsoftware.blinktracker.tools

import android.content.Context
import com.sedsoftware.blinktracker.root.integration.NotificationsManager

class AppNotificationsManager(
    private val context: Context,
) : NotificationsManager {

    override fun notifyWithSound() {
        context
    }

    override fun notifyWithVibro() {
        context
    }
}
