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
package com.savvasdalkitsis.uhuruphotos.feature.catalogue.user.domain.implementation.service.http.response

import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.album.user.UserAlbums
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UserAlbumSummaryResponse(
    @field:Json(name = "favorited")
    val isFavorite: Boolean,
    @field:Json(name = "id")
    val id: Int,
    @field:Json(name = "photo_count")
    val photoCount: Int,
    @field:Json(name = "cover_photos")
    val coverPhotos: List<UserAlbumCoverPhotoResponse>?,
    @field:Json(name = "cover_photo")
    val coverPhoto: UserAlbumCoverPhotoResponse?,
    @field:Json(name = "created_on")
    val createdOn: String,
    @field:Json(name = "title")
    val title: String,
)

fun UserAlbumSummaryResponse.toUserAlbums() = UserAlbums(
    id = id,
    isFavorite = isFavorite,
    photoCount = photoCount,
    coverPhoto1Hash = primaryCoverHash(),
    coverPhoto1IsVideo = primaryCoverIsVideo(),
    coverPhoto2Hash = coverHash(2),
    coverPhoto2IsVideo = coverIsVideo(2),
    coverPhoto3Hash = coverHash(3),
    coverPhoto3IsVideo = coverIsVideo(3),
    coverPhoto4Hash = coverHash(4),
    coverPhoto4IsVideo = coverIsVideo(4),
    timestamp = createdOn,
    title = title,
)

private fun UserAlbumSummaryResponse.primaryCoverHash(): String? =
    coverHash(1) ?: coverPhoto?.imageHash

private fun UserAlbumSummaryResponse.coverHash(index: Int): String? =
    coverPhotos?.getOrNull(index - 1)?.imageHash

private fun UserAlbumSummaryResponse.primaryCoverIsVideo(): Boolean? =
    coverIsVideo(1) ?: coverPhoto?.video

private fun UserAlbumSummaryResponse.coverIsVideo(index: Int): Boolean? =
    coverPhotos?.getOrNull(index - 1)?.video
