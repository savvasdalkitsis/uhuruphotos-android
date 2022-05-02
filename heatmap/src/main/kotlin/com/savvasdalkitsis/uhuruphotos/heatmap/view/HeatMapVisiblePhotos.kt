package com.savvasdalkitsis.uhuruphotos.heatmap.view

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.savvasdalkitsis.uhuruphotos.albums.model.Album
import com.savvasdalkitsis.uhuruphotos.feed.view.Feed
import com.savvasdalkitsis.uhuruphotos.feed.view.state.FeedState
import com.savvasdalkitsis.uhuruphotos.heatmap.view.state.HeatMapFeedDisplay
import com.savvasdalkitsis.uhuruphotos.heatmap.view.state.HeatMapState
import com.savvasdalkitsis.uhuruphotos.heatmap.viewmodel.HeatMapAction
import com.savvasdalkitsis.uhuruphotos.heatmap.viewmodel.HeatMapAction.SelectedPhoto

@Composable
fun HeatMapVisiblePhotos(
    modifier: Modifier = Modifier,
    loadingModifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
    state: HeatMapState,
    action: (HeatMapAction) -> Unit
) {
    if (state.allPhotos.isEmpty()) {
        CircularProgressIndicator(modifier = loadingModifier)
    } else {
        Feed(
            modifier = modifier,
            contentPadding = contentPadding,
            state = FeedState(
                isLoading = false,
                feedDisplay = HeatMapFeedDisplay,
                albums = listOf(
                    Album(
                        id = "visiblePhotos",
                        photoCount = state.photosToDisplay.size,
                        photos = state.photosToDisplay,
                        date = "Photos on map (${state.photosToDisplay.size} out of ${state.allPhotos.size})",
                        location = null,
                    )
                )
            ),
            onPhotoSelected = { photo, center, scale ->
                action(SelectedPhoto(photo, center, scale))
            },
        )
    }
}