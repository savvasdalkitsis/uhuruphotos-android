package com.savvasdalkitsis.librephotos.feed.view.state

import androidx.work.WorkInfo
import com.savvasdalkitsis.librephotos.albums.model.Album

data class FeedState(
    val isLoading: Boolean = true,
    val albums: List<Album> = emptyList(),
    val syncJobState: WorkInfo.State? = null,
) {
    override fun toString() = "Feed with ${albums.size} albums."
}