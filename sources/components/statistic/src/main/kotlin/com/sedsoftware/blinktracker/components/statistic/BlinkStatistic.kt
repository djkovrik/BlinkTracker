package com.sedsoftware.blinktracker.components.statistic

import com.sedsoftware.blinktracker.components.statistic.model.StatRecord
import kotlinx.coroutines.flow.Flow

interface BlinkStatistic {

    val models: Flow<Model>
    val initial: Model

    fun onNewBlinksValue(value: Int)

    data class Model(
        val records: List<StatRecord>,
        val average: Float,
        val min: Int,
        val max: Int,
        val checked: Boolean,
        val showPlaceholder: Boolean,
    )

    sealed class Output {
        data class ErrorCaught(val throwable: Throwable) : Output()
    }
}
