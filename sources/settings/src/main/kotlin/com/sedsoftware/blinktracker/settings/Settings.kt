package com.sedsoftware.blinktracker.settings

import kotlinx.coroutines.flow.Flow

interface Settings {
    val observableOpacity: Flow<Float>
    fun getPerMinuteThreshold(): Flow<Float>
    fun getNotifySoundEnabled(): Flow<Boolean>
    fun getNotifyVibrationEnabled(): Flow<Boolean>
    fun getLaunchMinimizedEnabled(): Flow<Boolean>
    fun getMinimizedOpacity(): Flow<Float>
    fun getAutoStartEnabled(): Flow<Boolean>
    suspend fun setPerMinuteThreshold(value: Float)
    suspend fun setNotifySoundEnabled(value: Boolean)
    suspend fun setNotifyVibrationEnabled(value: Boolean)
    suspend fun setLaunchMinimizedEnabled(value: Boolean)
    suspend fun setMinimizedOpacity(value: Float)
    suspend fun setAutoStartEnabled(value: Boolean)
}
