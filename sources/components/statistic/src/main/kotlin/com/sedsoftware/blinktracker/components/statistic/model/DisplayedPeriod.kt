package com.sedsoftware.blinktracker.components.statistic.model

enum class DisplayedPeriod(val takeLast: Int) {
    MINUTE(60), QUARTER_HOUR(30), HOUR(24), DAY(30), MONTH(12);
}
