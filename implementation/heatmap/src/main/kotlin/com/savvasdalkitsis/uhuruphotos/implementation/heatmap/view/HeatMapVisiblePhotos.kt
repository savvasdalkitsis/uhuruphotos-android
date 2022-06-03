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
package com.savvasdalkitsis.uhuruphotos.implementation.heatmap.view

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.savvasdalkitsis.uhuruphotos.api.albums.model.Album
import com.savvasdalkitsis.uhuruphotos.api.feed.view.Feed
import com.savvasdalkitsis.uhuruphotos.api.feed.view.state.FeedState
import com.savvasdalkitsis.uhuruphotos.implementation.heatmap.seam.HeatMapAction
import com.savvasdalkitsis.uhuruphotos.implementation.heatmap.seam.HeatMapAction.SelectedPhoto
import com.savvasdalkitsis.uhuruphotos.implementation.heatmap.view.state.HeatMapFeedDisplay
import com.savvasdalkitsis.uhuruphotos.implementation.heatmap.view.state.HeatMapState
import com.savvasdalkitsis.uhuruphotos.api.strings.R

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
                        photos = state.photosToDisplay,
                        date = stringResource(R.string.photos_on_map, state.photosToDisplay.size, state.allPhotos.size),
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