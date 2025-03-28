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
package com.savvasdalkitsis.uhuruphotos.feature.catalogue.auto.domain.implementation.service.http.response

import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.album.auto.AutoAlbums
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AutoAlbumSummaryResponse(
    @field:Json(name = "favorited")
    val isFavorite: Boolean,
    @field:Json(name = "id")
    val id: Int,
    @field:Json(name = "photo_count")
    val photoCount: Int,
    @field:Json(name = "photos")
    val coverPhoto: AutoAlbumPhotoResponse,
    @field:Json(name = "timestamp")
    val timestamp: String,
    @field:Json(name = "title")
    val title: String,
)

fun AutoAlbumSummaryResponse.toAutoAlbums() = AutoAlbums(
    id = id,
    isFavorite = isFavorite,
    photoCount = photoCount,
    coverPhotoHash = coverPhoto.imageHash,
    coverPhotoIsVideo = coverPhoto.video,
    timestamp = timestamp,
    title = title,
)
