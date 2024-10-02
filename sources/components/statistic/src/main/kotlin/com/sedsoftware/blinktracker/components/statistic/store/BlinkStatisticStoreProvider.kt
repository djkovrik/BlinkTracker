@file:OptIn(ExperimentalMviKotlinApi::class)

package com.sedsoftware.blinktracker.components.statistic.store

import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.core.utils.ExperimentalMviKotlinApi
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutorScope
import com.arkivanov.mvikotlin.extensions.coroutines.coroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.coroutineExecutorFactory
import com.sedsoftware.blinktracker.components.statistic.integration.StatisticsManager
import com.sedsoftware.blinktracker.components.statistic.model.DisplayedPeriod
import com.sedsoftware.blinktracker.components.statistic.model.DisplayedStats
import com.sedsoftware.blinktracker.components.statistic.store.BlinkStatisticStore.Intent
import com.sedsoftware.blinktracker.components.statistic.store.BlinkStatisticStore.Label
import com.sedsoftware.blinktracker.components.statistic.store.BlinkStatisticStore.Label.ErrorCaught
import com.sedsoftware.blinktracker.components.statistic.store.BlinkStatisticStore.State
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

internal class BlinkStatisticStoreProvider(
    private val storeFactory: StoreFactory,
    private val manager: StatisticsManager,
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
                        manager.stats
                            .onEach { dispatch(Msg.StatsUpdated(it)) }
                            .launchIn(this)
                    }
                }

                onIntent<Intent.OnNewBlink> {
                    launch(getExceptionHandler(this)) {
                        manager.saveBlinks(it.value)
                    }
                }

                onIntent<Intent.OnNewPeriod> {
                    launch(getExceptionHandler(this)) {
                        dispatch(Msg.PeriodUpdated(it.value))
                        manager.switchPeriod(it.value)
                    }
                }
            },
            reducer = { msg ->
                when (msg) {
                    is Msg.PeriodUpdated -> copy(
                        period = msg.newPeriod
                    )

                    is Msg.StatsUpdated -> copy(
                        stats = msg.newStats
                    )
                }
            }
        ) {}

    private sealed interface Action {
        data object ObserveStats : Action
    }

    private sealed interface Msg {
        data class PeriodUpdated(val newPeriod: DisplayedPeriod) : Msg
        data class StatsUpdated(val newStats: DisplayedStats) : Msg
    }

    private fun getExceptionHandler(scope: CoroutineExecutorScope<State, Msg, Action, Label>): CoroutineExceptionHandler =
        CoroutineExceptionHandler { _, throwable ->
            scope.publish(ErrorCaught(throwable))
        }
}
