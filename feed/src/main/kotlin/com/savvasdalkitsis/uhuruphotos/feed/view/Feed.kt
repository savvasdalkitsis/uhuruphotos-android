package com.savvasdalkitsis.uhuruphotos.feed.view

import android.content.res.Configuration
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.savvasdalkitsis.uhuruphotos.albums.model.Album
import com.savvasdalkitsis.uhuruphotos.feed.view.state.FeedDisplay
import com.savvasdalkitsis.uhuruphotos.feed.view.state.FeedState
import com.savvasdalkitsis.uhuruphotos.photos.model.Photo
import com.savvasdalkitsis.uhuruphotos.ui.view.FullProgressBar
import com.savvasdalkitsis.uhuruphotos.ui.window.WindowSize

@Composable
fun Feed(
    contentPadding: PaddingValues = PaddingValues(0.dp),
    state: FeedState,
    showSelectionHeader: Boolean = false,
    listState: LazyListState = rememberLazyListState(),
    onPhotoSelected: PhotoSelected = { _, _, _ -> },
    onChangeDisplay: (FeedDisplay) -> Unit = {},
    onPhotoLongPressed: (Photo) -> Unit = {},
    onAlbumSelectionClicked: (Album) -> Unit = {},
    ) {
    if (state.isLoading && state.albums.isEmpty()) {
        FullProgressBar()
    } else {
        val feedDisplay = state.feedDisplay
        val modifier = Modifier
            .pinchToChange(
                feedDisplay,
                onChangeDisplay,
            )
        val columnCount = feedDisplay.columnCount(
            windowSizeClass = WindowSize.LOCAL_WIDTH.current,
            landscape = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE
        )
        StaggeredDateFeed(
            modifier = modifier,
            contentPadding = contentPadding,
            albums = state.albums,
            showSelectionHeader = showSelectionHeader,
            maintainAspectRatio = feedDisplay.maintainAspectRatio,
            listState = listState,
            columnCount = columnCount,
            shouldAddEmptyPhotosInRows = feedDisplay.shouldAddEmptyPhotosInRows,
            onPhotoSelected = onPhotoSelected,
            onPhotoLongPressed = onPhotoLongPressed,
            onAlbumSelectionClicked = onAlbumSelectionClicked,
        )
    }
}