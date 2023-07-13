package com.sedsoftware.blinktracker.settings

import kotlinx.coroutines.flow.Flow

interface Settings {
    suspend fun getPerMinuteThreshold(): Flow<Float>
    suspend fun getNotifySoundEnabled(): Flow<Boolean>
    suspend fun getNotifyVibrationEnabled(): Flow<Boolean>
    suspend fun getLaunchMinimizedEnabled(): Flow<Boolean>
    suspend fun getReplacePipEnabled(): Flow<Boolean>
    suspend fun setPerMinuteThreshold(value: Float)
    suspend fun setNotifySoundEnabled(value: Boolean)
    suspend fun setNotifyVibrationEnabled(value: Boolean)
    suspend fun setLaunchMinimizedEnabled(value: Boolean)
    suspend fun setReplacePipEnabled(value: Boolean)
}
