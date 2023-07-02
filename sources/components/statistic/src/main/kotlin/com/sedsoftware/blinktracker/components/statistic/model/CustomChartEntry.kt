package com.sedsoftware.blinktracker.components.statistic.model

import com.patrykandpatrick.vico.core.entry.ChartEntry

class CustomChartEntry(
    val label: String,
    override val x: Float,
    override val y: Float,
) : ChartEntry {
    override fun withY(y: Float) = CustomChartEntry(label, x, y)
}
