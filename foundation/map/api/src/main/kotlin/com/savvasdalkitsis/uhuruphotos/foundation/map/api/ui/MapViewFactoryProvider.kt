package com.savvasdalkitsis.uhuruphotos.foundation.map.api.ui

import com.savvasdalkitsis.uhuruphotos.foundation.map.api.model.MapProvider

interface MapViewFactoryProvider {

    fun getFactory(mapProvider: MapProvider): MapViewFactory?
}