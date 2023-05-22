package com.savvasdalkitsis.uhuruphotos.foundation.map.api.ui

import com.savvasdalkitsis.uhuruphotos.foundation.map.api.model.LatLon
import com.savvasdalkitsis.uhuruphotos.foundation.map.api.model.MapProvider

class CompositeMapViewStateFactory(
    private val factories: Set<MapViewStateFactory>,
) : MapViewStateFactory {

    override fun create(
        mapProvider: MapProvider,
        initialPosition: LatLon,
        initialZoom: Float
    ): MapViewState = factories.firstNotNullOf {
        it.create(mapProvider, initialPosition, initialZoom)
    }
}