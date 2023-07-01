package com.sedsoftware.blinktracker.components.statistic.integration

import com.sedsoftware.blinktracker.components.statistic.BlinkStatistic.Model
import com.sedsoftware.blinktracker.components.statistic.model.DisplayedPeriod
import com.sedsoftware.blinktracker.components.statistic.model.DisplayedStats
import com.sedsoftware.blinktracker.components.statistic.store.BlinkStatisticStore.State

internal val stateToModel: (State) -> Model =
    {
        when (it.stats) {
            is DisplayedStats.Loading -> {
                Model(
                    min = 0,
                    max = 0,
                    average = 0f,
                    records = emptyList(),
                    period = DisplayedPeriod.MINUTE,
                    isLoading = true,
                    isEmpty = false,
                )
            }

            is DisplayedStats.Empty -> {
                Model(
                    min = 0,
                    max = 0,
                    average = 0f,
                    records = emptyList(),
                    period = DisplayedPeriod.MINUTE,
                    isLoading = false,
                    isEmpty = true,
                )
            }

            is DisplayedStats.Content -> {
                Model(
                    min = it.stats.min,
                    max = it.stats.max,
                    average = it.stats.average,
                    records = it.stats.records,
                    period = it.stats.period,
                    isLoading = false,
                    isEmpty = false,
                )
            }
        }
    }
