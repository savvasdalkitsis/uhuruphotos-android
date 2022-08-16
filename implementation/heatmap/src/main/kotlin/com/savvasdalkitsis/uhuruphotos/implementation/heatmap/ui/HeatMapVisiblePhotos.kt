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
package com.savvasdalkitsis.uhuruphotos.implementation.heatmap.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.savvasdalkitsis.uhuruphotos.api.albums.model.Album
import com.savvasdalkitsis.uhuruphotos.api.gallery.ui.Gallery
import com.savvasdalkitsis.uhuruphotos.api.gallery.ui.state.GalleryState
import com.savvasdalkitsis.uhuruphotos.foundation.strings.api.R.string
import com.savvasdalkitsis.uhuruphotos.implementation.heatmap.seam.HeatMapAction
import com.savvasdalkitsis.uhuruphotos.implementation.heatmap.seam.HeatMapAction.SelectedPhoto
import com.savvasdalkitsis.uhuruphotos.implementation.heatmap.ui.state.HeatMapGalleryDisplay
import com.savvasdalkitsis.uhuruphotos.implementation.heatmap.ui.state.HeatMapState

@Composable
fun HeatMapVisiblePhotos(
    modifier: Modifier = Modifier,
    loadingModifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
    state: HeatMapState,
    action: (HeatMapAction) -> Unit
) {
    if (state.allMedia.isEmpty()) {
        CircularProgressIndicator(modifier = loadingModifier)
    } else {
        Gallery(
            modifier = modifier,
            contentPadding = contentPadding,
            state = GalleryState(
                isLoading = false,
                galleryDisplay = HeatMapGalleryDisplay,
                albums = listOf(
                    Album(
                        id = "visiblePhotos",
                        photos = state.photosOnVisibleMap,
                        displayTitle = stringResource(string.photos_on_map, state.photosOnVisibleMap.size, state.allMedia.size),
                        location = null,
                    )
                )
            ),
            onMediaItemSelected = { photo, center, scale ->
                action(SelectedPhoto(photo, center, scale))
            },
        )
    }
}