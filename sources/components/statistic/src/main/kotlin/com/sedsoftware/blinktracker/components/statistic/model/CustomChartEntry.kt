package com.sedsoftware.blinktracker.components.statistic.model

import com.patrykandpatrick.vico.core.entry.ChartEntry

class CustomChartEntry(
    val label: String,
    override val x: Float,
    override val y: Float,
) : ChartEntry {
    override fun withY(y: Float) = CustomChartEntry(label, x, y)
}
/*
fun StatRecord.toChartEntry(index: Int): ChartEntry =
    CustomChartEntry(
        label = dateTime.time.toString().substringBeforeLast(":"),
        x = index.toFloat(),
        y = blinks.toFloat(),
    )


fun List<StatRecord>.toChartEntries(): List<ChartEntry> =
    mapIndexed { index, record -> record.toChartEntry(index) }
*/
