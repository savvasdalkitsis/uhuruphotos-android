package com.savvasdalkitsis.librephotos.photos.model

data class Photos(
    val paths: List<String>,
    val finished: Boolean = false
)
