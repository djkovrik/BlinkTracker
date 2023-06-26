package com.sedsoftware.blinktracker.root.integration

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.items
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.push
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.sedsoftware.blinktracker.components.camera.model.CameraLens
import com.sedsoftware.blinktracker.components.home.BlinkHome
import com.sedsoftware.blinktracker.components.home.integration.BlinkHomeComponent
import com.sedsoftware.blinktracker.components.home.integration.ErrorHandler
import com.sedsoftware.blinktracker.components.home.integration.NotificationsManager
import com.sedsoftware.blinktracker.components.preferences.BlinkPreferences
import com.sedsoftware.blinktracker.components.preferences.integration.BlinkPreferencesComponent
import com.sedsoftware.blinktracker.components.tracker.model.VisionFaceData
import com.sedsoftware.blinktracker.components.tracker.tools.PictureInPictureLauncher
import com.sedsoftware.blinktracker.database.StatisticsRepository
import com.sedsoftware.blinktracker.root.BlinkRoot
import com.sedsoftware.blinktracker.root.BlinkRoot.Child
import com.sedsoftware.blinktracker.settings.Settings
import kotlinx.parcelize.Parcelize

class BlinkRootComponent internal constructor(
    componentContext: ComponentContext,
    private val errorHandler: ErrorHandler,
    private val blinkPreferences: (ComponentContext, (BlinkPreferences.Output) -> Unit) -> BlinkPreferences,
    private val blinkHome: (ComponentContext) -> BlinkHome
) : BlinkRoot, ComponentContext by componentContext {

    constructor(
        componentContext: ComponentContext,
        storeFactory: StoreFactory,
        errorHandler: ErrorHandler,
        notificationsManager: NotificationsManager,
        settings: Settings,
        repo: StatisticsRepository,
        pipLauncher: PictureInPictureLauncher,
    ) : this(
        componentContext = componentContext,
        errorHandler = errorHandler,
        blinkPreferences = { childContext: ComponentContext, output: (BlinkPreferences.Output) -> Unit ->
            BlinkPreferencesComponent(
                componentContext = childContext,
                storeFactory = storeFactory,
                settings = settings,
                output = output,
            )
        },
        blinkHome = { childContext: ComponentContext ->
            BlinkHomeComponent(
                componentContext = childContext,
                storeFactory = storeFactory,
                errorHandler = errorHandler,
                notificationsManager = notificationsManager,
                settings = settings,
                repo = repo,
                pipLauncher = pipLauncher,
            )
        }
    )

    private val navigation: StackNavigation<Configuration> = StackNavigation()

    private val stack: Value<ChildStack<Configuration, Child>> =
        childStack(
            source = navigation,
            initialConfiguration = Configuration.Home,
            handleBackButton = true,
            childFactory = ::createChild
        )

    private val home: BlinkHome
        get() = findChild<Child.Home>().component

    override val childStack: Value<ChildStack<*, Child>> = stack

    override fun openPreferencesScreen() {
        navigation.push(Configuration.Preferences)
    }

    override fun closePreferencesScreen() {
        navigation.pop()
    }

    override fun onFaceDataChanged(data: VisionFaceData) {
        home.trackerComponent.onFaceDataChanged(data)
    }

    override fun onPictureInPictureChanged(enabled: Boolean) {
        home.trackerComponent.onPictureInPictureChanged(enabled)
    }

    override fun onPermissionGranted() {
        home.cameraComponent.onPermissionGranted()
    }

    override fun onPermissionDenied() {
        home.cameraComponent.onPermissionDenied()
    }

    override fun onPermissionRationale() {
        home.cameraComponent.onPermissionRationale()
    }

    override fun onCurrentLensChanged(lens: CameraLens) {
        home.cameraComponent.onCurrentLensChanged(lens)
    }

    private fun createChild(configuration: Configuration, componentContext: ComponentContext): Child =
        when (configuration) {
            is Configuration.Home -> Child.Home(blinkHome(componentContext))
            is Configuration.Preferences -> Child.Preferences(blinkPreferences(componentContext, ::onPreferencesOutput))
        }

    private fun onPreferencesOutput(output: BlinkPreferences.Output) {
        when (output) {
            is BlinkPreferences.Output.ErrorCaught ->
                errorHandler.consume(output.throwable)
        }
    }

    private inline fun <reified T : Child> findChild(): T =
        stack.items.find { it.instance is T }?.instance as? T
            ?: error("Failed to find child")

    private sealed interface Configuration : Parcelable {
        @Parcelize
        object Home : Configuration

        @Parcelize
        object Preferences : Configuration
    }
}
