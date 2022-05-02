package com.savvasdalkitsis.uhuruphotos.heatmap.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.WindowInsetsSides.Companion.Bottom
import androidx.compose.foundation.layout.WindowInsetsSides.Companion.End
import androidx.compose.foundation.layout.WindowInsetsSides.Companion.Top
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.google.accompanist.permissions.PermissionState
import com.savvasdalkitsis.uhuruphotos.heatmap.view.state.HeatMapState
import com.savvasdalkitsis.uhuruphotos.heatmap.viewmodel.HeatMapAction
import com.savvasdalkitsis.uhuruphotos.ui.insets.insetsTop
import com.savvasdalkitsis.uhuruphotos.ui.insets.systemPadding
import com.savvasdalkitsis.uhuruphotos.ui.view.CommonScaffold

@Composable
fun SidePanelHeatMap(
    state: HeatMapState,
    action: (HeatMapAction) -> Unit,
    locationPermissionState: PermissionState
) {
    CommonScaffold(
        topBar = {
            HeatMapTopBar(
                action = action,
                state = state,
                locationPermissionState = locationPermissionState,
                actionsInTitle = true,
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
                state = state
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