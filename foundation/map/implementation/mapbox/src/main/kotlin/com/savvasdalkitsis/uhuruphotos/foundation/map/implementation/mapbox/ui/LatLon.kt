package com.savvasdalkitsis.uhuruphotos.foundation.map.implementation.mapbox.ui

import com.mapbox.geojson.Point
import com.savvasdalkitsis.uhuruphotos.foundation.map.api.model.LatLon

internal val LatLon.toPoint get() = Point.fromLngLat(lon, lat)
