package com.sedsoftware.blinktracker.ui.extension

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.sedsoftware.blinktracker.components.statistic.model.DisplayedPeriod
import com.sedsoftware.blinktracker.ui.R

@Composable
fun DisplayedPeriod.asString(): String =
    when (this) {
        DisplayedPeriod.MINUTE -> stringResource(R.string.m1)
        DisplayedPeriod.QUARTER_HOUR -> stringResource(R.string.m15)
        DisplayedPeriod.HOUR -> stringResource(R.string.h1)
        DisplayedPeriod.DAY -> stringResource(R.string.d1)
        DisplayedPeriod.MONTH -> stringResource(R.string.d30)
    }
