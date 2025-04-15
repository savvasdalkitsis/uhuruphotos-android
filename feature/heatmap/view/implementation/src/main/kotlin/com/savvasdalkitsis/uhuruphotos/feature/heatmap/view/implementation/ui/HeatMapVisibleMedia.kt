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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.savvasdalkitsis.uhuruphotos.feature.collage.view.api.ui.Collage
import com.savvasdalkitsis.uhuruphotos.feature.collage.view.api.ui.state.ClusterState
import com.savvasdalkitsis.uhuruphotos.feature.collage.view.api.ui.state.CollageState
import com.savvasdalkitsis.uhuruphotos.feature.heatmap.view.implementation.seam.actions.HeatMapAction
import com.savvasdalkitsis.uhuruphotos.feature.heatmap.view.implementation.seam.actions.SelectedCel
import com.savvasdalkitsis.uhuruphotos.feature.heatmap.view.implementation.ui.state.HeatMapCollageDisplayState
import com.savvasdalkitsis.uhuruphotos.feature.heatmap.view.implementation.ui.state.HeatMapState
import kotlinx.collections.immutable.persistentListOf
import org.jetbrains.compose.resources.stringResource
import uhuruphotos_android.foundation.strings.api.generated.resources.Res.string
import uhuruphotos_android.foundation.strings.api.generated.resources.media_on_map

@Composable
fun HeatMapVisibleMedia(
    modifier: Modifier = Modifier,
    loadingModifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
    state: HeatMapState,
    action: (HeatMapAction) -> Unit
) {
    if (state.allMedia.isEmpty()) {
        CircularProgressIndicator(modifier = loadingModifier)
    } else {
        val displayTitle = stringResource(
            string.media_on_map,
            state.photosOnVisibleMap.size,
            state.allMedia.size
        )
        Collage(
            modifier = modifier,
            contentPadding = contentPadding,
            state = CollageState(
                isLoading = false,
                collageDisplayState = HeatMapCollageDisplayState,
                clusters = remember(displayTitle, state.photosOnVisibleMap) {
                    persistentListOf(
                        ClusterState(
                            id = "visibleItems",
                            cels = state.photosOnVisibleMap,
                            displayTitle = displayTitle,
                            location = null,
                        )
                    )
                },
            ),
            onCelSelected = { cel ->
                action(SelectedCel(cel))
            },
        )
    }
}