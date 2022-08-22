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
package com.savvasdalkitsis.uhuruphotos.feature.collage.view.api.ui

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
import com.savvasdalkitsis.uhuruphotos.feature.collage.view.api.ui.state.CollageDisplay
import com.savvasdalkitsis.uhuruphotos.feature.collage.view.api.ui.state.CollageState
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItem
import com.savvasdalkitsis.uhuruphotos.feature.media.common.view.api.ui.MediaItemSelected
import com.savvasdalkitsis.uhuruphotos.foundation.strings.api.R.string
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.FullProgressBar
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.NoContent
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.window.LocalWindowSize

@Composable
fun Collage(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    state: CollageState,
    showSelectionHeader: Boolean = false,
    showGroupRefreshButton: Boolean = false,
    listState: LazyListState = rememberLazyListState(),
    collageHeader: @Composable (LazyItemScope.() -> Unit)? = null,
    emptyContent: @Composable () -> Unit = { NoContent(string.no_photos) },
    onMediaItemSelected: MediaItemSelected = { _, _, _ -> },
    onChangeDisplay: ((CollageDisplay) -> Unit) = {},
    onPhotoLongPressed: (MediaItem) -> Unit = {},
    onGroupRefreshClicked: (Album) -> Unit = {},
    onGroupSelectionClicked: (Album) -> Unit = {},
) = when {
    state.isLoading || (!state.isEmpty && state.albums.isEmpty()) -> FullProgressBar()
    state.isEmpty && state.albums.isEmpty() -> emptyContent()
    else -> {
        val collageDisplay = state.collageDisplay
        StaggeredCollage(
            modifier = modifier
                .let {
                    when {
                        collageDisplay.allowsPinchGestures -> it.pinchToChange(
                            collageDisplay,
                            onChangeDisplay,
                        )
                        else -> it
                    }
                },
            contentPadding = contentPadding,
            albums = state.albums,
            showSelectionHeader = showSelectionHeader,
            showAlbumRefreshButton = showGroupRefreshButton,
            maintainAspectRatio = collageDisplay.maintainAspectRatio,
            miniIcons = collageDisplay.miniIcons,
            listState = listState,
            collageHeader = collageHeader,
            columnCount = collageDisplay.columnCount(
                widthSizeClass = LocalWindowSize.current.widthSizeClass,
                landscape = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE
            ),
            shouldAddEmptyPhotosInRows = collageDisplay.shouldAddEmptyPhotosInRows,
            onMediaItemSelected = onMediaItemSelected,
            onMediaItemLongPressed = onPhotoLongPressed,
            onAlbumSelectionClicked = onGroupSelectionClicked,
            onAlbumRefreshClicked = onGroupRefreshClicked,
        )
    }
}

private fun CollageDisplay.columnCount(
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