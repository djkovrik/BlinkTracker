package com.sedsoftware.blinktracker.camera.core

import androidx.camera.core.ImageProxy

interface VisionImageProcessor {
    fun process(image: ImageProxy)
    fun stop()
}
