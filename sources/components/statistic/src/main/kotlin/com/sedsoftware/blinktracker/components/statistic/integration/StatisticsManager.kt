package com.sedsoftware.blinktracker.components.statistic.integration

import com.sedsoftware.blinktracker.components.statistic.model.DisplayedPeriod
import com.sedsoftware.blinktracker.components.statistic.model.DisplayedStats
import com.sedsoftware.blinktracker.database.StatisticsRepository
import com.sedsoftware.blinktracker.database.model.BlinksRecordDbModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlin.math.round

internal interface StatisticsManager {
    val stats: StateFlow<DisplayedStats>

    suspend fun switchPeriod(newPeriod: DisplayedPeriod)
    suspend fun saveBlinks(newBlinks: Int)
}

internal class StatisticsManagerImpl(
    private val repo: StatisticsRepository,
    scope: CoroutineScope,
) : StatisticsManager {

    override val stats: StateFlow<DisplayedStats>
        get() = _stats

    private val _stats: MutableStateFlow<DisplayedStats> = MutableStateFlow(DisplayedStats.Loading)
    private val period: MutableStateFlow<DisplayedPeriod> = MutableStateFlow(DisplayedPeriod.MINUTE)

    init {
        scope.launch {
            combine(repo.observe(), period) { entries, period -> entries to period }
                .collect { consumeStatFlows(it.first, it.second) }
        }
    }

    override suspend fun switchPeriod(newPeriod: DisplayedPeriod) {
        period.emit(newPeriod)
    }

    override suspend fun saveBlinks(newBlinks: Int) {
        repo.insert(newBlinks)
    }

    private suspend fun consumeStatFlows(entries: List<BlinksRecordDbModel>, period: DisplayedPeriod) {
        when (period) {
            DisplayedPeriod.MINUTE -> {

            }

            DisplayedPeriod.QUARTER_HOUR -> {

            }

            DisplayedPeriod.HOUR -> {

            }

            DisplayedPeriod.SIX_HOURS -> {

            }

            DisplayedPeriod.DAY -> {

            }

            DisplayedPeriod.MONTH -> {

            }
        }

        _stats.emit(DisplayedStats.Empty)
    }

    private fun Float.roundTo(decimals: Int): Float {
        var multiplier = 1.0f
        repeat(decimals) { multiplier *= 10f }
        return round(this * multiplier) / multiplier
    }
}

