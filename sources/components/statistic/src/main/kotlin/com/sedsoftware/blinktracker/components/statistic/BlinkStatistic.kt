package com.sedsoftware.blinktracker.components.statistic

import com.sedsoftware.blinktracker.database.model.StatRecord
import kotlinx.coroutines.flow.Flow

interface BlinkStatistic {

    val models: Flow<Model>
    val initial: Model

    fun onNewBlinksValue(value: Int)

    data class Model(
        val records: List<StatRecord>,
        val rate: Float,
        val checked: Boolean,
        val showPlaceholder: Boolean,
    )

    sealed class Output {
        data class ErrorCaught(val throwable: Throwable) : Output()
    }
}
