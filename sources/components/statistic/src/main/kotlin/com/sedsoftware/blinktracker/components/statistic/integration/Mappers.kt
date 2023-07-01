package com.sedsoftware.blinktracker.components.statistic.integration

import com.sedsoftware.blinktracker.components.statistic.BlinkStatistic.Model
import com.sedsoftware.blinktracker.components.statistic.model.DisplayedPeriod
import com.sedsoftware.blinktracker.components.statistic.model.DisplayedStats
import com.sedsoftware.blinktracker.components.statistic.store.BlinkStatisticStore.State
import kotlin.math.round

internal val stateToModel: (State) -> Model =
    {
        when (it.stats) {
            is DisplayedStats.Loading -> {
                Model(
                    min = 0f,
                    max = 0f,
                    average = 0f,
                    records = emptyList(),
                    period = DisplayedPeriod.MINUTE,
                    isLoading = true,
                    isEmpty = false,
                )
            }

            is DisplayedStats.Empty -> {
                Model(
                    min = 0f,
                    max = 0f,
                    average = 0f,
                    records = emptyList(),
                    period = it.period,
                    isLoading = false,
                    isEmpty = true,
                )
            }

            is DisplayedStats.Content -> {
                Model(
                    min = it.stats.min.roundTo(1),
                    max = it.stats.max.roundTo(1),
                    average = it.stats.average.roundTo(1),
                    records = it.stats.records,
                    period = it.period,
                    isLoading = false,
                    isEmpty = false,
                )
            }
        }
    }

internal fun Float.roundTo(decimals: Int): Float {
    var multiplier = 1.0f
    repeat(decimals) { multiplier *= 10f }
    return round(this * multiplier) / multiplier
}
