package com.savvasdalkitsis.uhuruphotos.foundation.map.implementation.google.ui

import com.google.android.gms.maps.model.CameraPosition
import com.google.maps.android.compose.CameraPositionState
import com.savvasdalkitsis.uhuruphotos.foundation.map.api.model.LatLon
import com.savvasdalkitsis.uhuruphotos.foundation.map.api.model.MapProvider
import com.savvasdalkitsis.uhuruphotos.foundation.map.api.model.MapProvider.Google
import com.savvasdalkitsis.uhuruphotos.foundation.map.api.ui.MapViewState
import com.savvasdalkitsis.uhuruphotos.foundation.map.api.ui.MapViewStateFactory
import javax.inject.Inject

class GoogleMapViewStateFactory @Inject constructor(
) : MapViewStateFactory {

    override fun create(
        mapProvider: MapProvider,
        initialPosition: LatLon,
        initialZoom: Float,
    ): MapViewState? {
        return when (mapProvider) {
            Google -> {
                val cameraPosition =
                    CameraPosition.fromLatLngZoom(initialPosition.toLatLng, initialZoom)
                GoogleMapViewState(CameraPositionState(cameraPosition), initialPosition, initialZoom)
            }
            else -> null
            }
        }
}