package com.savvasdalkitsis.uhuruphotos.feature.album.user.domain.implementation.service.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UserAlbum(
    val id: Int,
    val title: String,
    val date: String,
    val location: String,
    @field:Json(name = "grouped_photos")
    val groups: List<UserAlbumPhotoGroup>,
)