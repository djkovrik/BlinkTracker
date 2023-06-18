package com.sedsoftware.blinktracker.components.statistic.store

import com.arkivanov.mvikotlin.core.store.Store
import com.sedsoftware.blinktracker.components.statistic.model.StatRecord
import com.sedsoftware.blinktracker.components.statistic.store.BlinkStatisticStore.Intent
import com.sedsoftware.blinktracker.components.statistic.store.BlinkStatisticStore.Label
import com.sedsoftware.blinktracker.components.statistic.store.BlinkStatisticStore.State

internal interface BlinkStatisticStore : Store<Intent, State, Label> {

    sealed class Intent {
        data class HandleNewBlinkValue(val value: Int) : Intent()
    }

    data class State(
        val records: List<StatRecord> = emptyList(),
        val averageRate: Float = 0.0f,
        val statsChecked: Boolean = false,
        val placeholderVisible: Boolean = false,
    )

    sealed class Label {
        data class ErrorCaught(val throwable: Throwable) : Label()
    }
}
