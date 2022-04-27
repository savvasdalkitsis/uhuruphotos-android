package com.savvasdalkitsis.uhuruphotos.feed.view

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.foundation.gestures.forEachGesture
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.scale
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChanged
import androidx.compose.ui.util.fastAny
import androidx.compose.ui.util.fastForEach
import com.savvasdalkitsis.uhuruphotos.feed.view.state.FeedDisplay
import kotlinx.coroutines.launch
import kotlin.math.max
import kotlin.math.min

private const val zoomMin = 0.8f
private const val zoomMax = 1.2f
private const val zoomLowTrigger = 0.9f
private const val zoomHighTrigger = 1.1f

fun Modifier.pinchToChange(
    feedDisplay: FeedDisplay,
    onChangeDisplay: (FeedDisplay) -> Unit,
) = composed {
    val coroutineScope = rememberCoroutineScope()
    val zoom = remember { Animatable(1f) }

    pointerInput(feedDisplay) {
        forEachGesture {
            awaitPointerEventScope {
                awaitFirstDown(requireUnconsumed = false)
                do {
                    val event = awaitPointerEvent()
                    if (event.changes.size > 1) {
                        val zoomChange = event.calculateZoom()
                        coroutineScope.launch {
                            zoom.snapTo(
                                max(
                                    zoomMin,
                                    min(zoom.value * zoomChange, zoomMax)
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
                if (zoom.value < zoomLowTrigger) {
                    onChangeDisplay(feedDisplay.zoomOut)
                }
                if (zoom.value > zoomHighTrigger) {
                    onChangeDisplay(feedDisplay.zoomIn)
                }
                coroutineScope.launch {
                    zoom.animateTo(1f)
                }
            }
        }
    }
        .scale(zoom.value)
}
