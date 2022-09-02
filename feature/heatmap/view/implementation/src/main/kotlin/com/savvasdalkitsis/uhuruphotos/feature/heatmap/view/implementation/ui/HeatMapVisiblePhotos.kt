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
package com.savvasdalkitsis.uhuruphotos.feature.heatmap.view.implementation.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.savvasdalkitsis.uhuruphotos.feature.collage.view.api.ui.Collage
import com.savvasdalkitsis.uhuruphotos.feature.collage.view.api.ui.state.Cluster
import com.savvasdalkitsis.uhuruphotos.feature.collage.view.api.ui.state.CollageState
import com.savvasdalkitsis.uhuruphotos.feature.heatmap.view.implementation.seam.HeatMapAction
import com.savvasdalkitsis.uhuruphotos.feature.heatmap.view.implementation.seam.HeatMapAction.SelectedCel
import com.savvasdalkitsis.uhuruphotos.feature.heatmap.view.implementation.ui.state.HeatMapCollageDisplay
import com.savvasdalkitsis.uhuruphotos.feature.heatmap.view.implementation.ui.state.HeatMapState
import com.savvasdalkitsis.uhuruphotos.feature.media.common.view.api.ui.state.toCel
import com.savvasdalkitsis.uhuruphotos.foundation.strings.api.R.string

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
        Collage(
            modifier = modifier,
            contentPadding = contentPadding,
            state = CollageState(
                isLoading = false,
                collageDisplay = HeatMapCollageDisplay,
                clusters = listOf(
                    Cluster(
                        id = "visibleItems",
                        cels = state.photosOnVisibleMap.map { it.toCel() },
                        displayTitle = stringResource(string.photos_on_map, state.photosOnVisibleMap.size, state.allMedia.size),
                        location = null,
                    )
                ),
            ),
            onCelSelected = { cel, center, scale ->
                action(SelectedCel(cel, center, scale))
            },
        )
    }
}