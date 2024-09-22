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
package com.savvasdalkitsis.uhuruphotos.foundation.dismiss.api.ui

import androidx.compose.animation.core.animate
import androidx.compose.foundation.MutatorMutex
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.math.MathUtils.clamp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun rememberPullToDismissState(
    onDismiss: () -> Unit,
    dismissThreshold: Dp = PullToDismissDefaults.DismissThreshold,
): PullToDismissState {
    require(dismissThreshold > 0.dp) { "The dismiss trigger must be greater than zero!" }

    var dismissing by remember {
        mutableStateOf(false)
    }
    val scope = rememberCoroutineScope()
    val onDismissState = rememberUpdatedState {
        dismissing = true
        onDismiss()
    }
    val thresholdPx: Float

    with(LocalDensity.current) {
        thresholdPx = dismissThreshold.toPx()
    }

    val state = remember(scope) {
        PullToDismissState(scope, onDismissState, thresholdPx, dismissThreshold)
    }

    SideEffect {
        state.setDismissing(dismissing)
    }

    return state
}

class PullToDismissState internal constructor(
    private val animationScope: CoroutineScope,
    private val onDismissState: State<() -> Unit>,
    threshold: Float,
    dismissOffsetDp: Dp
) {
    val progress get() = clamp(adjustedDistancePulled / thresholdPx, 0f, 1f)
    val postDismissProgress get() = _postDismissProgress

    internal val dismissing get() = _dismissing
    internal val thresholdPx get() = _thresholdPx
    val thresholdDp get() = _thresholdDp

    private val adjustedDistancePulled by derivedStateOf { distancePulled * DragMultiplier }

    private var _postDismissProgress by mutableFloatStateOf(0f)
    private var _dismissing by mutableStateOf(false)
    private var distancePulled by mutableFloatStateOf(0f)
    private var _thresholdPx by mutableFloatStateOf(threshold)
    private var _thresholdDp by mutableStateOf(dismissOffsetDp)

    internal fun onPull(pullDelta: Float): Float {
        if (_dismissing) return 0f // Already dismissing, do nothing.

        val newOffset = (distancePulled + pullDelta).coerceAtLeast(0f)
        val dragConsumed = newOffset - distancePulled
        distancePulled = newOffset
        return dragConsumed
    }

    internal fun onRelease(velocity: Float): Float {
        if (dismissing) return 0f // Already dismissing, do nothing

        if (adjustedDistancePulled > thresholdPx) {
            animationScope.launch {
                mutatorMutex.mutate {
                    animate(0f, 1f) { value, _ ->
                        _postDismissProgress = value
                    }
                }
                onDismissState.value()
            }
            return 0f
        } else {
            val consumed = when {
                distancePulled == 0f -> 0f
                velocity < 0f -> 0f
                else -> velocity
            }
            resetDistancePulled()
            return consumed
        }
    }

    internal fun setDismissing(dismissing: Boolean) {
        if (_dismissing != dismissing) {
            _dismissing = dismissing
            distancePulled = 0f
        }
    }

    private val mutatorMutex = MutatorMutex()

    private fun resetDistancePulled() = animationScope.launch {
        mutatorMutex.mutate {
            animate(initialValue = distancePulled, targetValue = 0f) { value, _ ->
                distancePulled = value
            }
        }
    }
}

object PullToDismissDefaults {
    val DismissThreshold = 36.dp
}

private const val DragMultiplier = 0.5f
