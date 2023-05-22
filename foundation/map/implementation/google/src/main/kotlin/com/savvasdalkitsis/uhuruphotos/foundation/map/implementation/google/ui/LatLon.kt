package com.savvasdalkitsis.uhuruphotos.foundation.map.implementation.google.ui

import com.google.android.gms.maps.model.LatLng
import com.savvasdalkitsis.uhuruphotos.foundation.map.api.model.LatLon

internal val LatLon.toLatLng get() = LatLng(lat, lon)
