package com.savvasdalkitsis.uhuruphotos.foundation.map.implementation.maplibre.ui

import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.geometry.LatLng
import com.savvasdalkitsis.uhuruphotos.foundation.map.api.model.LatLon

val LatLon.toLatLng get() = LatLng(lat, lon)
val LatLon.toPoint get() = Point.fromLngLat(lon, lat)