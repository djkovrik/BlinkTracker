@file:OptIn(ExperimentalMviKotlinApi::class)

package com.sedsoftware.blinktracker.components.statistic.store

import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.core.utils.ExperimentalMviKotlinApi
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutorScope
import com.arkivanov.mvikotlin.extensions.coroutines.coroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.coroutineExecutorFactory
import com.sedsoftware.blinktracker.components.statistic.store.BlinkStatisticStore.Intent
import com.sedsoftware.blinktracker.components.statistic.store.BlinkStatisticStore.Label
import com.sedsoftware.blinktracker.components.statistic.store.BlinkStatisticStore.Label.ErrorCaught
import com.sedsoftware.blinktracker.components.statistic.store.BlinkStatisticStore.State
import com.sedsoftware.blinktracker.database.StatisticsRepository
import com.sedsoftware.blinktracker.database.model.StatRecord
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch

internal class BlinkStatisticStoreProvider(
    private val storeFactory: StoreFactory,
    private val repo: StatisticsRepository,
) {

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
                            val rate: Float = if (items.isNotEmpty()) {
                                items.sumOf { it.blinks }.toFloat() / items.size
                            } else {
                                0f
                            }

                            dispatch(Msg.RecordsUpdated(items))
                            dispatch(Msg.AverageRateChanged(rate))
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
                        records = msg.records.takeLast(MAX_DISPLAYED_RECORDS),
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

    private fun getExceptionHandler(scope: CoroutineExecutorScope<State, Msg, Label>): CoroutineExceptionHandler =
        CoroutineExceptionHandler { _, throwable ->
            scope.publish(ErrorCaught(throwable))
        }

    private companion object {
        const val MAX_DISPLAYED_RECORDS = 25
    }
}
