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
package com.savvasdalkitsis.uhuruphotos.heatmap.view

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.savvasdalkitsis.uhuruphotos.albums.api.model.Album
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