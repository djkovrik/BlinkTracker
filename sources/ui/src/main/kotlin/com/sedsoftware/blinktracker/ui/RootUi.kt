package com.sedsoftware.blinktracker.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.stack.animation.fade
import com.arkivanov.decompose.extensions.compose.stack.animation.plus
import com.arkivanov.decompose.extensions.compose.stack.animation.scale
import com.arkivanov.decompose.extensions.compose.stack.animation.stackAnimation
import androidx.compose.foundation.layout.systemBarsPadding
import com.sedsoftware.blinktracker.root.BlinkRoot
import com.sedsoftware.blinktracker.ui.camera.core.VisionImageProcessor

@Composable
fun BlinkRootContent(
    component: BlinkRoot,
    processor: VisionImageProcessor,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .systemBarsPadding()
    ) {
        Children(
            stack = component.childStack,
            animation = stackAnimation(fade() + scale()),
        ) {
            when (val child = it.instance) {
                is BlinkRoot.Child.Home ->
                    BlinkHomeContent(
                        component = child.component,
                        processor = processor,
                        onPreferencesClicked = component::openPreferencesScreen,
                    )

                is BlinkRoot.Child.Preferences ->
                    BlinkPreferencesContent(
                        component = child.component,
                        onBackClicked = component::closePreferencesScreen,
                    )
            }
        }
    }
}
