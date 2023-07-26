package com.savvasdalkitsis.uhuruphotos.foundation.image.api.video

internal data class VideoRequestParam(
    val url: String,
    val headers: Map<String, String>?,
)