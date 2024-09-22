/*
Copyright 2022 Savvas Dalkitsis

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
package com.savvasdalkitsis.uhuruphotos.feature.collage.view.api.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.scale
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChanged
import androidx.compose.ui.util.fastAny
import androidx.compose.ui.util.fastForEach
import com.savvasdalkitsis.uhuruphotos.feature.collage.view.api.ui.state.CollageDisplayState
import kotlinx.coroutines.launch
import kotlin.math.max
import kotlin.math.min

private const val ZOOM_MIN = 0.8f
private const val ZOOM_MAX = 1.2f
private const val ZOOM_LOW_TRIGGER = 0.9f
private const val ZOOM_HIGH_TRIGGER = 1.1f

internal fun Modifier.pinchToChange(
    collageDisplayState: CollageDisplayState,
    onChangeDisplay: (CollageDisplayState) -> Unit,
) = composed {
    val coroutineScope = rememberCoroutineScope()
    val zoom = remember { Animatable(1f) }

    pointerInput(collageDisplayState) {
        awaitEachGesture {
            awaitFirstDown(requireUnconsumed = false)
            do {
                val event = awaitPointerEvent()
                if (event.changes.size > 1) {
                    val zoomChange = event.calculateZoom()
                    coroutineScope.launch {
                        zoom.snapTo(
                            max(
                                ZOOM_MIN,
                                min(zoom.value * zoomChange, ZOOM_MAX)
                            )
                        )
                    }

                    event.changes.fastForEach {
                        if (it.positionChanged()) {
                            it.consume()
                        }
                    }
                }
            } while (event.changes.fastAny { it.pressed })
            if (zoom.value < ZOOM_LOW_TRIGGER) {
                onChangeDisplay(collageDisplayState.zoomOut)
            }
            if (zoom.value > ZOOM_HIGH_TRIGGER) {
                onChangeDisplay(collageDisplayState.zoomIn)
            }
            coroutineScope.launch {
                zoom.animateTo(1f)
            }
        }
    }
        .scale(zoom.value)
}
