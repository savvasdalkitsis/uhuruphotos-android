package com.savvasdalkitsis.uhuruphotos.feature.album.user.domain.implementation.service.model

import com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.api.model.RemoteMediaItemSummary
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UserAlbumPhotoGroup(
    val date: String,
    @field:Json(name = "items")
    val photos: List<RemoteMediaItemSummary>
)