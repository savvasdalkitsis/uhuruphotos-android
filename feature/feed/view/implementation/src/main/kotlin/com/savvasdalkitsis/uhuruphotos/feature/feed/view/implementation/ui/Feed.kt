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
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.savvasdalkitsis.uhuruphotos.feature.collage.view.api.ui.Collage
import com.savvasdalkitsis.uhuruphotos.feature.collage.view.api.ui.state.PredefinedCollageDisplay
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.api.ui.state.FeedMediaItemSyncDisplay.ALWAYS_OFF
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.api.ui.state.FeedMediaItemSyncDisplay.ALWAYS_ON
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.api.ui.state.FeedMediaItemSyncDisplay.SHOW_ON_SCROLL
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.actions.CelLongPressed
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.actions.ChangeDisplay
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.actions.ClusterRefreshClicked
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.actions.ClusterSelectionClicked
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.actions.DeleteLocalSelectedCels
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.actions.DismissSelectedMediaTrashing
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.actions.EnableCloudSync
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.actions.FeedAction
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.actions.MemorySelected
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.actions.NeverAskForCloudSyncRequest
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.actions.NeverAskForLocalMediaAccessPermissionRequest
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.actions.RefreshFeed
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.actions.SelectedCel
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.actions.TrashRemoteAndDeleteLocalSelectedCels
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.actions.TrashRemoteSelectedCels
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.ui.state.FeedState
import com.savvasdalkitsis.uhuruphotos.feature.home.view.api.ui.HomeScaffold
import com.savvasdalkitsis.uhuruphotos.feature.media.common.view.api.ui.DeleteFullySyncedPermissionDialog
import com.savvasdalkitsis.uhuruphotos.feature.media.common.view.api.ui.DeletePermissionDialog
import com.savvasdalkitsis.uhuruphotos.feature.media.common.view.api.ui.TrashPermissionDialog
import com.savvasdalkitsis.uhuruphotos.feature.media.local.view.api.ui.LocalMediaAccessRequestBanner
import com.savvasdalkitsis.uhuruphotos.feature.media.local.view.api.ui.RequestBanner
import com.savvasdalkitsis.uhuruphotos.foundation.compose.api.blurIf
import com.savvasdalkitsis.uhuruphotos.foundation.strings.api.R.string
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.SwipeRefresh
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.grid.SmartGridState
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.grid.rememberSmartGridState
import dev.shreyaspatil.permissionflow.compose.rememberPermissionFlowRequestLauncher
import kotlinx.coroutines.launch

@Composable
internal fun Feed(
    state: FeedState,
    isShowingPopUp: Boolean,
    action: (FeedAction) -> Unit,
    actionBarContent: @Composable () -> Unit,
    additionalContent: @Composable () -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    val gridState = rememberSmartGridState(state.collageState.collageDisplay.usingStaggeredGrid)

    fun scrollToTop() {
        coroutineScope.launch {
            gridState.animateScrollToItem(0, 0)
        }
    }
    val isScrolling by remember {
        derivedStateOf {
            gridState.isScrollInProgress
        }
    }
    val showSyncState = when (state.syncItemDisplay) {
        SHOW_ON_SCROLL -> isScrolling
        ALWAYS_ON -> true
        ALWAYS_OFF -> false
    }
    val permissionLauncher = rememberPermissionFlowRequestLauncher()

    HomeScaffold(
        modifier = Modifier.blurIf(isShowingPopUp),
        title = {
            FeedTitle(action, ::scrollToTop, state.hasSelection, state.selectedCelCount)
        },
        homeFeedDisplay = state.collageState.collageDisplay,
        selectionMode = state.hasSelection,
        showLibrary = state.showLibrary,
        showBottomNavigationBar = !state.autoHideNavOnScroll || gridState.isScrollingUp(),
        actionBarContent = {
            FeedActionBar(
                shouldShowShareIcon = state.shouldShowShareIcon,
                shouldShowDeleteIcon = state.shouldShowDeleteIcon,
                shouldShowDownloadIcon = state.shouldShowDownloadIcon,
                shouldShowUploadIcon = state.shouldShowUploadIcon,
                hasSelection = state.hasSelection,
                collageDisplay = state.collageState.collageDisplay,
                action = action
            )
            actionBarContent()
        },
        onReselected = { scrollToTop() },
    ) { contentPadding ->
        SwipeRefresh(
            indicatorPadding = contentPadding,
            isRefreshing = state.isRefreshing,
            onRefresh = { action(RefreshFeed) }
        ) {
            Collage(
                contentPadding = contentPadding,
                state = state.collageState,
                showSelectionHeader = state.hasSelection,
                showSyncState = showSyncState,
                showStickyHeaders = true,
                showScrollbarHint = true,
                gridState = gridState,
                collageHeader = {
                    AnimatedVisibility(
                        visible =
                        state.memories.isNotEmpty()
                                || state.showRequestPermissionForLocalMediaAccess != null
                                || state.localMediaSyncRunning
                                || state.showRequestForCloudSync,
                    ) {
                        Column(
                            verticalArrangement = spacedBy(8.dp),
                        ) {
                            if (state.memories.isNotEmpty()) {
                                FeedMemories(state.memories) { cel, yearsAgo ->
                                    action(MemorySelected(cel, yearsAgo))
                                }
                            }
                            val missingPermissions = state.showRequestPermissionForLocalMediaAccess
                            if (missingPermissions != null) {
                                LocalMediaAccessRequestBanner(
                                    modifier = Modifier.padding(4.dp),
                                    missingPermissions = missingPermissions,
                                    description = string.missing_local_media_permissions,
                                ) {
                                    action(NeverAskForLocalMediaAccessPermissionRequest)
                                }
                            }
                            if (state.showRequestForCloudSync) {
                                RequestBanner(
                                    modifier = Modifier.padding(4.dp),
                                    description = string.enable_cloud_sync,
                                    grantText = string.enable,
                                    onAccessGranted = { action(EnableCloudSync) },
                                    onNeverRemindMeAgain = { action(NeverAskForCloudSyncRequest) },
                                )
                            }
                            if (state.localMediaSyncRunning) {
                                FeedLocalMediaSyncRunning()
                            }
                        }
                    }
                },
                onCelSelected = { cel ->
                    action(SelectedCel(cel))
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
                onDismiss = { action(DismissSelectedMediaTrashing) },
                onDelete = { action(TrashRemoteSelectedCels) },
            )
        }
        if (state.showDeleteConfirmationDialog) {
            DeletePermissionDialog(
                mediaItemCount = state.selectedCelCount,
                onDismiss = { action(DismissSelectedMediaTrashing) },
                onDelete = { action(DeleteLocalSelectedCels) },
            )
        }
        if (state.showFullySyncedDeleteConfirmationDialog) {
            DeleteFullySyncedPermissionDialog(
                onDismiss = { action(DismissSelectedMediaTrashing) },
                onDeleteLocalTrashRemote =  { action(TrashRemoteAndDeleteLocalSelectedCels) },
                onDeleteLocal =  { action(DeleteLocalSelectedCels) },
            )
        }
        if (state.missingPermissions.isNotEmpty()) {
            permissionLauncher.launch(state.missingPermissions.toTypedArray())
        }
        additionalContent()
    }
}

@Composable
private fun SmartGridState.isScrollingUp(): Boolean {
    var previousIndex by remember(this) { mutableIntStateOf(firstVisibleItemIndex) }
    var previousScrollOffset by remember(this) { mutableIntStateOf(firstVisibleItemScrollOffset) }
    return remember(this) {
        derivedStateOf {
            if (previousIndex != firstVisibleItemIndex) {
                previousIndex > firstVisibleItemIndex
            } else {
                previousScrollOffset >= firstVisibleItemScrollOffset
            }.also {
                previousIndex = firstVisibleItemIndex
                previousScrollOffset = firstVisibleItemScrollOffset
            }
        }
    }.value
}