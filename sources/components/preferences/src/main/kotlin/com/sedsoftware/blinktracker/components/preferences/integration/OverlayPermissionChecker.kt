package com.sedsoftware.blinktracker.components.preferences.integration

interface OverlayPermissionChecker {
    fun isPermissionGranted(): Boolean
    fun requestPermission()
}
