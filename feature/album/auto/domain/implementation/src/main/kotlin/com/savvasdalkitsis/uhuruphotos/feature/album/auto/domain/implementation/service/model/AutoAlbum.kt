package com.savvasdalkitsis.uhuruphotos.feature.album.auto.domain.implementation.service.model

import com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.api.model.RemoteMediaItem
import com.savvasdalkitsis.uhuruphotos.feature.people.domain.api.service.model.PersonResult
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AutoAlbum(
    @field:Json(name = "created_on")
    val createdOn: String,
    @field:Json(name = "favorited")
    val isFavorite: Boolean,
    @field:Json(name = "gps_lat")
    val gpsLat: Double?,
    @field:Json(name = "gps_lon")
    val gpsLon: Double?,
    val id: Int,
    val timestamp: String,
    val title: String,
    val photos: List<RemoteMediaItem>,
    val people: List<PersonResult>,
)