package com.savvasdalkitsis.uhuruphotos.foundation.map.implementation.google.ui

import com.savvasdalkitsis.uhuruphotos.foundation.map.api.model.MapProvider
import com.savvasdalkitsis.uhuruphotos.foundation.map.api.ui.MapViewFactory
import com.savvasdalkitsis.uhuruphotos.foundation.map.api.ui.MapViewFactoryProvider
import javax.inject.Inject

class GoogleMapViewFactoryProvider @Inject constructor(
    private val googleMapViewFactory: GoogleMapViewFactory,
): MapViewFactoryProvider {

    override fun getFactory(mapProvider: MapProvider): MapViewFactory? =
        if (mapProvider == MapProvider.Google) {
            googleMapViewFactory
        } else {
            null
        }
}
