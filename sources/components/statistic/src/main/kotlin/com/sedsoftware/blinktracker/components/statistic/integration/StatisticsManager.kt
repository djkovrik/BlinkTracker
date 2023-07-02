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
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import java.time.format.TextStyle
import java.util.Locale

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

    private val timeZone: TimeZone by lazy {
        TimeZone.currentSystemDefault()
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

        if (stats.isEmpty()) {
            _stats.emit(DisplayedStats.Empty)
            return
        }

        val mapped = DisplayedStats.Content(
            min = stats.minOf { it.averageForPeriod },
            max = stats.maxOf { it.averageForPeriod },
            average = stats.sumOf { it.averageForPeriod } / stats.size,
            records = stats.mapIndexed { index, record ->
                CustomChartEntry(
                    label = record.label,
                    x = index.toFloat(),
                    y = record.averageForPeriod,
                )
            }
        )

        _stats.emit(mapped)
    }

    private fun generateStatsBundle(entries: List<BlinksRecordDbModel>, period: DisplayedPeriod): List<PeriodStatsBundle> {
        return when (period) {
            DisplayedPeriod.MINUTE -> getPerMinuteStats(entries)
            DisplayedPeriod.QUARTER_HOUR -> getPerQuarterHourStats(entries)
            DisplayedPeriod.HOUR -> getPerHourStats(entries)
            DisplayedPeriod.DAY -> getPerDayStats(entries)
            DisplayedPeriod.MONTH -> getPerMonthStats(entries)
        }
    }

    private fun getPerMinuteStats(entries: List<BlinksRecordDbModel>): MutableList<PeriodStatsBundle> {
        val result = mutableListOf<PeriodStatsBundle>()
        entries.takeLast(DisplayedPeriod.MINUTE.takeLast).forEach { entry ->
            result.add(
                PeriodStatsBundle(
                    averageForPeriod = entry.blinks.toFloat(),
                    label = entry.date.time.toString().substringBeforeLast(":")
                )
            )
        }

        return result
    }

    private fun getPerQuarterHourStats(entries: List<BlinksRecordDbModel>): MutableList<PeriodStatsBundle> {
        val result = mutableListOf<PeriodStatsBundle>()
        val grouped = mutableListOf<List<BlinksRecordDbModel>>()
        val subgroup = mutableListOf<BlinksRecordDbModel>()
        var tempInstant = entries.first().date.toInstant(timeZone)

        entries.forEach { entry ->
            val entryInstant = entry.date.toInstant(timeZone)
            val diff = entryInstant - tempInstant
            if (diff.inWholeMinutes > PERIOD_FIFTEEN_MINUTES && subgroup.isNotEmpty()) {
                grouped.add(ArrayList(subgroup))
                subgroup.clear()
                tempInstant = entryInstant
            }

            subgroup.add(entry)
        }

        if (subgroup.isNotEmpty()) {
            grouped.add(ArrayList(subgroup))
        }

        grouped.takeLast(DisplayedPeriod.QUARTER_HOUR.takeLast).forEach { group ->
            result.add(
                PeriodStatsBundle(
                    group.sumOf { it.blinks.toFloat() } / group.size,
                    label = group.last().date.time.toString().substringBeforeLast(":")
                )
            )
        }

        return result
    }

    private fun getPerHourStats(entries: List<BlinksRecordDbModel>): MutableList<PeriodStatsBundle> {
        val result = mutableListOf<PeriodStatsBundle>()
        val grouped = mutableListOf<List<BlinksRecordDbModel>>()
        val subgroup = mutableListOf<BlinksRecordDbModel>()
        var tempInstant = entries.first().date.toInstant(timeZone)

        entries.forEach { entry ->
            val entryInstant = entry.date.toInstant(timeZone)
            val diff = entryInstant - tempInstant
            if (diff.inWholeMinutes > PERIOD_SIXTY_MINUTES && subgroup.isNotEmpty()) {
                grouped.add(ArrayList(subgroup))
                subgroup.clear()
                tempInstant = entryInstant
            }

            subgroup.add(entry)
        }

        if (subgroup.isNotEmpty()) {
            grouped.add(ArrayList(subgroup))
        }

        grouped.takeLast(DisplayedPeriod.HOUR.takeLast).forEach { group ->
            result.add(
                PeriodStatsBundle(
                    group.sumOf { it.blinks.toFloat() } / group.size,
                    label = group.last().date.time.toString().substringBeforeLast(":")
                )
            )
        }

        return result
    }

    private fun getPerDayStats(entries: List<BlinksRecordDbModel>): MutableList<PeriodStatsBundle> {
        val result = mutableListOf<PeriodStatsBundle>()
        val grouped = mutableListOf<List<BlinksRecordDbModel>>()
        val subgroup = mutableListOf<BlinksRecordDbModel>()
        var temp = entries.first()

        entries.forEach { entry ->
            if (entry.hasDifferentDay(temp)) {
                if (subgroup.isNotEmpty()) {
                    grouped.add(ArrayList(subgroup))
                }
                subgroup.clear()
                temp = entry
            }

            subgroup.add(entry)
        }

        if (subgroup.isNotEmpty()) {
            grouped.add(ArrayList(subgroup))
        }

        grouped.takeLast(DisplayedPeriod.DAY.takeLast).forEach { group ->
            result.add(
                PeriodStatsBundle(
                    group.sumOf { it.blinks.toFloat() } / group.size,
                    label = with(group.last().date) {
                        "${month.getDisplayName(TextStyle.SHORT, Locale.getDefault())} $dayOfMonth"
                    }
                )
            )
        }

        return result
    }

    private fun getPerMonthStats(entries: List<BlinksRecordDbModel>): MutableList<PeriodStatsBundle> {
        val result = mutableListOf<PeriodStatsBundle>()
        val grouped = mutableListOf<List<BlinksRecordDbModel>>()
        val subgroup = mutableListOf<BlinksRecordDbModel>()
        var temp = entries.first()

        entries.forEach { entry ->
            if (entry.hasDifferentMonth(temp)) {
                if (subgroup.isNotEmpty()) {
                    grouped.add(ArrayList(subgroup))
                }
                subgroup.clear()
                temp = entry
            }

            subgroup.add(entry)
        }

        if (subgroup.isNotEmpty()) {
            grouped.add(ArrayList(subgroup))
        }

        grouped.takeLast(DisplayedPeriod.MONTH.takeLast).forEach { group ->
            result.add(
                PeriodStatsBundle(
                    group.sumOf { it.blinks.toFloat() } / group.size,
                    label = with(group.last().date) {
                        month.getDisplayName(TextStyle.SHORT, Locale.getDefault())
                    }
                )
            )
        }

        return result
    }

    private fun BlinksRecordDbModel.hasDifferentDay(other: BlinksRecordDbModel): Boolean =
        (this.date.year != other.date.year) || (this.date.year == other.date.year && this.date.dayOfYear != other.date.dayOfYear)

    private fun BlinksRecordDbModel.hasDifferentMonth(other: BlinksRecordDbModel): Boolean =
        (this.date.year != other.date.year) || (this.date.year == other.date.year && this.date.monthNumber != other.date.monthNumber)

    private companion object {
        const val PERIOD_FIFTEEN_MINUTES = 15
        const val PERIOD_SIXTY_MINUTES = 60
    }
}

