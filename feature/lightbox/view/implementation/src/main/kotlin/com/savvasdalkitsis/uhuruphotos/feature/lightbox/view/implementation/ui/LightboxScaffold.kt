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
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ScrollState
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass.Companion.Compact
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.savvasdalkitsis.uhuruphotos.feature.auth.view.api.navigation.LocalServerUrl
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.actions.LightboxAction
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.actions.ShowActionsOverlay
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.actions.UpPressed
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.ui.state.LightboxState
import com.savvasdalkitsis.uhuruphotos.foundation.dismiss.api.ui.rememberPullToDismissState
import com.savvasdalkitsis.uhuruphotos.foundation.theme.api.window.LocalWindowSize
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.BackPressHandler
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.CommonScaffold
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.FullLoading
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.UpNavButton
import kotlinx.coroutines.launch
import me.saket.telephoto.zoomable.ZoomableState

@Composable
internal fun LightboxScaffold(
    state: LightboxState,
    index: Int,
    action: (LightboxAction) -> Unit,
    zoomableState: ZoomableState,
    scrollState: ScrollState,
) {
    val scope = rememberCoroutineScope()
    val density = LocalDensity.current

    val dismissState = rememberPullToDismissState(
        onDismiss = { action(UpPressed) },
    )
    val showingActionsOverlay by remember {
        derivedStateOf {
            scrollState.value < with(density) {
                48.dp.toPx()
            }
        }
    }
    LaunchedEffect(showingActionsOverlay) {
        action(ShowActionsOverlay(showingActionsOverlay))
    }
    BackPressHandler {
        scope.launch {
            val scrolledDown = !showingActionsOverlay
            if (scrolledDown) {
                scrollState.animateScrollTo(0)
            } else if ((zoomableState.zoomFraction ?: 0f) > 0f){
                zoomableState.resetZoom()
            } else {
                action(UpPressed)
            }
        }
    }
    CommonScaffold(
        title = { },
        bottomBarContent = {
            AnimatedVisibility(
                visible = LocalWindowSize.current.widthSizeClass == Compact && state.showActionsOverlay,
                enter = slideInVertically(initialOffsetY = { it }),
                exit = slideOutVertically(targetOffsetY = { it }),
            ) {
                LightboxDismissProgressAware(dismissState) {
                    LightboxBottomActionBar(state, index, action)
                }
            }
        },
        actionBarContent = {
            LightboxActionBar(state, index, action, scrollState, dismissState)
        },
        toolbarColor = { Color.Transparent },
        bottomBarColor = { Color.Transparent },
        topBarDisplayed = state.showUI,
        bottomBarDisplayed = state.showUI,
        navigationIcon = {
            LightboxDismissProgressAware(dismissState) {
                UpNavButton { action(UpPressed) }
            }
        },
    ) { contentPadding ->
        val serverUrl = LocalServerUrl.current
        val mediaItem = state.media[index]
        val thumbnailUri = remember(serverUrl, mediaItem.id) {
            mediaItem.id.thumbnailUri(serverUrl)
        }

        when {
            state.isLoading && thumbnailUri.isEmpty() -> FullLoading()
            else -> LightboxCanvas(action, state, index, contentPadding, scrollState, zoomableState, dismissState)
        }
    }
}