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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import com.savvasdalkitsis.uhuruphotos.feature.account.view.api.ui.AccountOverviewActionBar
import com.savvasdalkitsis.uhuruphotos.feature.account.view.api.ui.AccountOverviewContent
import com.savvasdalkitsis.uhuruphotos.feature.collage.view.api.ui.Collage
import com.savvasdalkitsis.uhuruphotos.feature.collage.view.api.ui.state.PredefinedCollageDisplayState
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.api.ui.state.FeedMediaItemSyncDisplayState.ALWAYS_OFF
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.api.ui.state.FeedMediaItemSyncDisplayState.ALWAYS_ON
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.api.ui.state.FeedMediaItemSyncDisplayState.SHOW_ON_SCROLL
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.actions.CelLongPressed
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.actions.ChangeDisplay
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.actions.ClusterRefreshClicked
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.actions.ClusterSelectionClicked
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.actions.DeleteLocalSelectedCels
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.actions.DismissSelectedMediaTrashing
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.actions.FeedAction
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.actions.RefreshFeed
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.actions.SelectedCel
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.actions.TrashRemoteAndDeleteLocalSelectedCels
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.actions.TrashRemoteSelectedCels
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.ui.state.FeedState
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.viewmodel.FeedCompositeAction
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.viewmodel.FeedCompositeState
import com.savvasdalkitsis.uhuruphotos.feature.home.view.api.ui.HomeScaffold
import com.savvasdalkitsis.uhuruphotos.feature.media.common.view.api.ui.DeleteFullySyncedPermissionDialog
import com.savvasdalkitsis.uhuruphotos.feature.media.common.view.api.ui.DeletePermissionDialog
import com.savvasdalkitsis.uhuruphotos.feature.media.common.view.api.ui.TrashPermissionDialog
import com.savvasdalkitsis.uhuruphotos.feature.media.common.view.api.ui.state.CelSelectionModeState
import com.savvasdalkitsis.uhuruphotos.feature.media.common.view.api.ui.state.CelState
import com.savvasdalkitsis.uhuruphotos.foundation.compose.api.blurIf
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.Either
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.grid.smart.SmartGridState
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.grid.smart.rememberSmartGridState
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.refresh.SwipeRefresh
import dev.shreyaspatil.permissionflow.compose.rememberPermissionFlowRequestLauncher
import kotlinx.coroutines.launch

@Composable
internal fun Feed(
    state: FeedCompositeState,
    actions: (FeedCompositeAction) -> Unit,
) {
    Feed(
        state.first,
        isShowingPopUp = state.second.showAccountOverview,
        action = {
            actions(Either.Left(it))
        },
        actionBarContent = {
            AnimatedVisibility(visible = !state.first.hasSelection) {
                AccountOverviewActionBar(state.second) {
                    actions(Either.Right(it))
                }
            }
        }
    ) {
        AccountOverviewContent(state.second) {
            actions(Either.Right(it))
        }
    }
}

@Composable
private fun Feed(
    state: FeedState,
    isShowingPopUp: Boolean,
    action: (FeedAction) -> Unit,
    actionBarContent: @Composable () -> Unit,
    additionalContent: @Composable () -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    val gridState = rememberSmartGridState(state.collageState.collageDisplayState.usingStaggeredGrid)

    fun scrollToTop() {
        coroutineScope.launch {
            gridState.animateScrollToItem(0, 0)
        }
    }
    val feedHeadersVisible by remember {
        derivedStateOf {
            state.memories.isNotEmpty()
                    || state.showRequestPermissionForLocalMediaAccess != null
                    || state.localMediaSyncRunning
                    || state.showRequestForCloudSync
                    || state.showLoginBanner
                    || state.showBatteryOptimizationBanner
        }
    }

    val showSyncState by remember {
        derivedStateOf {
            when (state.syncItemDisplay) {
                SHOW_ON_SCROLL -> gridState.isScrollInProgress
                ALWAYS_ON -> true
                ALWAYS_OFF -> false
            }
        }
    }
    val permissionLauncher = rememberPermissionFlowRequestLauncher()

    HomeScaffold(
        modifier = Modifier.blurIf(isShowingPopUp),
        title = {
            FeedTitle(action, ::scrollToTop, state.hasSelection, state.selectedCelCount)
        },
        homeFeedDisplay = state.collageState.collageDisplayState,
        selectionMode = state.hasSelection,
        showLibrary = state.showLibrary,
        showBottomNavigationBar = !state.autoHideNavOnScroll || gridState.isScrollingUp(),
        actionBarContent = {
            FeedActionBar(
                shouldShowAddIcon = state.shouldShowAddIcon,
                shouldShowShareIcon = state.shouldShowShareIcon,
                shouldShowDeleteIcon = state.shouldShowDeleteIcon,
                shouldShowDownloadIcon = state.shouldShowDownloadIcon,
                shouldShowUploadIcon = state.shouldShowUploadIcon,
                hasSelection = state.hasSelection,
                collageDisplayState = state.collageState.collageDisplayState,
                action = action
            )
            actionBarContent()
        },
        onReselected = { scrollToTop() },
    ) { contentPadding ->
        val density = LocalDensity.current
        val topPadding = remember(density, contentPadding) {
            with(density) {
                contentPadding.calculateTopPadding().toPx().toInt()
            }
        }
        fun scrollToCel(cel: CelState) {
            coroutineScope.launch {
                var found = false
                val index = state.collageState.clusterStates.fold(0) { index, cluster ->
                    if (found) {
                        index
                    } else if (cluster.cels.contains(cel)) {
                        found = true
                        index + 1 + cluster.cels.indexOf(cel)
                    } else {
                        index + 1 + cluster.cels.size
                    }
                } + if (feedHeadersVisible) 1 else 0
                if (index < gridState.layoutInfo.totalItemsCount) {
                    gridState.animateScrollToItem(index, -topPadding)
                }
            }
        }
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
                celsSelectionMode = CelSelectionModeState.CHECKABLE,
                gridState = gridState,
                collageHeader = {
                    FeedHeaders(
                        state = state,
                        visible = feedHeadersVisible,
                        onScrollToMemory = ::scrollToCel,
                        action = action)
                },
                collageFooter = {
                    FeedFooters(
                        state = state
                    )
                },
                onCelSelected = { cel ->
                    action(SelectedCel(cel))
                },
                onChangeDisplay = { action(ChangeDisplay(it as PredefinedCollageDisplayState)) },
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
        state.showAddToAlbumDialog?.let { albums ->
            AddToAlbumDialog(albums, action)
        }
        if (state.showNewAlbumNameDialog) {
            NewAlbumNameDialog(action)
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