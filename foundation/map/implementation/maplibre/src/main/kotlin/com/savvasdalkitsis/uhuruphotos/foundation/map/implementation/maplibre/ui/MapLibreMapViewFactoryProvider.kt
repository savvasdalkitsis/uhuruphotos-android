package com.savvasdalkitsis.uhuruphotos.foundation.map.implementation.maplibre.ui

import com.savvasdalkitsis.uhuruphotos.foundation.map.api.model.MapProvider
import com.savvasdalkitsis.uhuruphotos.foundation.map.api.ui.MapViewFactory
import com.savvasdalkitsis.uhuruphotos.foundation.map.api.ui.MapViewFactoryProvider
import javax.inject.Inject

class MapLibreMapViewFactoryProvider @Inject constructor(
    private val mapLibreMapViewFactory: MapLibreMapViewFactory,
) : MapViewFactoryProvider {
    override fun getFactory(mapProvider: MapProvider): MapViewFactory? = when(mapProvider) {
        MapProvider.MapLibre ->  mapLibreMapViewFactory
        else -> null
    }
}
