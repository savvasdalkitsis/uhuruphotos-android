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

import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.FeedAction
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.FeedAction.AlbumRefreshClicked
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.FeedAction.AlbumSelectionClicked
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.FeedAction.ChangeDisplay
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.FeedAction.DismissSelectedPhotosTrashing
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.FeedAction.PhotoLongPressed
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.FeedAction.RefreshAlbums
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.FeedAction.SelectedPhoto
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.FeedAction.TrashSelectedPhotos
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.ui.state.FeedState
import com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.ui.Gallery
import com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.ui.state.PredefinedGalleryDisplay
import com.savvasdalkitsis.uhuruphotos.feature.home.view.api.ui.HomeScaffold
import com.savvasdalkitsis.uhuruphotos.feature.media.common.view.api.ui.TrashPermissionDialog
import com.savvasdalkitsis.uhuruphotos.foundation.compose.api.blurIf
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

    HomeScaffold(
        modifier = Modifier.blurIf(isShowingPopUp),
        title = {
            FeedTitle(state, action, ::scrollToTop)
        },
        navController = navHostController,
        showLibrary = state.showLibrary,
        homeFeedDisplay = state.galleryState.galleryDisplay,
        selectionMode = state.hasSelection,
        actionBarContent = {
            FeedActionBar(state, action)
            actionBarContent()
        },
        onReselected = { scrollToTop() },
    ) { contentPadding ->
        SwipeRefresh(
            indicatorPadding = contentPadding,
            state = rememberSwipeRefreshState(isRefreshing = state.isRefreshing),
            onRefresh = { action(RefreshAlbums) }
        ) {
            Gallery(
                contentPadding = contentPadding,
                state = state.galleryState,
                showSelectionHeader = state.hasSelection,
                showGroupRefreshButton = state.shouldShowAlbumRefreshButtons,
                listState = listState,
                onMediaItemSelected = { photo, center, scale ->
                    action(SelectedPhoto(photo, center, scale,))
                },
                onChangeDisplay = { action(ChangeDisplay(it as PredefinedGalleryDisplay)) },
                onPhotoLongPressed = {
                    action(PhotoLongPressed(it))
                },
                onGroupSelectionClicked = {
                    action(AlbumSelectionClicked(it))
                },
                onGroupRefreshClicked = {
                    action(AlbumRefreshClicked(it))
                }
            )
        }
        if (state.showPhotoTrashingConfirmationDialog) {
            TrashPermissionDialog(
                mediaItemCount = state.selectedPhotoCount,
                onDismiss = { action(DismissSelectedPhotosTrashing) },
                onDelete = { action(TrashSelectedPhotos) }
            )
        }
        additionalContent()
    }
}