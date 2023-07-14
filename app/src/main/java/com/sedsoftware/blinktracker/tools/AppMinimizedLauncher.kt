package com.sedsoftware.blinktracker.tools

import android.content.Context
import android.content.Intent
import com.sedsoftware.blinktracker.components.tracker.tools.MinimizedLauncher

class AppMinimizedLauncher(
    private val context: Context,
) : MinimizedLauncher {

    override fun launchMinimized() {
        val minimize = Intent(Intent.ACTION_MAIN)
        minimize.addCategory(Intent.CATEGORY_HOME)
        minimize.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(minimize)
    }
}
