package com.sedsoftware.blinktracker.tools

import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.core.net.toUri
import com.sedsoftware.blinktracker.components.preferences.integration.OverlayPermissionChecker

class AppOverlayPermissionChecker(
    private val context: Context,
) : OverlayPermissionChecker {

    override fun isPermissionGranted(): Boolean {
        return Settings.canDrawOverlays(context)
    }

    override fun requestPermission() {
        val intent = Intent(
            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            "package:${context.packageName}".toUri()
        ).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }
}
