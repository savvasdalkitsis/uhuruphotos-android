package com.savvasdalkitsis.uhuruphotos.foundation.map.api.ui

import com.savvasdalkitsis.uhuruphotos.foundation.map.api.model.LatLon
import com.savvasdalkitsis.uhuruphotos.foundation.map.api.model.MapProvider

interface MapViewStateFactory {

    fun create(
        mapProvider: MapProvider,
        initialPosition: LatLon,
        initialZoom: Float,
    ): MapViewState?
}