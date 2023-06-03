package com.sedsoftware.blinktracker.components.preferences

import com.arkivanov.decompose.value.Value

interface BlinkPreferences {

    val models: Value<Model>

    fun onMinimalThresholdChanged(value: Int)
    fun onNotifySoundChanged(value: Boolean)
    fun onNotifyVibrationChanged(value: Boolean)

    data class Model(
        val settingsPanelVisible: Boolean,
        val selectedThreshold: Int,
        val notifySoundChecked: Boolean,
        val notifyVibrationChecked: Boolean,
    )
}
