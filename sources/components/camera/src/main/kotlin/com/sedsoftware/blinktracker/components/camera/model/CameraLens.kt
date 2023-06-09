package com.sedsoftware.blinktracker.components.camera.model

enum class CameraLens(val value: Int) {
    FRONT(0), BACK(1), NOT_AVAILABLE(-1);
}

val CameraLens.isNotValid: Boolean
    get() = value == CameraLens.NOT_AVAILABLE.value
