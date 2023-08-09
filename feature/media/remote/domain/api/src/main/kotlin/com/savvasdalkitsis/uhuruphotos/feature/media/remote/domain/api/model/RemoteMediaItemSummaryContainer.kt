package com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.api.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RemoteMediaItemSummaryContainer(
    @Json(name = "album_date_id") val albumDateId: String,
    @Json(name = "photo_summary") val photoSummary: RemoteMediaItemSummary,
)