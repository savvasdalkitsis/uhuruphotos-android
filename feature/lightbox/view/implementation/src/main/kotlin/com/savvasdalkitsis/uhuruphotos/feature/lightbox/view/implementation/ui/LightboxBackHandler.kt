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
package com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.ui

import androidx.activity.BackEventCompat
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.SnapSpec
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.actions.LightboxAction
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.actions.UpPressed
import kotlinx.coroutines.launch
import me.saket.telephoto.zoomable.ZoomableState

@Composable
internal fun LightboxBackHandler(
    isTrulyActivePage: Boolean,
    showingActionsOverlay: Boolean,
    scrollState: ScrollState,
    zoomableState: ZoomableState,
    action: (LightboxAction) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val currentAction by rememberUpdatedState(action)

    val currentShowingActionsOverlay by rememberUpdatedState(showingActionsOverlay)
    val currentScrollValue by rememberUpdatedState(scrollState.value)
    val currentZoomFraction by rememberUpdatedState(zoomableState.zoomFraction ?: 0f)
    val currentZoomableState by rememberUpdatedState(zoomableState)

    var backGestureProgress by remember { mutableFloatStateOf(0f) }
    var peekingForScroll by remember { mutableStateOf(false) }
    var peekingForZoomReset by remember { mutableStateOf(false) }

    var initialScrollOnPeek by remember { mutableIntStateOf(0) }
    var initialScaleOnPeek by remember { mutableFloatStateOf(1f) } // This will store scaleX

    val density = LocalDensity.current
    val maxPeekScrollDp = 64.dp
    val maxPeekScrollPx = remember(density, maxPeekScrollDp) {
        with(density) { maxPeekScrollDp.toPx() }
    }

    val zoomAnimationSpec: AnimationSpec<Float> = ZoomableState.DefaultZoomAnimationSpec
    val scrollAnimationSpec = remember { tween<Float>(durationMillis = 250) }


    // --- Conditions for enabling the callback ---
    val currentScaleX = currentZoomableState.contentTransformation.scale.scaleX
    val canScrollBasedOnCurrentState = !currentShowingActionsOverlay && currentScrollValue > 0
    val canZoomResetBasedOnCurrentState = currentShowingActionsOverlay && (currentScaleX > 1.05f || currentZoomFraction > 0.05f)
    val isCallbackEnabledConditionMet =
        (canScrollBasedOnCurrentState || canZoomResetBasedOnCurrentState) && isTrulyActivePage

    val onBackPressedCallback = remember {
        object : OnBackPressedCallback(false) {
            override fun handleOnBackStarted(backEvent: BackEventCompat) {
                val latestShowingActionsOverlay = currentShowingActionsOverlay
                val latestScrollValue = currentScrollValue
                val latestScaleX = currentZoomableState.contentTransformation.scale.scaleX
                val latestZoomFraction = currentZoomableState.zoomFraction ?: 0f

                val shouldPeekForScroll = !latestShowingActionsOverlay && latestScrollValue > 0
                val shouldPeekForZoomReset = latestShowingActionsOverlay && (latestScaleX > 1.05f || latestZoomFraction > 0.05f)

                peekingForScroll = shouldPeekForScroll
                // Only allow one peek type. If scrolling is possible (overlay hidden), it usually takes precedence.
                peekingForZoomReset = if (!shouldPeekForScroll) shouldPeekForZoomReset else false

                if (peekingForScroll) {
                    initialScrollOnPeek = latestScrollValue
                } else if (peekingForZoomReset) {
                    initialScaleOnPeek = latestScaleX
                }
                backGestureProgress = backEvent.progress
            }

            override fun handleOnBackProgressed(backEvent: BackEventCompat) {
                backGestureProgress = backEvent.progress
            }

            override fun handleOnBackPressed() { // Commit gesture
                var eventConsumedByPeek = false

                if (peekingForScroll) {
                    coroutineScope.launch {
                        scrollState.animateScrollTo(0, animationSpec = scrollAnimationSpec)
                    }
                    eventConsumedByPeek = true
                } else if (peekingForZoomReset) {
                    coroutineScope.launch {
                        currentZoomableState.resetZoom(animationSpec = zoomAnimationSpec)
                    }
                    eventConsumedByPeek = true
                }

                if (!eventConsumedByPeek) {
                    // If no peek action was committed based on gesture start,
                    // re-evaluate conditions based on current state at gesture commit.
                    val latestShowingActionsOverlayNow = currentShowingActionsOverlay
                    val latestScrollValueNow = currentScrollValue
                    val latestScaleXNow = currentZoomableState.contentTransformation.scale.scaleX
                    val latestZoomFractionNow = currentZoomableState.zoomFraction ?: 0f

                    val canStillScrollNow = !latestShowingActionsOverlayNow && latestScrollValueNow > 0
                    val canStillZoomResetNow = latestShowingActionsOverlayNow && (latestScaleXNow > 1.05f || latestZoomFractionNow > 0.05f)

                    var performedFallbackAction = false
                    if (canStillScrollNow) {
                        coroutineScope.launch {
                            scrollState.animateScrollTo(0, animationSpec = scrollAnimationSpec)
                        }
                        performedFallbackAction = true
                    } else if (canStillZoomResetNow) {
                        coroutineScope.launch {
                            currentZoomableState.resetZoom(animationSpec = zoomAnimationSpec)
                        }
                        performedFallbackAction = true
                    }

                    if (!performedFallbackAction) {
                        // If truly nothing to do (scroll/zoom) based on current state either,
                        // then proceed with UpPressed.
                        this.isEnabled = false // Disable self before calling the action
                        currentAction(UpPressed)
                    }
                }

                // Reset peek states after decision
                peekingForScroll = false
                peekingForZoomReset = false
                backGestureProgress = 0f
            }

            override fun handleOnBackCancelled() { // Cancel gesture
                coroutineScope.launch {
                    if (peekingForScroll) {
                        scrollState.animateScrollTo(initialScrollOnPeek, animationSpec = scrollAnimationSpec)
                    } else if (peekingForZoomReset) {
                        currentZoomableState.zoomTo(
                            zoomFactor = initialScaleOnPeek,
                            animationSpec = zoomAnimationSpec
                        )
                    }
                }
                peekingForScroll = false
                peekingForZoomReset = false
                backGestureProgress = 0f
            }
        }
    }

    LaunchedEffect(backGestureProgress, peekingForScroll, peekingForZoomReset) {
        if (peekingForScroll) {
            val targetScroll = initialScrollOnPeek - (backGestureProgress * maxPeekScrollPx).toInt()
            scrollState.scrollTo(targetScroll.coerceIn(0, initialScrollOnPeek))
        } else if (peekingForZoomReset) {
            val baseTargetScale = 1f
            val targetScale = lerp(initialScaleOnPeek, baseTargetScale, backGestureProgress)
            val currentActualScaleX = currentZoomableState.contentTransformation.scale.scaleX

            if (currentActualScaleX != targetScale.coerceIn(baseTargetScale, initialScaleOnPeek)) { // Added coerceIn here for safety
                currentZoomableState.zoomTo(
                    zoomFactor = targetScale.coerceIn(baseTargetScale, initialScaleOnPeek),
                    animationSpec = SnapSpec()
                )
            }
        }
    }

    LaunchedEffect(isCallbackEnabledConditionMet) {
        onBackPressedCallback.isEnabled = isCallbackEnabledConditionMet
    }

    val dispatcherOwner = LocalOnBackPressedDispatcherOwner.current
    val lifecycleOwner = LocalLifecycleOwner.current // From androidx.lifecycle.compose.LocalLifecycleOwner

    DisposableEffect(dispatcherOwner, lifecycleOwner, onBackPressedCallback) {
        val dispatcher = dispatcherOwner?.onBackPressedDispatcher
        dispatcher?.addCallback(lifecycleOwner, onBackPressedCallback)
        onDispose {
            onBackPressedCallback.remove()
            peekingForScroll = false
            peekingForZoomReset = false
            backGestureProgress = 0f
        }
    }
}

private fun lerp(start: Float, stop: Float, fraction: Float): Float {
    return (1 - fraction) * start + fraction * stop
}