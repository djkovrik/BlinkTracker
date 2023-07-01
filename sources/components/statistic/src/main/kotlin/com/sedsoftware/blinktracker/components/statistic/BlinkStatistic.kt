package com.sedsoftware.blinktracker.components.statistic

import com.patrykandpatrick.vico.core.entry.ChartEntry
import com.sedsoftware.blinktracker.components.statistic.model.DisplayedPeriod
import kotlinx.coroutines.flow.Flow

interface BlinkStatistic {

    val models: Flow<Model>
    val initial: Model

    fun onNewBlinksValue(value: Int)
    fun onPeriodChipSelect(value: DisplayedPeriod)

    data class Model(
        val isLoading: Boolean,
        val isEmpty: Boolean,
        val min: Float,
        val max: Float,
        val average: Float,
        val records: List<ChartEntry>,
        val period: DisplayedPeriod,
    )

    sealed class Output {
        data class ErrorCaught(val throwable: Throwable) : Output()
    }
}
