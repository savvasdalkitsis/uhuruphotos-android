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
package com.savvasdalkitsis.uhuruphotos.ui.view.zoom

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.DecayAnimationSpec
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.Velocity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.math.atan
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class ZoomableState(
    private var animatedScale: Animatable<Float, AnimationVector1D>,
    private var offsetX: Animatable<Float, AnimationVector1D>,
    private var offsetY: Animatable<Float, AnimationVector1D>,
    val coroutineScope: CoroutineScope
) {

    var scale: Float
        get() = animatedScale.value
        set(value) {
            coroutineScope.launch { animatedScale.snapTo(value) }
        }

    fun animateScaleTo(target: Float) = coroutineScope.launch { animatedScale.animateTo(target) }

    var offset: Offset
        get() = Offset(offsetX.value, offsetY.value)
        set(value) {
            coroutineScope.launch { offsetX.snapTo(value.x) }
            coroutineScope.launch { offsetY.snapTo(value.y) }
        }

    fun animateOffsetTo(x: Float, y: Float) {
        coroutineScope.launch { offsetX.animateTo(x) }
        coroutineScope.launch { offsetY.animateTo(y) }
    }

    fun animateOffsetBy(by: Offset) {
        animateOffsetTo(offset.x + by.x, offset.y + by.y)
    }

    fun animateZoomTo(position: Offset, currentComposableCenter: Offset, zoomChange: Float) {
        val offsetBuffer = offset

        val x0 = position.x - currentComposableCenter.x
        val y0 = position.y - currentComposableCenter.y

        val hyp0 = sqrt(x0 * x0 + y0 * y0)
        val hyp1 = zoomChange * hyp0 * (if (x0 > 0) {
            1f
        } else {
            -1f
        })

        val alpha0 = atan(y0 / x0)

        val x1 = cos(alpha0) * hyp1
        val y1 = sin(alpha0) * hyp1

        val transformOffset =
            position - (currentComposableCenter - offsetBuffer) - Offset(x1, y1)

        animateScaleTo(3f)
        animateOffsetBy(transformOffset)
    }

    fun transform(
        composableCenter: Offset,
        centroid: Offset,
        pan: Offset,
        zoom: Float
    ) {
        val tempOffset = offset + pan

        val x0 = centroid.x - composableCenter.x
        val y0 = centroid.y - composableCenter.y

        val hyp0 = sqrt(x0 * x0 + y0 * y0)
        val hyp1 = zoom * hyp0 * (if (x0 > 0) {
            1f
        } else {
            -1f
        })

        val alpha = atan(y0 / x0)

        val x1 = cos(alpha) * hyp1
        val y1 = sin(alpha) * hyp1

        scale *= zoom
        offset = centroid - (composableCenter - tempOffset) - Offset(x1, y1)
    }

    fun stopAnimations() {
        coroutineScope.launch {
            offsetX.stop()
            offsetY.stop()
        }
    }

    fun fling(velocity: Velocity, decay: DecayAnimationSpec<Float>) {
        coroutineScope.launch {
            offsetX.animateDecay(velocity.x, decay)
        }
        coroutineScope.launch {
            offsetY.animateDecay(velocity.y, decay)
        }
    }

    fun reset() {
        animateScaleTo(1f)
        animateOffsetTo(0f, 0f)
    }
}

@Composable
fun rememberZoomableState(
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
): ZoomableState {
    val zoomR = remember { Animatable(1f) }
    val offsetX = remember { Animatable(0f) }
    val offsetY = remember { Animatable(0f) }
    return remember {
        ZoomableState(
            zoomR,
            offsetX,
            offsetY,
            coroutineScope
        )
    }
}