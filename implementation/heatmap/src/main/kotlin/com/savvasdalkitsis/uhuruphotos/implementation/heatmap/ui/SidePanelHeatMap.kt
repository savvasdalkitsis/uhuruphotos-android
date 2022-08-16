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

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsetsSides.Companion.Bottom
import androidx.compose.foundation.layout.WindowInsetsSides.Companion.End
import androidx.compose.foundation.layout.WindowInsetsSides.Companion.Top
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.google.accompanist.permissions.PermissionState
import com.savvasdalkitsis.uhuruphotos.foundation.map.api.ui.MapViewState
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.insets.systemPadding
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.CommonScaffold
import com.savvasdalkitsis.uhuruphotos.implementation.heatmap.seam.HeatMapAction
import com.savvasdalkitsis.uhuruphotos.implementation.heatmap.ui.state.HeatMapState

@Composable
fun SidePanelHeatMap(
    state: HeatMapState,
    action: (HeatMapAction) -> Unit,
    locationPermissionState: PermissionState,
    mapViewState: MapViewState
) {

    CommonScaffold(
        topBar = {
            HeatMapTopBar(
                action = action,
                state = state,
                locationPermissionState = locationPermissionState,
                actionsInTitle = true,
                mapViewState = mapViewState,
            )
        },
        bottomBarDisplayed = false,
    ) {
        Row {
            HeatMapContent(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(2f),
                action = action,
                locationPermissionState = locationPermissionState,
                state = state,
                mapViewState = mapViewState,
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
            ) {
                HeatMapVisiblePhotos(
                    loadingModifier = Modifier
                        .align(Alignment.Center),
                    contentPadding = systemPadding(Top + Bottom + End),
                    state = state,
                    action = action,
                )
            }
        }
    }
}