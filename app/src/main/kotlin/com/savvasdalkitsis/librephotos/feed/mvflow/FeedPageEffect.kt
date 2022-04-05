package com.savvasdalkitsis.librephotos.feed.mvflow

sealed class FeedPageEffect {
    data class OpenPhotoDetails(val id: String) : FeedPageEffect()
    object ReloadApp : FeedPageEffect()
}
