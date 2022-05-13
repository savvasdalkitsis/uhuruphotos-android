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
package com.savvasdalkitsis.uhuruphotos.feed.view

import android.content.res.Configuration
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.savvasdalkitsis.uhuruphotos.albums.api.model.Album
import com.savvasdalkitsis.uhuruphotos.feed.view.state.FeedDisplay
import com.savvasdalkitsis.uhuruphotos.feed.view.state.FeedState
import com.savvasdalkitsis.uhuruphotos.photos.api.model.Photo
import com.savvasdalkitsis.uhuruphotos.ui.view.FullProgressBar
import com.savvasdalkitsis.uhuruphotos.ui.window.LocalWindowSize

@Composable
fun Feed(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    state: FeedState,
    showSelectionHeader: Boolean = false,
    listState: LazyListState = rememberLazyListState(),
    onPhotoSelected: PhotoSelected = { _, _, _ -> },
    onChangeDisplay: ((FeedDisplay) -> Unit)? = null,
    onPhotoLongPressed: (Photo) -> Unit = {},
    onAlbumSelectionClicked: (Album) -> Unit = {},
) {
    if (state.isLoading && state.albums.isEmpty()) {
        FullProgressBar()
    } else {
        val feedDisplay = state.feedDisplay
        StaggeredDateFeed(
            modifier = modifier
                .let {
                    when {
                        onChangeDisplay != null -> it.pinchToChange(
                            feedDisplay,
                            onChangeDisplay,
                        )
                        else -> it
                    }
                },
            contentPadding = contentPadding,
            albums = state.albums,
            showSelectionHeader = showSelectionHeader,
            maintainAspectRatio = feedDisplay.maintainAspectRatio,
            listState = listState,
            columnCount = feedDisplay.columnCount(
                widthSizeClass = LocalWindowSize.current.widthSizeClass,
                landscape = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE
            ),
            shouldAddEmptyPhotosInRows = feedDisplay.shouldAddEmptyPhotosInRows,
            onPhotoSelected = onPhotoSelected,
            onPhotoLongPressed = onPhotoLongPressed,
            onAlbumSelectionClicked = onAlbumSelectionClicked,
        )
    }
}