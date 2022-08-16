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
package com.savvasdalkitsis.uhuruphotos.api.albums.service.model

import com.savvasdalkitsis.uhuruphotos.api.media.remote.model.RemoteMediaItem
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
