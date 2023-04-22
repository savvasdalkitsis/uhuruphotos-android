/*
Copyright 2023 Savvas Dalkitsis

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
