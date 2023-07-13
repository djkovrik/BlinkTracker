@file:OptIn(ExperimentalMviKotlinApi::class)

package com.sedsoftware.blinktracker.components.camera.store

import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.core.utils.ExperimentalMviKotlinApi
import com.arkivanov.mvikotlin.extensions.coroutines.coroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.coroutineExecutorFactory
import com.sedsoftware.blinktracker.components.camera.model.CameraLens
import com.sedsoftware.blinktracker.components.camera.model.CameraState
import com.sedsoftware.blinktracker.components.camera.model.PermissionStateCamera
import com.sedsoftware.blinktracker.components.camera.store.BlinkCameraStore.Intent
import com.sedsoftware.blinktracker.components.camera.store.BlinkCameraStore.Label
import com.sedsoftware.blinktracker.components.camera.store.BlinkCameraStore.State

internal class BlinkCameraStoreProvider(
    private val storeFactory: StoreFactory,
) {

    fun provide(): BlinkCameraStore =
        object : BlinkCameraStore, Store<Intent, State, Label> by storeFactory.create<Intent, Action, Msg, State, Label>(
            name = "BlinkCameraStore",
            initialState = State(),
            bootstrapper = coroutineBootstrapper { },
            executorFactory = coroutineExecutorFactory {
                onIntent<Intent.OnPermissionGrant> { dispatch(Msg.PermissionStateChanged(PermissionStateCamera.GRANTED)) }
                onIntent<Intent.OnPermissionDeny> { dispatch(Msg.PermissionStateChanged(PermissionStateCamera.DENIED)) }
                onIntent<Intent.OnPermissionRationale> { dispatch(Msg.PermissionStateChanged(PermissionStateCamera.RATIONALE)) }
                onIntent<Intent.OnLensChange> { dispatch(Msg.LensSelected(it.lens)) }
            },
            reducer = { msg ->
                when (msg) {
                    is Msg.PermissionStateChanged -> copy(
                        permissionState = msg.newState
                    )

                    is Msg.LensSelected -> copy(
                        lensFacing = msg.newLens,
                        cameraState = if (msg.newLens == CameraLens.FRONT) {
                            CameraState.DETECTED
                        } else {
                            CameraState.NOT_DETECTED
                        },
                    )

                }
            }
        ) {}

    private sealed interface Action

    private sealed interface Msg {
        data class PermissionStateChanged(val newState: PermissionStateCamera) : Msg
        data class LensSelected(val newLens: CameraLens) : Msg
    }
}
