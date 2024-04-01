/*
Copyright 2024 Savvas Dalkitsis

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
package com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.shared.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.shared.LocalScreenshotState
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.shared.LocalSharedElementTransitionContentProvider
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.shared.LocalSharedElementTransitionProvider
import com.smarttoolfactory.screenshot.ScreenshotState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class SharedElementTransitionState(
    private val elementBounds: MutableState<Rect?>,
    private val scope: CoroutineScope,
    private val screenshotState: ScreenshotState,
    private val sharedElement: MutableState<Rect?>,
    private val sharedElementContent: MutableState<String?>,
    private val aspectRatio: Float,
    private val contentUrl: String,
) {

    internal fun trackPosition(layoutCoordinates: LayoutCoordinates) {
        elementBounds.value = layoutCoordinates
            .boundsInRoot()
            .let {
                when (it.top) {
                    // might be offscreen on top
                    0f -> it.copy(top = it.bottom - it.width / aspectRatio)
                    // might be offscreen on bottom
                    else -> it.copy(bottom = it.top + it.width / aspectRatio)
                }
            }
    }

    fun startElementTransition(action: () -> Unit) {
        scope.launch {
            screenshotState.capture()
            sharedElement.value = elementBounds.value
            sharedElementContent.value = contentUrl
            action()
        }
    }

    fun clearElementTransition() {
        screenshotState.bitmapState.value = null
        sharedElement.value = null
        sharedElementContent.value = null
    }
}

@Stable
fun Modifier.sharedElementTransition(
    sharedElementTransitionState: SharedElementTransitionState,
) = composed {
    onGloballyPositioned { layoutCoordinates ->
        sharedElementTransitionState.trackPosition(layoutCoordinates)
    }
}

@Composable
fun rememberSharedElementTransitionState(
    key: Any?,
    contentUrl: String,
    aspectRatio: Float
): SharedElementTransitionState {
    val screenshotState = LocalScreenshotState.current
    val elementBounds = remember(key, aspectRatio) {
        mutableStateOf<Rect?>(null)
    }
    val sharedElement = LocalSharedElementTransitionProvider.current
    val sharedElementContent = LocalSharedElementTransitionContentProvider.current
    val scope = rememberCoroutineScope()
    return remember {
        SharedElementTransitionState(
            elementBounds,
            scope,
            screenshotState,
            sharedElement,
            sharedElementContent,
            aspectRatio,
            contentUrl,
        )
    }
}
