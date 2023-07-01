package com.sedsoftware.blinktracker.components.statistic.integration

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.lifecycle.doOnDestroy
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.extensions.coroutines.states
import com.sedsoftware.blinktracker.components.statistic.BlinkStatistic
import com.sedsoftware.blinktracker.components.statistic.BlinkStatistic.Model
import com.sedsoftware.blinktracker.components.statistic.store.BlinkStatisticStore
import com.sedsoftware.blinktracker.components.statistic.store.BlinkStatisticStoreProvider
import com.sedsoftware.blinktracker.database.StatisticsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

class BlinkStatisticComponent(
    private val componentContext: ComponentContext,
    private val storeFactory: StoreFactory,
    private val repo: StatisticsRepository,
    private val output: (BlinkStatistic.Output) -> Unit,
) : BlinkStatistic, ComponentContext by componentContext {

    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Main)

    private val store: BlinkStatisticStore =
        instanceKeeper.getStore {
            BlinkStatisticStoreProvider(
                storeFactory = storeFactory,
                manager = StatisticsManagerImpl(repo, scope),
            ).provide()
        }

    init {
        store.labels
            .onEach { label ->
                when (label) {
                    is BlinkStatisticStore.Label.ErrorCaught -> {
                        output(BlinkStatistic.Output.ErrorCaught(label.throwable))
                    }
                }
            }
            .launchIn(scope)

        lifecycle.doOnDestroy(scope::cancel)
    }

    override val models: Flow<Model> = store.states.map { stateToModel(it) }

    override val initial: Model = stateToModel(BlinkStatisticStore.State())

    override fun onNewBlinksValue(value: Int) {
        store.accept(BlinkStatisticStore.Intent.OnNewBlink(value))
    }
}
