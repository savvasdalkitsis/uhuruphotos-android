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

import com.savvasdalkitsis.uhuruphotos.api.db.albums.UserAlbums
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UserAlbumSummary(
    @field:Json(name = "favorited")
    val isFavorite: Boolean,
    val id: Int,
    @field:Json(name = "photo_count")
    val photoCount: Int,
    @field:Json(name = "cover_photos")
    val coverPhotos: List<UserAlbumCoverPhoto>?,
    @field:Json(name = "cover_photo")
    val coverPhoto: UserAlbumCoverPhoto?,
    @field:Json(name = "created_on")
    val createdOn: String,
    val title: String,
)

fun UserAlbumSummary.toUserAlbums() = UserAlbums(
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

private fun UserAlbumSummary.primaryCoverHash(): String? =
    coverHash(1) ?: coverPhoto?.imageHash

private fun UserAlbumSummary.coverHash(index: Int): String? =
    coverPhotos?.getOrNull(index - 1)?.imageHash

private fun UserAlbumSummary.primaryCoverIsVideo(): Boolean? =
    coverIsVideo(1) ?: coverPhoto?.video

private fun UserAlbumSummary.coverIsVideo(index: Int): Boolean? =
    coverPhotos?.getOrNull(index - 1)?.video
