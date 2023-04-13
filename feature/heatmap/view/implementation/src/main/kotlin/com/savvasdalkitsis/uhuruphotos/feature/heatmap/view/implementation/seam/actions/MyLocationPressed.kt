package com.savvasdalkitsis.uhuruphotos.feature.heatmap.view.implementation.seam.actions

import android.annotation.SuppressLint
import android.location.LocationManager
import androidx.core.location.LocationManagerCompat
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.savvasdalkitsis.uhuruphotos.feature.heatmap.view.implementation.seam.HeatMapActionsContext
import com.savvasdalkitsis.uhuruphotos.feature.heatmap.view.implementation.seam.HeatMapEffect
import com.savvasdalkitsis.uhuruphotos.feature.heatmap.view.implementation.seam.HeatMapMutation
import com.savvasdalkitsis.uhuruphotos.feature.heatmap.view.implementation.ui.state.HeatMapState
import com.savvasdalkitsis.uhuruphotos.foundation.map.api.model.LatLon
import com.savvasdalkitsis.uhuruphotos.foundation.map.api.ui.MapViewState
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.EffectHandler
import kotlinx.coroutines.flow.flow

@SuppressLint("MissingPermission")
data class MyLocationPressed(
    val locationPermissionState: PermissionState,
    val mapViewState: MapViewState
) : HeatMapAction() {

    context(HeatMapActionsContext) override fun handle(
        state: HeatMapState,
        effect: EffectHandler<HeatMapEffect>
    ) = flow<HeatMapMutation> {
        if (locationPermissionState.status.isGranted) {
            var location: LatLon? = null
            LocationManagerCompat.getCurrentLocation(locationManager, LocationManager.NETWORK_PROVIDER, null, Runnable::run) {
                location = LatLon(it.latitude, it.longitude)
            }
            location?.let {
                mapViewState.centerToLocation(it)
            }
        } else {
            locationPermissionState.launchPermissionRequest()
        }
    }
}