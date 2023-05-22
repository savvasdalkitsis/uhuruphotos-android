package com.savvasdalkitsis.uhuruphotos.foundation.map.implementation.maplibre.ui

import com.savvasdalkitsis.uhuruphotos.foundation.map.api.model.LatLon
import com.savvasdalkitsis.uhuruphotos.foundation.map.api.model.MapProvider
import com.savvasdalkitsis.uhuruphotos.foundation.map.api.ui.MapViewState
import com.savvasdalkitsis.uhuruphotos.foundation.map.api.ui.MapViewStateFactory
import javax.inject.Inject

class MapLibreMapViewStateFactory @Inject constructor(
): MapViewStateFactory {
    override fun create(
        mapProvider: MapProvider,
        initialPosition: LatLon,
        initialZoom: Float
    ): MapViewState? = when (mapProvider) {
        MapProvider.MapLibre -> MapLibreMapViewState(initialPosition, initialZoom)
        else -> null
    }

}
