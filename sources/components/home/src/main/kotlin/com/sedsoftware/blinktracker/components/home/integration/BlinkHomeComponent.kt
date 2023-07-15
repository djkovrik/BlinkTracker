package com.sedsoftware.blinktracker.components.home.integration

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.childContext
import com.arkivanov.essenty.lifecycle.doOnDestroy
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.sedsoftware.blinktracker.components.camera.BlinkCamera
import com.sedsoftware.blinktracker.components.camera.integration.BlinkCameraComponent
import com.sedsoftware.blinktracker.components.home.BlinkHome
import com.sedsoftware.blinktracker.components.statistic.BlinkStatistic
import com.sedsoftware.blinktracker.components.statistic.integration.BlinkStatisticComponent
import com.sedsoftware.blinktracker.components.tracker.BlinkTracker
import com.sedsoftware.blinktracker.components.tracker.integration.BlinkTrackerComponent
import com.sedsoftware.blinktracker.components.tracker.tools.MinimizedLauncher
import com.sedsoftware.blinktracker.components.tracker.tools.PictureInPictureLauncher
import com.sedsoftware.blinktracker.database.StatisticsRepository
import com.sedsoftware.blinktracker.settings.Settings
import java.lang.ref.WeakReference

class BlinkHomeComponent internal constructor(
    componentContext: ComponentContext,
    override val errorHandler: ErrorHandler,
    override val notificationsManager: NotificationsManager,
    private val blinkCamera: (ComponentContext) -> BlinkCamera,
    private val blinkTracker: (ComponentContext, (BlinkTracker.Output) -> Unit) -> BlinkTracker,
    private val blinkStatistic: (ComponentContext, (BlinkStatistic.Output) -> Unit) -> BlinkStatistic,
) : BlinkHome, ComponentContext by componentContext {

    constructor(
        componentContext: ComponentContext,
        storeFactory: StoreFactory,
        errorHandler: ErrorHandler,
        notificationsManager: NotificationsManager,
        settings: Settings,
        repo: StatisticsRepository,
        pipLauncher: PictureInPictureLauncher,
        minimizedLauncher: MinimizedLauncher,
    ) : this(
        componentContext = componentContext,
        errorHandler = errorHandler,
        notificationsManager = notificationsManager,
        blinkCamera = { childContext: ComponentContext ->
            BlinkCameraComponent(
                componentContext = childContext,
                storeFactory = storeFactory,
            )
        },
        blinkTracker = { childContext: ComponentContext, output: (BlinkTracker.Output) -> Unit ->
            BlinkTrackerComponent(
                componentContext = childContext,
                storeFactory = storeFactory,
                settings = settings,
                pipLauncher = WeakReference(pipLauncher),
                minimizedLauncher = minimizedLauncher,
                output = output,
            )
        },
        blinkStatistic = { childContext: ComponentContext, output: (BlinkStatistic.Output) -> Unit ->
            BlinkStatisticComponent(
                componentContext = childContext,
                storeFactory = storeFactory,
                repo = repo,
                output = output,
            )
        },
    )

    init {
        lifecycle.doOnDestroy {
            notificationsManager.clearNotification()
        }
    }

    override val cameraComponent: BlinkCamera =
        blinkCamera(componentContext.childContext(key = COMPONENT_CAMERA))

    override val trackerComponent: BlinkTracker =
        blinkTracker(componentContext.childContext(key = COMPONENT_TRACKER), ::onTrackerOutput)

    override val statsComponent: BlinkStatistic =
        blinkStatistic(componentContext.childContext(key = COMPONENT_STATISTIC), ::onStatisticOutput)

    private fun onTrackerOutput(output: BlinkTracker.Output) {
        when (output) {
            is BlinkTracker.Output.SoundNotificationTriggered ->
                notificationsManager.notifyWithSound()

            is BlinkTracker.Output.VibroNotificationTriggered ->
                notificationsManager.notifyWithVibro()

            is BlinkTracker.Output.ErrorCaught ->
                errorHandler.consume(output.throwable)

            is BlinkTracker.Output.BlinkedPerMinute ->
                statsComponent.onNewBlinksValue(output.value)

            is BlinkTracker.Output.NotificationDataChanged ->
                notificationsManager.showTrackingNotification(output.active, output.timer, output.blinks)
        }
    }

    private fun onStatisticOutput(output: BlinkStatistic.Output) {
        when (output) {
            is BlinkStatistic.Output.ErrorCaught ->
                errorHandler.consume(output.throwable)
        }
    }

    private companion object {
        const val COMPONENT_CAMERA = "camera"
        const val COMPONENT_TRACKER = "tracker"
        const val COMPONENT_STATISTIC = "statistic"
    }
}
