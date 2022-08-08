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
package com.savvasdalkitsis.uhuruphotos.api.media.remote.model

import com.savvasdalkitsis.uhuruphotos.api.db.domain.model.media.DbRemoteMediaItemSummary
import com.savvasdalkitsis.uhuruphotos.api.db.domain.model.media.DbTrash
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
    val aspectRatio: Float,
    val type: String,
    @Json(name = "video_length") val videoLength: String,
    val rating: Int
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
    containerId = albumId
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
    containerId = albumId
)