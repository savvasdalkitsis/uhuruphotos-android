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

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomSheetScaffold
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import com.google.accompanist.permissions.PermissionState
import com.savvasdalkitsis.uhuruphotos.feature.heatmap.view.implementation.seam.actions.HeatMapAction
import com.savvasdalkitsis.uhuruphotos.feature.heatmap.view.implementation.seam.actions.NeverAskForLocalMediaAccessPermissionRequest
import com.savvasdalkitsis.uhuruphotos.feature.heatmap.view.implementation.ui.state.HeatMapState
import com.savvasdalkitsis.uhuruphotos.feature.media.local.view.api.ui.LocalMediaAccessRequestBanner
import com.savvasdalkitsis.uhuruphotos.foundation.map.api.ui.MapViewState
import com.savvasdalkitsis.uhuruphotos.foundation.strings.api.Res.strings
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.insets.insetsTop
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.sheet.SheetHandle
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.sheet.SheetSize
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.sheet.adjustingSheetSize

@Composable
fun BottomPanelHeatMap(
    state: HeatMapState,
    action: (HeatMapAction) -> Unit,
    locationPermissionState: PermissionState,
    mapViewState: MapViewState
) {
    val sheetSize: SheetSize = SheetSize.rememberSheetSize()
    val sheetPeekHeight = 320.dp

    BottomSheetScaffold(
        sheetPeekHeight = sheetPeekHeight,
        sheetShape = MaterialTheme.shapes.large,
        sheetContent = {
            val height = max(sheetPeekHeight, sheetSize.size.value.height - insetsTop() - 120.dp)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = height, max = height)
            ) {
                SheetHandle()
                HeatMapVisibleMedia(
                    loadingModifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = (sheetPeekHeight / 2) - 48.dp),
                    state = state,
                    action = action,
                )
            }
        },
    ) {
        Box(
            modifier = Modifier
                .adjustingSheetSize(sheetSize)
        ) {
            HeatMapContent(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = sheetPeekHeight - 12.dp),
                action = action,
                locationPermissionState = locationPermissionState,
                state = state,
                mapViewState = mapViewState,
            )
            Column {
                HeatMapTopBar(action, state, locationPermissionState, mapViewState = mapViewState)
                if (state.showRequestPermissionForLocalMediaAccess != null) {
                    LocalMediaAccessRequestBanner(
                        modifier = Modifier.padding(16.dp),
                        missingPermissions = state.showRequestPermissionForLocalMediaAccess,
                        description = strings.missing_local_media_permissions_photo_map,
                    ) {
                        action(NeverAskForLocalMediaAccessPermissionRequest)
                    }
                }
            }
        }
    }
}