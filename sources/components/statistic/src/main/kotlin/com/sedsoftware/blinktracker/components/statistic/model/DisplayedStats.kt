package com.sedsoftware.blinktracker.components.statistic.model

import com.patrykandpatrick.vico.core.entry.ChartEntry

sealed class DisplayedStats {
    object Empty : DisplayedStats()

    object Loading : DisplayedStats()

    data class Content(
        val min: Float,
        val max: Float,
        val average: Float,
        val records: List<ChartEntry>,
        val period: DisplayedPeriod,
    ) : DisplayedStats()
}
