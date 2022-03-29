package com.savvasdalkitsis.librephotos.feed.mvflow

import androidx.work.WorkInfo
import com.savvasdalkitsis.librephotos.albums.model.Album

sealed class FeedMutation {
    object Loading : FeedMutation()
    object FinishedLoading : FeedMutation()
    data class ShowAlbums(val albums: List<Album>) : FeedMutation() {
        override fun toString() = "Showing ${albums.size} albums"
    }
    data class SyncJobUpdate(val state: WorkInfo.State) : FeedMutation()
}
