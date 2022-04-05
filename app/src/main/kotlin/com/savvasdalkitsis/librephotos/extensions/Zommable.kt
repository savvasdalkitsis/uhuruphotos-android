package com.savvasdalkitsis.librephotos.extensions

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.splineBasedDecay
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.util.VelocityTracker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@ExperimentalFoundationApi
fun Modifier.zoomable(
    maxZoom: Float = 5f,
): Modifier = composed {
    val scale = remember { Animatable(1f) }
    val offsetX = remember { Animatable(0f) }
    val offsetY = remember { Animatable(0f) }
    var gesturing = remember { false }
    val coroutineScope: CoroutineScope = rememberCoroutineScope()
    this
        .combinedClickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
            onClick = { },
            onDoubleClick = {
                if (gesturing)
                    return@combinedClickable
                coroutineScope.launch {
                    val resetting = scale.value != 1f
                    val targetScale = if (resetting) 1f else maxZoom
                    launch {
                        scale.animateTo(targetScale)
                    }
                    launch {
                        offsetX.animateTo(0f)
                    }
                    launch {
                        offsetY.animateTo(0f)
                    }
                }
            },
        )
        .pointerInput(Unit) {
            val decay = splineBasedDecay<Float>(this)
            val velocityTracker = VelocityTracker()
            coroutineScope.launch {
                forEachGesture {
                    awaitPointerEventScope {
                        awaitFirstDown()
                        velocityTracker.resetTracking()
                        launch { offsetX.stop() }
                        launch { offsetY.stop() }
                        gesturing = true
                        do {
                            val event = awaitPointerEvent()
                            launch {
                                scale.snapTo(scale.value * event.calculateZoom())
                                val offset = event.calculatePan()
                                offsetX.snapTo(offsetX.value + offset.x)
                                offsetY.snapTo(offsetY.value + offset.y)
                                val lastChange = event.changes.maxByOrNull { it.uptimeMillis }!!
                                velocityTracker.addPosition(
                                    lastChange.uptimeMillis,
                                    lastChange.position
                                )
                                if (scale.value < 0.8) {
                                    scale.snapTo(0.8f)
                                }
                            }
                        } while (event.changes.any { it.pressed })
                        val velocity = velocityTracker.calculateVelocity()
                        launch {
                            offsetX.animateDecay(velocity.x, decay)
                        }
                        launch {
                            offsetY.animateDecay(velocity.y, decay)
                        }
                        if (scale.value <= 1f) {
                            launch {
                                scale.animateTo(1f)
                            }
                            launch {
                                offsetX.animateTo(0f)
                            }
                            launch {
                                offsetY.animateTo(0f)
                            }
                        }
                        launch {
                            delay(500)
                            gesturing = false
                        }
                    }
                }
            }
        }
        .graphicsLayer(
            scaleX = scale.value,
            scaleY = scale.value,
            translationX = offsetX.value,
            translationY = offsetY.value,
        )
}

