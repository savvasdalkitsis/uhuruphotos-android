package com.savvasdalkitsis.uhuruphotos.heatmap.view

import android.content.res.Configuration
import android.content.res.Configuration.ORIENTATION_LANDSCAPE
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalConfiguration
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.savvasdalkitsis.uhuruphotos.heatmap.view.state.HeatMapState
import com.savvasdalkitsis.uhuruphotos.heatmap.viewmodel.HeatMapAction

@Composable
fun HeatMap(
    state: HeatMapState,
    action: (HeatMapAction) -> Unit
) {
    val locationPermissionState = rememberPermissionState(
        android.Manifest.permission.ACCESS_FINE_LOCATION
    )

    when (LocalConfiguration.current.orientation) {
        ORIENTATION_LANDSCAPE -> SidePanelHeatMap(state, action, locationPermissionState)
        else -> BottomPanelHeatMap(state, action, locationPermissionState)
    }

    LaunchedEffect(locationPermissionState.status) {
        if (!locationPermissionState.status.isGranted
            && !locationPermissionState.status.shouldShowRationale
        ) {
            locationPermissionState.launchPermissionRequest()
        }
    }
}