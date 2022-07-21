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
package com.savvasdalkitsis.uhuruphotos.api.feed.view

import android.content.res.Configuration
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.savvasdalkitsis.uhuruphotos.api.albums.model.Album
import com.savvasdalkitsis.uhuruphotos.api.feed.view.state.FeedDisplay
import com.savvasdalkitsis.uhuruphotos.api.feed.view.state.FeedState
import com.savvasdalkitsis.uhuruphotos.api.photos.model.Photo
import com.savvasdalkitsis.uhuruphotos.api.photos.view.PhotoSelected
import com.savvasdalkitsis.uhuruphotos.api.strings.R
import com.savvasdalkitsis.uhuruphotos.api.ui.view.FullProgressBar
import com.savvasdalkitsis.uhuruphotos.api.ui.view.NoContent
import com.savvasdalkitsis.uhuruphotos.api.ui.window.LocalWindowSize

@Composable
fun Feed(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    state: FeedState,
    showSelectionHeader: Boolean = false,
    showAlbumRefreshButton: Boolean = false,
    listState: LazyListState = rememberLazyListState(),
    feedHeader: @Composable (LazyItemScope.() -> Unit)? = null,
    onPhotoSelected: PhotoSelected = { _, _, _ -> },
    onChangeDisplay: ((FeedDisplay) -> Unit) = {},
    onPhotoLongPressed: (Photo) -> Unit = {},
    onAlbumRefreshClicked: (Album) -> Unit = {},
    onAlbumSelectionClicked: (Album) -> Unit = {},
) = when {
    state.isLoading || (!state.isEmpty && state.albums.isEmpty()) -> FullProgressBar()
    state.isEmpty && state.albums.isEmpty() -> NoContent(R.string.no_photos)
    else -> {
        val feedDisplay = state.feedDisplay
        StaggeredDateFeed(
            modifier = modifier
                .let {
                    when {
                        feedDisplay.allowsPinchGestures -> it.pinchToChange(
                            feedDisplay,
                            onChangeDisplay,
                        )
                        else -> it
                    }
                },
            contentPadding = contentPadding,
            albums = state.albums,
            showSelectionHeader = showSelectionHeader,
            showAlbumRefreshButton = showAlbumRefreshButton,
            maintainAspectRatio = feedDisplay.maintainAspectRatio,
            miniIcons = feedDisplay.miniIcons,
            listState = listState,
            feedHeader = feedHeader,
            columnCount = feedDisplay.columnCount(
                widthSizeClass = LocalWindowSize.current.widthSizeClass,
                landscape = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE
            ),
            shouldAddEmptyPhotosInRows = feedDisplay.shouldAddEmptyPhotosInRows,
            onPhotoSelected = onPhotoSelected,
            onPhotoLongPressed = onPhotoLongPressed,
            onAlbumSelectionClicked = onAlbumSelectionClicked,
            onAlbumRefreshClicked = onAlbumRefreshClicked,
        )
    }
}

private fun FeedDisplay.columnCount(
    widthSizeClass: WindowWidthSizeClass,
    landscape: Boolean,
) = when {
    landscape -> when (widthSizeClass) {
        WindowWidthSizeClass.Compact -> compactColumnsLandscape
        else -> wideColumnsLandscape
    }
    else -> when (widthSizeClass) {
        WindowWidthSizeClass.Compact -> compactColumnsPortrait
        else -> wideColumnsPortrait
    }
}