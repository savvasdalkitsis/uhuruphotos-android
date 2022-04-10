package com.savvasdalkitsis.librephotos.ui.view.zoom

import androidx.compose.animation.splineBasedDecay
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.*
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.util.fastAny
import androidx.compose.ui.util.fastForEach
import kotlin.math.abs

@ExperimentalFoundationApi
fun Modifier.zoomable(
    maxZoom: Float = 8f,
    onTap: () -> Unit = {},
) = composed {

    val coroutineScope = rememberCoroutineScope()
    val zoomableState = rememberZoomableState(coroutineScope = coroutineScope)
    var dragOffset by remember { mutableStateOf(Offset.Zero) }
    var composableCenter by remember { mutableStateOf(Offset.Zero) }

    this
        .pointerInput("taps") {
            detectTapGestures(
                onTap = { onTap() },
                onDoubleTap = {
                    if (zoomableState.scale != 1f) {
                        zoomableState.animateOffsetBy(-zoomableState.offset)
                        zoomableState.animateScaleTo(1f)
                    } else {
                        zoomableState.animateZoomTo(it, composableCenter, maxZoom / 2)
                    }
                }
            )
        }
        .pointerInput("gestures") {
            forEachGesture {
                val decay = splineBasedDecay<Float>(this)
                val velocityTracker = VelocityTracker()
                awaitPointerEventScope {
                    var zoom = 1f
                    var pan = Offset.Zero
                    var pastTouchSlop = false
                    val touchSlop = viewConfiguration.touchSlop
                    var drag: PointerInputChange?
                    var overSlop = Offset.Zero

                    val down = awaitFirstDown(requireUnconsumed = false)

                    velocityTracker.resetTracking()
                    zoomableState.stopAnimations()

                    var transformEventCounter = 0
                    do {
                        val event = awaitPointerEvent()
                        val canceled = event.changes.fastAny { it.positionChangeConsumed() }
                        var relevant = true
                        if (event.changes.size > 1) {
                            if (!canceled) {
                                var zoomChange = event.calculateZoom()
                                if (zoomableState.scale * zoomChange > maxZoom) {
                                    zoomChange = maxZoom / zoomableState.scale
                                }

                                if (zoomableState.scale * zoomChange < 0.6f) {
                                    zoomChange = 0.6f / zoomableState.scale
                                }

                                val panChange = event.calculatePan()

                                if (!pastTouchSlop) {
                                    zoom *= zoomChange
                                    pan += panChange

                                    val centroidSize =
                                        event.calculateCentroidSize(useCurrent = false)
                                    val zoomMotion = abs(1 - zoom) * centroidSize
                                    val panMotion = pan.getDistance()

                                    if (zoomMotion > touchSlop ||
                                        panMotion > touchSlop
                                    ) {
                                        pastTouchSlop = true
                                    }
                                }

                                if (pastTouchSlop) {
                                    val eventCentroid =
                                        event.calculateCentroid(useCurrent = false)
                                    if (zoomChange != 1f ||
                                        panChange != Offset.Zero
                                    ) {
                                        zoomableState.transform(
                                            composableCenter,
                                            eventCentroid,
                                            panChange,
                                            zoomChange
                                        )
                                    }
                                    val lastChange = event.changes.maxByOrNull { it.uptimeMillis }!!

                                    velocityTracker.addPosition(
                                        lastChange.uptimeMillis,
                                        lastChange.position
                                    )
                                    event.changes.fastForEach {
                                        if (it.positionChanged()) {
                                            it.consumeAllChanges()
                                        }
                                    }
                                }
                            }
                        } else if (transformEventCounter > 3) relevant = false
                        transformEventCounter++
                    } while (!canceled && event.changes.fastAny { it.pressed } && relevant)

                    if (zoomableState.scale < 1f) {
                        zoomableState.animateScaleTo(1f)
                        zoomableState.animateOffsetTo(0f, 0f)
                    }

                    do {
                        awaitPointerEvent()
                        drag = awaitTouchSlopOrCancellation(down.id) { change, over ->
                            change.consumePositionChange()
                            overSlop = over
                        }
                    } while (drag != null && !drag.positionChangeConsumed())
                    if (drag != null) {
                        dragOffset = Offset.Zero
                        if (zoomableState.scale !in 0.92f..1.08f) {
                            zoomableState.offset += overSlop
                        } else {
                            dragOffset += overSlop
                        }
                        drag(drag.id) {
                            val positionChange = it.positionChange()
                            if (zoomableState.scale !in 0.92f..1.08f) {
                                zoomableState.offset += positionChange
                            } else {
                                dragOffset += positionChange
                            }
                            velocityTracker.addPosition(
                                it.uptimeMillis,
                                it.position
                            )
                            it.consumePositionChange()
                        }
                    }
                    val velocity = velocityTracker.calculateVelocity()
                    if (zoomableState.scale > 1f) {
                        zoomableState.fling(velocity, decay)
                    }
                }
            }
        }
        .clip(RectangleShape)
        .graphicsLayer(
            translationX = zoomableState.offset.x,
            translationY = zoomableState.offset.y,
            scaleX = zoomableState.scale,
            scaleY = zoomableState.scale,
        )
        .onGloballyPositioned { coordinates ->
            val localOffset =
                Offset(
                    coordinates.size.width.toFloat() / 2,
                    coordinates.size.height.toFloat() / 2
                )
            val windowOffset = coordinates.localToWindow(localOffset)
            composableCenter = try {
                coordinates.parentLayoutCoordinates?.windowToLocal(windowOffset)
                    ?: Offset.Zero
            } catch (e: Throwable) {
                Offset.Zero
            }
        }
}