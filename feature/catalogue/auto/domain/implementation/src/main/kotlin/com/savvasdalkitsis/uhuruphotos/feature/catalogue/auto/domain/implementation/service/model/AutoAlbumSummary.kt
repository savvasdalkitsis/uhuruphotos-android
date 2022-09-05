package com.savvasdalkitsis.uhuruphotos.feature.catalogue.auto.domain.implementation.service.model

import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.album.auto.AutoAlbums
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AutoAlbumSummary(
    @field:Json(name = "favorited")
    val isFavorite: Boolean,
    val id: Int,
    @field:Json(name = "photo_count")
    val photoCount: Int,
    @field:Json(name = "photos")
    val coverPhoto: AutoAlbumPhoto,
    val timestamp: String,
    val title: String,
)

fun AutoAlbumSummary.toAutoAlbums() = AutoAlbums(
    id = id,
    isFavorite = isFavorite,
    photoCount = photoCount,
    coverPhotoHash = coverPhoto.imageHash,
    coverPhotoIsVideo = coverPhoto.video,
    timestamp = timestamp,
    title = title,
)