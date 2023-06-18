package com.sedsoftware.blinktracker.root.integration

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.childContext
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.sedsoftware.blinktracker.components.camera.BlinkCamera
import com.sedsoftware.blinktracker.components.camera.integration.BlinkCameraComponent
import com.sedsoftware.blinktracker.components.preferences.BlinkPreferences
import com.sedsoftware.blinktracker.components.preferences.integration.BlinkPreferencesComponent
import com.sedsoftware.blinktracker.components.tracker.BlinkTracker
import com.sedsoftware.blinktracker.components.tracker.integration.BlinkTrackerComponent
import com.sedsoftware.blinktracker.root.BlinkRoot
import com.sedsoftware.blinktracker.settings.Settings

class BlinkRootComponent internal constructor(
    componentContext: ComponentContext,
    override val errorHandler: ErrorHandler,
    override val notificationsManager: NotificationsManager,
    private val blinkCamera: (ComponentContext) -> BlinkCamera,
    private val blinkPreferences: (ComponentContext, (BlinkPreferences.Output) -> Unit) -> BlinkPreferences,
    private val blinkTracker: (ComponentContext, (BlinkTracker.Output) -> Unit) -> BlinkTracker,
) : BlinkRoot, ComponentContext by componentContext {

    constructor(
        componentContext: ComponentContext,
        storeFactory: StoreFactory,
        errorHandler: ErrorHandler,
        notificationsManager: NotificationsManager,
        settings: Settings,
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
        blinkPreferences = { childContext: ComponentContext, output: (BlinkPreferences.Output) -> Unit ->
            BlinkPreferencesComponent(
                componentContext = childContext,
                storeFactory = storeFactory,
                settings = settings,
                output = output,
            )
        },
        blinkTracker = { childContext: ComponentContext, output: (BlinkTracker.Output) -> Unit ->
            BlinkTrackerComponent(
                componentContext = childContext,
                storeFactory = storeFactory,
                settings = settings,
                output = output,
            )
        }
    )

    override val cameraComponent: BlinkCamera =
        blinkCamera(componentContext.childContext(key = COMPONENT_CAMERA))

    override val preferencesComponent: BlinkPreferences =
        blinkPreferences(componentContext.childContext(key = COMPONENT_PREFERENCES), ::onPreferencesOutput)

    override val trackerComponent: BlinkTracker =
        blinkTracker(componentContext.childContext(key = COMPONENT_TRACKER), ::onTrackerOutput)

    private fun onPreferencesOutput(output: BlinkPreferences.Output) {
        when (output) {
            is BlinkPreferences.Output.ErrorCaught ->
                errorHandler.consume(output.throwable)
        }
    }

    private fun onTrackerOutput(output: BlinkTracker.Output) {
        when (output) {
            is BlinkTracker.Output.SoundNotificationTriggered ->
                notificationsManager.notifyWithSound()

            is BlinkTracker.Output.VibroNotificationTriggered ->
                notificationsManager.notifyWithVibro()

            is BlinkTracker.Output.ErrorCaught ->
                errorHandler.consume(output.throwable)

            is BlinkTracker.Output.BlinkedPerMinute -> TODO()
        }
    }

    private companion object {
        const val COMPONENT_CAMERA = "camera"
        const val COMPONENT_PREFERENCES = "preferences"
        const val COMPONENT_TRACKER = "tracker"
    }
}
