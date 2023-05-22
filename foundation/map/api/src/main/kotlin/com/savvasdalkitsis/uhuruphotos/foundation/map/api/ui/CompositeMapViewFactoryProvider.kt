package com.savvasdalkitsis.uhuruphotos.foundation.map.api.ui

import com.savvasdalkitsis.uhuruphotos.foundation.map.api.model.MapProvider

class CompositeMapViewFactoryProvider(
    private val providers: Set<MapViewFactoryProvider>,
) : MapViewFactoryProvider {

    override fun getFactory(mapProvider: MapProvider): MapViewFactory =
        providers.firstNotNullOf { it.getFactory(mapProvider) }
}