package com.sedsoftware.blinktracker.components.statistic.store

import com.arkivanov.mvikotlin.core.store.Store
import com.sedsoftware.blinktracker.components.statistic.model.DisplayedPeriod
import com.sedsoftware.blinktracker.components.statistic.model.DisplayedStats
import com.sedsoftware.blinktracker.components.statistic.store.BlinkStatisticStore.Intent
import com.sedsoftware.blinktracker.components.statistic.store.BlinkStatisticStore.Label
import com.sedsoftware.blinktracker.components.statistic.store.BlinkStatisticStore.State

internal interface BlinkStatisticStore : Store<Intent, State, Label> {

    sealed class Intent {
        data class OnNewBlink(val value: Int) : Intent()
        data class OnNewPeriod(val value: DisplayedPeriod) : Intent()
    }

    data class State(
        val stats: DisplayedStats = DisplayedStats.Loading
    )

    sealed class Label {
        data class ErrorCaught(val throwable: Throwable) : Label()
    }
}
