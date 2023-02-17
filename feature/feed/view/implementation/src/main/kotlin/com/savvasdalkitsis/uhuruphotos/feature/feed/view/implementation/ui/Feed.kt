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
package com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.savvasdalkitsis.uhuruphotos.feature.collage.view.api.ui.Collage
import com.savvasdalkitsis.uhuruphotos.feature.collage.view.api.ui.state.PredefinedCollageDisplay
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.FeedAction
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.FeedAction.CelLongPressed
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.FeedAction.ChangeDisplay
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.FeedAction.ClusterRefreshClicked
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.FeedAction.ClusterSelectionClicked
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.FeedAction.DismissSelectedPhotosTrashing
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.FeedAction.MemorySelected
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.FeedAction.RefreshFeed
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.FeedAction.SelectedCel
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.FeedAction.TrashSelectedCels
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.ui.state.FeedState
import com.savvasdalkitsis.uhuruphotos.feature.home.view.api.ui.HomeScaffold
import com.savvasdalkitsis.uhuruphotos.feature.media.common.view.api.ui.TrashPermissionDialog
import com.savvasdalkitsis.uhuruphotos.foundation.compose.api.blurIf
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.SwipeRefresh
import kotlinx.coroutines.launch

@Composable
internal fun Feed(
    navHostController: NavHostController,
    state: FeedState,
    isShowingPopUp: Boolean,
    action: (FeedAction) -> Unit,
    actionBarContent: @Composable () -> Unit,
    additionalContent: @Composable () -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    fun scrollToTop() {
        coroutineScope.launch {
            listState.animateScrollToItem(0, 0)
        }
    }
    val isScrolling by remember {
        derivedStateOf {
            listState.isScrollInProgress
        }
    }

    HomeScaffold(
        modifier = Modifier.blurIf(isShowingPopUp),
        title = {
            FeedTitle(state, action, ::scrollToTop)
        },
        navController = navHostController,
        showLibrary = state.showLibrary,
        homeFeedDisplay = state.collageState.collageDisplay,
        selectionMode = state.hasSelection,
        actionBarContent = {
            FeedActionBar(state, action)
            actionBarContent()
        },
        onReselected = { scrollToTop() },
    ) { contentPadding ->
        SwipeRefresh(
            indicatorPadding = contentPadding,
            isRefreshing = state.isRefreshing || state.collageState.isLoading,
            onRefresh = { action(RefreshFeed) }
        ) {
            Collage(
                contentPadding = contentPadding,
                state = state.collageState,
                showSelectionHeader = state.hasSelection,
                showSyncState = isScrolling,
                listState = listState,
                collageHeader = {
                    AnimatedVisibility(
                        visible =
                        state.memories.isNotEmpty()
                                || state.showRequestPermissionForLocalMediaAccess != null
                                || state.localMediaSyncRunning,
                    ) {
                        Column(
                            verticalArrangement = spacedBy(8.dp),
                        ) {
                            if (state.memories.isNotEmpty()) {
                                FeedMemories(state.memories) { cel, center, scale ->
                                    action(MemorySelected(cel, center, scale))
                                }
                            }
                            val missingPermissions = state.showRequestPermissionForLocalMediaAccess
                            if (missingPermissions != null) {
                                FeedLocalMediaAccessRequest(missingPermissions, action)
                            }
                            if (state.localMediaSyncRunning) {
                                FeedLocalMediaSyncRunning()
                            }
                        }
                    }
                },
                onCelSelected = { cel, center, scale ->
                    action(SelectedCel(cel, center, scale,))
                },
                onChangeDisplay = { action(ChangeDisplay(it as PredefinedCollageDisplay)) },
                onCelLongPressed = {
                    action(CelLongPressed(it))
                },
                onClusterRefreshClicked = {
                    action(ClusterRefreshClicked(it))
                }
            ) {
                action(ClusterSelectionClicked(it))
            }
        }
        if (state.showTrashingConfirmationDialog) {
            TrashPermissionDialog(
                mediaItemCount = state.selectedCelCount,
                onDismiss = { action(DismissSelectedPhotosTrashing) },
                onDelete = { action(TrashSelectedCels) }
            )
        }
        additionalContent()
    }
}