package com.savvasdalkitsis.librephotos.feed.mvflow

sealed class FeedPageEffect {
    object ReloadApp : FeedPageEffect()
}
