package com.savvasdalkitsis.uhuruphotos.heatmap.view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
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
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.savvasdalkitsis.uhuruphotos.heatmap.view.state.HeatMapState
import com.savvasdalkitsis.uhuruphotos.heatmap.viewmodel.HeatMapAction
import com.savvasdalkitsis.uhuruphotos.icons.R
import com.savvasdalkitsis.uhuruphotos.ui.view.ActionIcon
import com.savvasdalkitsis.uhuruphotos.ui.view.BackNavButton
import com.savvasdalkitsis.uhuruphotos.ui.view.CommonTopBar

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
                Text("Photo map")
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
            icon = R.drawable.ic_my_location,
        )
    }
}