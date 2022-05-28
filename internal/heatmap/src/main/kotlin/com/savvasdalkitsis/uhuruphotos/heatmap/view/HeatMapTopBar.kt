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
import com.google.accompanist.permissions.isGranted
import com.savvasdalkitsis.uhuruphotos.heatmap.view.state.HeatMapState
import com.savvasdalkitsis.uhuruphotos.heatmap.viewmodel.HeatMapAction
import com.savvasdalkitsis.uhuruphotos.ui.view.ActionIcon
import com.savvasdalkitsis.uhuruphotos.ui.view.BackNavButton
import com.savvasdalkitsis.uhuruphotos.ui.view.CommonTopBar
import com.savvasdalkitsis.uhuruphotos.icons.R as Icons
import com.savvasdalkitsis.uhuruphotos.strings.R as Strings

@Composable
fun HeatMapTopBar(
    action: (HeatMapAction) -> Unit,
    state: HeatMapState,
    locationPermissionState: PermissionState,
    actionsInTitle: Boolean = false,
) {
    CommonTopBar(
        navigationIcon = {
            BackNavButton(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(MaterialTheme.colors.background)
            ) {
                action(HeatMapAction.BackPressed)
            }
        },
        title = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(stringResource(Strings.string.photo_map))
                if (actionsInTitle) {
                    Actions(state, locationPermissionState)
                }
            }
        },
        topBarDisplayed = true,
        toolbarColor = { Color.Transparent },
        actionBarContent = {
            if (!actionsInTitle) {
                Actions(state, locationPermissionState)
            }
        }
    )
}

@Composable
private fun RowScope.Actions(
    state: HeatMapState,
    locationPermissionState: PermissionState
) {
    AnimatedVisibility(visible = state.loading) {
        CircularProgressIndicator()
    }
    AnimatedVisibility(visible = !locationPermissionState.status.isGranted) {
        ActionIcon(
            onClick = { locationPermissionState.launchPermissionRequest() },
            icon = Icons.drawable.ic_my_location,
        )
    }
}