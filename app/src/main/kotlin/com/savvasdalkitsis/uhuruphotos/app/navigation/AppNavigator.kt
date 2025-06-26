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
package com.savvasdalkitsis.uhuruphotos.app.navigation

import androidx.compose.animation.ContentTransform
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.navigation3.ui.NavDisplay
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.Navigator
import com.savvasdalkitsis.uhuruphotos.foundation.theme.api.window.LocalSystemUiController
import com.savvasdalkitsis.uhuruphotos.foundation.ui.implementation.usecase.UiUseCase
import javax.inject.Inject

class AppNavigator @Inject constructor(
    private val navigator: Navigator,
    private val uiUseCase: UiUseCase,
    private val compositionLocalProviders: CompositionLocalProviders,
) {

    @OptIn(ExperimentalSharedTransitionApi::class)
    @Composable
    fun NavigationTargets() {
        compositionLocalProviders.Provide {
            val currentKeyboardController = LocalSoftwareKeyboardController.current!!
            val currentSystemUiController = LocalSystemUiController.current
            val currentHapticFeedback = LocalHapticFeedback.current
            LaunchedEffect(currentKeyboardController, currentSystemUiController, currentHapticFeedback) {
                with(uiUseCase) {
                    keyboardController = currentKeyboardController
                    systemUiController = currentSystemUiController
                    haptics = currentHapticFeedback
                }
            }
            SharedTransitionLayout {
                NavDisplay(
                    backStack = navigator.backStack,
                    onBack = { navigator.navigateUp() },
                    entryProvider = { key ->
                        navigationTree(key)
                    },
                    // Apply the custom transitions
                    transitionSpec = {
                        ContentTransform(
                            targetContentEnter = customEnterTransition(),
                            initialContentExit = customExitTransition(),
                            // sizeTransform = null // Or your custom SizeTransform if needed
                        )
                    },
                    popTransitionSpec = {
                        ContentTransform (
                            targetContentEnter = customPopEnterTransition(),
                            initialContentExit = customPopExitTransition(),
                            // sizeTransform = null
                        )
                    },
                    // This is where you'd try to make predictive back feel better
                    // The transitions below are an attempt. Perfect finger tracking is hard.
                    predictivePopTransitionSpec = {
                        ContentTransform(
                            targetContentEnter = predictivePopEnterTransitionRefined(),
                            initialContentExit = predictivePopExitTransitionRefined(),
                            sizeTransform = null // Important to often set to null for predictive back
                            // to avoid size changes interfering with the gesture.
                            // Or use a hold
                        )
                    }
                )
            }
        }
    }
}

// Duration for the animations
private const val ANIMATION_DURATION_MS = 300
private const val PREDICTIVE_ANIMATION_DURATION_MS = 400 // Predictive can sometimes be slightly longer

@OptIn(ExperimentalAnimationApi::class)
fun customEnterTransition(): EnterTransition {
    return slideInHorizontally(
        initialOffsetX = { fullWidth -> fullWidth },
        animationSpec = tween(ANIMATION_DURATION_MS)
    ) + fadeIn(animationSpec = tween(ANIMATION_DURATION_MS))
}

@OptIn(ExperimentalAnimationApi::class)
fun customExitTransition(): ExitTransition {
    return slideOutHorizontally(
        targetOffsetX = { fullWidth -> -fullWidth },
        animationSpec = tween(ANIMATION_DURATION_MS)
    ) + fadeOut(animationSpec = tween(ANIMATION_DURATION_MS))
}

@OptIn(ExperimentalAnimationApi::class)
fun customPopEnterTransition(): EnterTransition {
    return slideInHorizontally(
        initialOffsetX = { fullWidth -> -fullWidth },
        animationSpec = tween(ANIMATION_DURATION_MS)
    ) + fadeIn(animationSpec = tween(ANIMATION_DURATION_MS))
    // Potentially scale in slightly if it feels right for the pop
    // + scaleIn(initialScale = 0.95f, animationSpec = tween(ANIMATION_DURATION_MS))
}

@OptIn(ExperimentalAnimationApi::class)
fun customPopExitTransition(): ExitTransition {
    return slideOutHorizontally(
        targetOffsetX = { fullWidth -> fullWidth },
        animationSpec = tween(ANIMATION_DURATION_MS)
    ) + fadeOut(animationSpec = tween(ANIMATION_DURATION_MS))+
            // Add a slight scale out for the screen being popped
            scaleOut(targetScale = 0.95f, animationSpec = tween(ANIMATION_DURATION_MS))
}


// Predictive Transitions (Attempting to get closer to system feel)
// These would ideally react more to the gesture itself, but with ContentTransform
// we are defining fixed animations that trigger.

@OptIn(ExperimentalAnimationApi::class)
fun predictivePopEnterTransition(): EnterTransition {
    // The screen coming into view as you swipe back
    return slideInHorizontally(
        initialOffsetX = { it / 2 }, // Start partway in, as if dragged by finger
        animationSpec = tween(PREDICTIVE_ANIMATION_DURATION_MS)
    ) + fadeIn(animationSpec = tween(PREDICTIVE_ANIMATION_DURATION_MS))
    // Optionally, scale it up from a slightly smaller state
    // + scaleIn(initialScale = 0.90f, animationSpec = tween(PREDICTIVE_ANIMATION_DURATION_MS))
}

@OptIn(ExperimentalAnimationApi::class)
fun predictivePopExitTransition(): ExitTransition {
    // The screen being swiped away
    return slideOutHorizontally(
        targetOffsetX = { it / 2 }, // Exit partway, as if dragged by finger
        animationSpec = tween(PREDICTIVE_ANIMATION_DURATION_MS)
    ) + fadeOut(animationSpec = tween(PREDICTIVE_ANIMATION_DURATION_MS))+
            // Scale down the exiting screen
            scaleOut(targetScale = 0.90f, animationSpec = tween(PREDICTIVE_ANIMATION_DURATION_MS))
}
// Duration for the animations
private const val PREDICTIVE_PEEK_DURATION_MS = 150 // Short duration for the initial "peek"
private const val PREDICTIVE_SETTLE_DURATION_MS = 250 // Duration for the rest of the animation

@OptIn(ExperimentalAnimationApi::class)
fun predictivePopEnterTransitionImproved(): EnterTransition {
    // Screen A (previous screen) coming into view
    return fadeIn(animationSpec = tween(PREDICTIVE_PEEK_DURATION_MS + PREDICTIVE_SETTLE_DURATION_MS)) +
            slideInHorizontally(
                initialOffsetX = { fullWidth -> -fullWidth / 4 }, // Start slightly on-screen
                animationSpec = tween(PREDICTIVE_PEEK_DURATION_MS + PREDICTIVE_SETTLE_DURATION_MS)
            ) +
            scaleIn( // Scale up from a slightly smaller state
                initialScale = 0.95f,
                animationSpec = tween(PREDICTIVE_PEEK_DURATION_MS + PREDICTIVE_SETTLE_DURATION_MS)
            )
}

@OptIn(ExperimentalAnimationApi::class)
fun predictivePopExitTransitionImproved(): ExitTransition {
    // Screen B (current screen) being swiped away
    return fadeOut(animationSpec = tween(PREDICTIVE_PEEK_DURATION_MS + PREDICTIVE_SETTLE_DURATION_MS)) +
            slideOutHorizontally(
                targetOffsetX = { fullWidth -> fullWidth / 2 }, // Don't slide fully off, mimic being held by finger
                animationSpec = tween(PREDICTIVE_PEEK_DURATION_MS + PREDICTIVE_SETTLE_DURATION_MS)
            ) +
            scaleOut( // Scale down more significantly
                targetScale = 0.85f,
                animationSpec = tween(PREDICTIVE_PEEK_DURATION_MS + PREDICTIVE_SETTLE_DURATION_MS)
            )
}


// Durations
private const val TOTAL_EXIT_DURATION_MS = 350 // Slightly shorter for a snappier feel
private const val TOTAL_ENTER_DURATION_MS = 350
private const val PEEK_DURATION_MS = 120 // Duration of the "peek" visual state

@OptIn(ExperimentalAnimationApi::class)
fun predictivePopExitTransitionRefined(): ExitTransition {
    // The slide will happen over the total duration.
    // The keyframes for scale and fade will create the staged effect.
    return slideOutHorizontally(
        targetOffsetX = { fullWidth -> fullWidth / 2 }, // How far it slides in total
        animationSpec = tween(
            durationMillis = TOTAL_EXIT_DURATION_MS,
            easing = FastOutSlowInEasing // Easing for the overall slide
        )
    ) + scaleOut(
        animationSpec = keyframes {
            durationMillis = TOTAL_EXIT_DURATION_MS
            1.0f at 0 with LinearEasing
            0.93f at PEEK_DURATION_MS with FastOutSlowInEasing // Scale down for peek
            0.85f at TOTAL_EXIT_DURATION_MS with FastOutSlowInEasing // Further scale down
        },
        targetScale = 0.85f
    ) + fadeOut(
        animationSpec = keyframes {
            durationMillis = TOTAL_EXIT_DURATION_MS
            1.0f at 0 with LinearEasing
            0.7f at PEEK_DURATION_MS with FastOutSlowInEasing // Slightly fade for peek
            0.0f at TOTAL_EXIT_DURATION_MS with FastOutSlowInEasing
        }
    )
}

@OptIn(ExperimentalAnimationApi::class)
fun predictivePopEnterTransitionRefined(): EnterTransition {
    return slideInHorizontally(
        initialOffsetX = { fullWidth -> -fullWidth / 4 }, // Start from this offset
        animationSpec = tween(
            durationMillis = TOTAL_ENTER_DURATION_MS,
            easing = FastOutSlowInEasing
        )
    ) + scaleIn(
        animationSpec = keyframes {
            durationMillis = TOTAL_ENTER_DURATION_MS
            0.93f at 0 with LinearEasing // Start slightly scaled down
            0.97f at PEEK_DURATION_MS with FastOutSlowInEasing // Scale up a bit during peek
            1.0f at TOTAL_ENTER_DURATION_MS with FastOutSlowInEasing
        },
        initialScale = 0.93f
    ) + fadeIn(
        animationSpec = keyframes {
            durationMillis = TOTAL_ENTER_DURATION_MS
            0.0f at 0 with LinearEasing
            0.3f at PEEK_DURATION_MS with FastOutSlowInEasing // Slightly visible
            1.0f at TOTAL_ENTER_DURATION_MS with FastOutSlowInEasing
        }
    )
}