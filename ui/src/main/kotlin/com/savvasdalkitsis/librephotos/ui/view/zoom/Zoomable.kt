package com.savvasdalkitsis.librephotos.ui.view.zoom

import androidx.compose.animation.splineBasedDecay
import androidx.compose.foundation.gestures.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.input.pointer.positionChanged
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection.Ltr
import androidx.compose.ui.unit.LayoutDirection.Rtl
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastAny
import androidx.compose.ui.util.fastForEach
import kotlin.math.abs

fun Modifier.zoomable(
    maxZoom: Float = 8f,
    zoomableState: ZoomableState,
    onTap: () -> Unit = {},
    onSwipeAway: () -> Unit = {},
    onSwipeUp: () -> Boolean = { false },
    onSwipeToStart: () -> Boolean = {false},
) = composed {
    val density = LocalDensity.current
    var composableCenter by remember { mutableStateOf(Offset.Zero) }
    val layoutDirection = LocalLayoutDirection.current

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
        .pointerInput("gestures", layoutDirection) {
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
                        val canceled = event.changes.fastAny { it.isConsumed }
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
                                            it.consume()
                                        }
                                    }
                                }
                            }
                        } else if (transformEventCounter > 3) relevant = false
                        transformEventCounter++
                    } while (!canceled && event.changes.fastAny { it.pressed } && relevant)

                    if (zoomableState.scale < 1f) {
                        zoomableState.reset()
                    }

                    do {
                        awaitPointerEvent()
                        drag = awaitTouchSlopOrCancellation(down.id) { change, over ->
                            if (change.positionChange() != Offset.Zero) change.consume()
                            overSlop = over
                        }
                    } while (drag != null && !drag.isConsumed)
                    if (drag != null) {
                        zoomableState.offset += overSlop
                        if (zoomableState.scale in 0.92f..1.08f) {
                            zoomableState.animateScaleTo(0.6f)
                        }
                        drag(drag.id) {
                            val positionChange = it.positionChange()
                            zoomableState.offset += positionChange
                            velocityTracker.addPosition(
                                it.uptimeMillis,
                                it.position
                            )
                            if (it.positionChange() != Offset.Zero) it.consume()
                        }
                    }
                    val velocity = velocityTracker.calculateVelocity()
                    if (zoomableState.scale > 1f) {
                        zoomableState.fling(velocity, decay)
                    }
                    if (zoomableState.scale < 0.92) {
                        with(density) {
                            val swipeYOffset = zoomableState.offset.y.toDp()
                            val swipeXOffset = zoomableState.offset.x.toDp()
                            when {
                                swipeYOffset < (-24).dp && onSwipeUp() -> {}
                                layoutDirection == Rtl && swipeXOffset > 24.dp && onSwipeToStart() -> {}
                                layoutDirection == Ltr && swipeXOffset < (-24).dp && onSwipeToStart() -> {}
                                swipeYOffset > 24.dp -> onSwipeAway()
                                else -> zoomableState.reset()
                            }
                        }
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