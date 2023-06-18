@file:OptIn(ExperimentalMviKotlinApi::class)

package com.sedsoftware.blinktracker.components.statistic.store

import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.core.utils.ExperimentalMviKotlinApi
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutorScope
import com.arkivanov.mvikotlin.extensions.coroutines.coroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.coroutineExecutorFactory
import com.sedsoftware.blinktracker.components.statistic.model.StatRecord
import com.sedsoftware.blinktracker.components.statistic.model.getAverage
import com.sedsoftware.blinktracker.components.statistic.store.BlinkStatisticStore.Intent
import com.sedsoftware.blinktracker.components.statistic.store.BlinkStatisticStore.Label
import com.sedsoftware.blinktracker.components.statistic.store.BlinkStatisticStore.Label.ErrorCaught
import com.sedsoftware.blinktracker.components.statistic.store.BlinkStatisticStore.State
import com.sedsoftware.blinktracker.database.StatisticsRepository
import com.sedsoftware.blinktracker.database.model.BlinksRecordDbModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock.System
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.math.round

internal class BlinkStatisticStoreProvider(
    private val storeFactory: StoreFactory,
    private val repo: StatisticsRepository,
) {

    private val today: LocalDate by lazy {
        System.now().toLocalDateTime(timeZone = TimeZone.currentSystemDefault()).date
    }

    fun provide(): BlinkStatisticStore =
        object : BlinkStatisticStore, Store<Intent, State, Label> by storeFactory.create<Intent, Action, Msg, State, Label>(
            name = "BlinkStatisticStore",
            initialState = State(),
            bootstrapper = coroutineBootstrapper {
                dispatch(Action.ObserveStats)
            },
            executorFactory = coroutineExecutorFactory {
                onAction<Action.ObserveStats> {
                    launch(getExceptionHandler(this)) {
                        repo.observe().collect { items ->
                            val mapped = mapItems(items)
                            dispatch(Msg.RecordsUpdated(mapped))
                            dispatch(Msg.AverageRateChanged(mapped.getAverage().roundTo(AVERAGE_PRECISION)))
                        }
                    }
                }

                onIntent<Intent.HandleNewBlinkValue> {
                    launch(getExceptionHandler(this)) {
                        repo.insert(it.value)
                    }
                }
            },
            reducer = { msg ->
                when (msg) {
                    is Msg.RecordsUpdated -> copy(
                        records = msg.records.filter { it.dateTime.date == today },
                        statsChecked = true,
                        placeholderVisible = msg.records.isEmpty(),
                    )

                    is Msg.AverageRateChanged -> copy(
                        averageRate = msg.rate,
                    )
                }
            }
        ) {}

    private sealed interface Action {
        object ObserveStats : Action
    }

    private sealed interface Msg {
        data class RecordsUpdated(val records: List<StatRecord>) : Msg
        data class AverageRateChanged(val rate: Float) : Msg
    }

    private fun mapItems(items: List<BlinksRecordDbModel>): List<StatRecord> =
        items.map { item ->
            StatRecord(
                blinks = item.blinks,
                dateTime = item.date,
            )
        }

    private fun getExceptionHandler(scope: CoroutineExecutorScope<State, Msg, Label>): CoroutineExceptionHandler =
        CoroutineExceptionHandler { _, throwable ->
            scope.publish(ErrorCaught(throwable))
        }

    private fun Float.roundTo(decimals: Int): Float {
        var multiplier = 1.0f
        repeat(decimals) { multiplier *= 10f }
        return round(this * multiplier) / multiplier
    }

    private companion object {
        const val AVERAGE_PRECISION = 1
    }
}
