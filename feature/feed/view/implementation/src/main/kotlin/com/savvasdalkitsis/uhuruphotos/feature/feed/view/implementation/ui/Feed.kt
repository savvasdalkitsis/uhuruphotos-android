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
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.savvasdalkitsis.uhuruphotos.feature.collage.view.api.ui.Collage
import com.savvasdalkitsis.uhuruphotos.feature.collage.view.api.ui.state.PredefinedCollageDisplay
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.actions.CelLongPressed
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.actions.ChangeDisplay
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.actions.ClusterRefreshClicked
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.actions.ClusterSelectionClicked
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.actions.DismissSelectedPhotosTrashing
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.actions.FeedAction
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.actions.MemorySelected
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.actions.NeverAskForLocalMediaAccessPermissionRequest
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.actions.RefreshFeed
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.actions.SelectedCel
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.actions.TrashSelectedCels
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.ui.state.FeedState
import com.savvasdalkitsis.uhuruphotos.feature.home.view.api.ui.HomeScaffold
import com.savvasdalkitsis.uhuruphotos.feature.media.common.view.api.ui.TrashPermissionDialog
import com.savvasdalkitsis.uhuruphotos.feature.media.local.view.api.ui.LocalMediaAccessRequestBanner
import com.savvasdalkitsis.uhuruphotos.foundation.compose.api.blurIf
import com.savvasdalkitsis.uhuruphotos.foundation.strings.api.R.string
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
                                LocalMediaAccessRequestBanner(
                                    missingPermissions = missingPermissions,
                                    description = string.missing_local_media_permissions,
                                ) {
                                    action(NeverAskForLocalMediaAccessPermissionRequest)
                                }
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