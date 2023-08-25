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

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.PermissionState
import com.savvasdalkitsis.uhuruphotos.feature.heatmap.view.implementation.seam.actions.BackPressed
import com.savvasdalkitsis.uhuruphotos.feature.heatmap.view.implementation.seam.actions.HeatMapAction
import com.savvasdalkitsis.uhuruphotos.feature.heatmap.view.implementation.seam.actions.MyLocationPressed
import com.savvasdalkitsis.uhuruphotos.feature.heatmap.view.implementation.ui.state.HeatMapState
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.R.drawable
import com.savvasdalkitsis.uhuruphotos.foundation.map.api.ui.MapViewState
import com.savvasdalkitsis.uhuruphotos.foundation.strings.api.R.string
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.ActionIcon
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.CommonTopBar
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.UpNavButton

@Composable
fun HeatMapTopBar(
    action: (HeatMapAction) -> Unit,
    state: HeatMapState,
    locationPermissionState: PermissionState,
    actionsInTitle: Boolean = false,
    mapViewState: MapViewState,
) {
    CommonTopBar(
        navigationIcon = {
            UpNavButton(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(MaterialTheme.colors.background)
            ) {
                action(BackPressed)
            }
        },
        title = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(stringResource(string.media_map))
                if (actionsInTitle) {
                    Actions(state, action, locationPermissionState, mapViewState)
                }
            }
        },
        topBarDisplayed = true,
        toolbarColor = { Color.Transparent },
        actionBarContent = {
            if (!actionsInTitle) {
                Actions(state, action, locationPermissionState, mapViewState)
            }
        }
    )
}

@Composable
private fun RowScope.Actions(
    state: HeatMapState,
    action: (HeatMapAction) -> Unit,
    locationPermissionState: PermissionState,
    mapViewState: MapViewState
) {
    AnimatedVisibility(visible = state.loading) {
        CircularProgressIndicator()
    }
    ActionIcon(
        onClick = { action(MyLocationPressed(locationPermissionState, mapViewState)) },
        icon = drawable.ic_my_location,
    )
}