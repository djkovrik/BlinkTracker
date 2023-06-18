package com.sedsoftware.blinktracker.ui.model

import com.patrykandpatrick.vico.core.entry.ChartEntry
import com.sedsoftware.blinktracker.components.statistic.model.StatRecord

class CustomChartEntry(
    val date: String,
    override val x: Float,
    override val y: Float,
) : ChartEntry {
    override fun withY(y: Float) = CustomChartEntry(date, x, y)
}

fun StatRecord.toChartEntry(index: Int): ChartEntry =
    CustomChartEntry(
        date = dateTime.time.toString().substringBeforeLast(":"),
        x = index.toFloat(),
        y = blinks.toFloat(),
    )


fun List<StatRecord>.toChartEntries(): List<ChartEntry> =
    mapIndexed { index, record -> record.toChartEntry(index) }
