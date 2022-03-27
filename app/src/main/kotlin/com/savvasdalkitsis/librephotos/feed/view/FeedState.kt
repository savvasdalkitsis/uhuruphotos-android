package com.savvasdalkitsis.librephotos.feed.view

import com.savvasdalkitsis.librephotos.photos.model.Photo

data class FeedState(
    val photos: List<Photo> = emptyList(),
) {
    override fun toString() = "Feed with size: ${photos.size}. First photo: ${photos.firstOrNull()}"
}