/*
Copyright 2024 Savvas Dalkitsis

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
package com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.api.service.http.response

import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.entities.media.DbRemoteMediaItemSummary
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.entities.media.DbTrash
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RemoteMediaItemSummaryResponse(
    @field:Json(name = "id")
    val id: String,
    @field:Json(name = "dominantColor")
    val dominantColor: String,
    @field:Json(name = "url")
    val url: String,
    @field:Json(name = "location")
    val location: String,
    @field:Json(name = "date")
    val date: String?,
    @field:Json(name = "birthTime")
    val birthTime: String,
    @field:Json(name = "aspectRatio")
    val aspectRatio: Float? = null,
    @field:Json(name = "type")
    val type: String,
    @Json(name = "video_length")
    val videoLength: String,
    @Json(name = "rating")
    val rating: Int,
    @Json(name = "exif_gps_lat")
    val lat: String? = null,
    @Json(name = "exif_gps_lon")
    val lon: String? = null,
    @Json(name = "removed")
    val removed: Boolean = false,
    @Json(name = "in_trashcan")
    val inTrash: Boolean = false,
    @Json(name = "owner")
    val owner: RemoteMediaItemSummaryOwnerResponse,
)

fun RemoteMediaItemSummaryResponse.toDbModel(albumId: String) = DbRemoteMediaItemSummary(
    id = id,
    dominantColor = dominantColor,
    url = url,
    location = location,
    date = date,
    birthTime = birthTime,
    aspectRatio = aspectRatio,
    type = type,
    videoLength = videoLength,
    rating = rating,
    containerId = albumId,
    gpsLat = lat,
    gpsLon = lon
)

fun RemoteMediaItemSummaryResponse.toTrash(albumId: String) = DbTrash(
    id = id,
    dominantColor = dominantColor,
    url = url,
    location = location,
    date = date,
    birthTime = birthTime,
    aspectRatio = aspectRatio,
    type = type,
    videoLength = videoLength,
    rating = rating,
    containerId = albumId,
    gpsLat = lat,
    gpsLon = lon,
)