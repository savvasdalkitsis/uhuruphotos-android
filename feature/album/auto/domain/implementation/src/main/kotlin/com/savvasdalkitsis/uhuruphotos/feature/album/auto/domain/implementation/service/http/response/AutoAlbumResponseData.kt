/*
Copyright 2023 Savvas Dalkitsis

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
package com.savvasdalkitsis.uhuruphotos.feature.album.auto.domain.implementation.service.http.response

import com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.api.service.http.response.RemoteMediaItemResponseData
import com.savvasdalkitsis.uhuruphotos.feature.people.domain.api.service.http.response.PersonResponseData
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AutoAlbumResponseData(
    @field:Json(name = "created_on")
    val createdOn: String,
    @field:Json(name = "favorited")
    val isFavorite: Boolean,
    @field:Json(name = "gps_lat")
    val gpsLat: Double?,
    @field:Json(name = "gps_lon")
    val gpsLon: Double?,
    @field:Json(name = "id")
    val id: Int,
    @field:Json(name = "timestamp")
    val timestamp: String,
    @field:Json(name = "title")
    val title: String,
    @field:Json(name = "photos")
    val photos: List<RemoteMediaItemResponseData>,
    @field:Json(name = "people")
    val people: List<PersonResponseData>,
)
