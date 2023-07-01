package com.sedsoftware.blinktracker.components.statistic.integration

import com.patrykandpatrick.vico.core.extension.sumOf
import com.sedsoftware.blinktracker.components.statistic.model.CustomChartEntry
import com.sedsoftware.blinktracker.components.statistic.model.DisplayedPeriod
import com.sedsoftware.blinktracker.components.statistic.model.DisplayedStats
import com.sedsoftware.blinktracker.database.StatisticsRepository
import com.sedsoftware.blinktracker.database.model.BlinksRecordDbModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

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
        if (entries.isEmpty()) {
            _stats.emit(DisplayedStats.Empty)
            return
        }

        val stats = generateStatsBundle(entries, period)
        val mapped = DisplayedStats.Content(
            min = stats.minOf { it.blinksForPeriod },
            max = stats.maxOf { it.blinksForPeriod },
            average = stats.sumOf { it.blinksForPeriod } / stats.size,
            period = period,
            records = stats.mapIndexed { index, record ->
                CustomChartEntry(
                    label = record.label,
                    x = index.toFloat(),
                    y = record.blinksForPeriod,
                )
            }
        )

        _stats.emit(mapped)
    }

    private fun generateStatsBundle(entries: List<BlinksRecordDbModel>, period: DisplayedPeriod): List<PeriodStatsBundle> {
        val result = mutableListOf<PeriodStatsBundle>()
        when (period) {
            DisplayedPeriod.MINUTE -> {
                entries.takeLast(DisplayedPeriod.MINUTE.takeLast).forEach { entry ->
                    result.add(
                        PeriodStatsBundle(
                            blinksForPeriod = entry.blinks.toFloat(),
                            label = entry.date.time.toString().substringBeforeLast(":")
                        )
                    )
                }
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

        return result
    }
}

