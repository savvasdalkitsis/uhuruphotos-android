package com.savvasdalkitsis.uhuruphotos.foundation.map.implementation.mapbox.ui

import com.savvasdalkitsis.uhuruphotos.foundation.map.api.model.MapProvider
import com.savvasdalkitsis.uhuruphotos.foundation.map.api.ui.MapViewFactory
import com.savvasdalkitsis.uhuruphotos.foundation.map.api.ui.MapViewFactoryProvider
import javax.inject.Inject

class MapBoxMapViewFactoryProvider @Inject constructor(
    private val mapBoxMapViewFactory: MapBoxMapViewFactory,
) : MapViewFactoryProvider {
    override fun getFactory(mapProvider: MapProvider): MapViewFactory? = when(mapProvider) {
        MapProvider.MapBox ->  mapBoxMapViewFactory
        else -> null
    }
}
