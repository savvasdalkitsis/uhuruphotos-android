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
package com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateRectAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.round
import androidx.core.graphics.drawable.toBitmapOrNull
import com.savvasdalkitsis.uhuruphotos.foundation.dismiss.api.ui.PullToDismissState
import com.savvasdalkitsis.uhuruphotos.foundation.image.api.model.LocalThumbnailImageLoader
import com.savvasdalkitsis.uhuruphotos.foundation.image.api.ui.toRequest
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.shared.LocalSharedElementTransition

@Composable
internal fun LightboxPreviousScreenBackground(
    dismissState: PullToDismissState,
    content: @Composable () -> Unit,
) {
    val sharedElementTransition = LocalSharedElementTransition.current
    var finishedSharedTransitionAlready by LocalAnimatedSharedTransitionFinished.current
    val previousScreen = sharedElementTransition.screenshotState.bitmap
    var animatingSharedElement by remember {
        mutableStateOf(true)
    }
    val transition = remember {
        MutableTransitionState(false)
    }.apply {
        targetState = !animatingSharedElement
    }
    if (previousScreen == null) {
        animatingSharedElement = false
    } else {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            AnimatedVisibility(
                visible = animatingSharedElement || dismissState.progress > 0f,
                enter = EnterTransition.None,
                exit = fadeOut(),
            ) {
                Image(
                    bitmap = previousScreen.asImageBitmap(),
                    contentDescription = null,
                )
            }

            val startPosition = sharedElementTransition.bounds
            val subImage = sharedElementTransition.contents
            if (!finishedSharedTransitionAlready && subImage != null && startPosition != null && (!transition.isIdle || animatingSharedElement)) {
                var subImageBitmap by remember {
                    mutableStateOf<ImageBitmap?>(null)
                }
                val maxWidth = constraints.maxWidth.toFloat()
                val maxHeight = constraints.maxHeight.toFloat()
                val startRatio = startPosition.width / startPosition.height
                val endRatio = maxWidth / maxHeight
                val ratio = startPosition.width / startPosition.height
                val endPosition = if (endRatio > startRatio) {
                    val w = maxHeight / ratio
                    Rect(
                        maxWidth / 2 - w / 2,
                        0f,
                        maxWidth / 2 + w / 2,
                        maxHeight
                    )
                } else {
                    val h = maxWidth / ratio
                    Rect(
                        0f,
                        maxHeight / 2 - h / 2,
                        maxWidth,
                        maxHeight / 2 + h / 2,
                    )
                }
                var startedAnimation by remember {
                    mutableStateOf(false)
                }
                val rect by animateRectAsState(
                    targetValue = when {
                        startedAnimation -> endPosition
                        else -> startPosition
                    },
                    label = "sharedElementAnimation",
                    finishedListener = {
                        animatingSharedElement = false
                    },
                )
                val request = subImage.toRequest(
                    onSuccess = { result ->
                        subImageBitmap = result.drawable.toBitmapOrNull()?.asImageBitmap()
                        startedAnimation = true
                    },
                    onError = {
                        startedAnimation = true
                    }
                )
                val imageLoader = LocalThumbnailImageLoader.current
                LaunchedEffect(subImage) {
                    imageLoader.execute(request)
                }
                subImageBitmap?.let { it ->
                    val alpha by animateFloatAsState(targetValue = 1f, label = "alpha")
                    with(LocalDensity.current) {
                        Image(
                            bitmap = it,
                            contentDescription = null,
                            modifier = Modifier
                                .alpha(alpha)
                                .offset { rect.topLeft.round() }
                                .size(rect.width.toDp(), rect.height.toDp()),
                            contentScale = ContentScale.FillBounds,
                        )
                    }
                }
            }
        }
    }
    if (finishedSharedTransitionAlready) {
        content()
    } else {
        AnimatedVisibility(
            visibleState = transition,
            enter = fadeIn(),
        ) {
            content()
            if (transition.isIdle && transition.currentState) {
                finishedSharedTransitionAlready = true
            }
        }
    }
}