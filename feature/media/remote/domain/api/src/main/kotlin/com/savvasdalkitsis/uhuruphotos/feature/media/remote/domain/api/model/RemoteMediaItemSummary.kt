/*
Copyright 2022 Savvas Dalkitsis

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
package com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.api.model

import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.entities.media.DbRemoteMediaItemSummary
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.entities.media.DbTrash
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RemoteMediaItemSummary(
    val id: String,
    val dominantColor: String,
    val url: String,
    val location: String,
    val date: String?,
    val birthTime: String,
    val aspectRatio: Float? = null,
    val type: String,
    @Json(name = "video_length")
    val videoLength: String,
    val rating: Int,
    @Json(name = "exif_gps_lat")
    val lat: String? = null,
    @Json(name = "exif_gps_lon")
    val lon: String? = null,
    val removed: Boolean = false,
    @Json(name = "in_trashcan")
    val inTrash: Boolean = false,
    val owner: RemoteMediaItemSummaryOwner,
)

fun RemoteMediaItemSummary.toDbModel(albumId: String) = DbRemoteMediaItemSummary(
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

fun RemoteMediaItemSummary.toTrash(albumId: String) = DbTrash(
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