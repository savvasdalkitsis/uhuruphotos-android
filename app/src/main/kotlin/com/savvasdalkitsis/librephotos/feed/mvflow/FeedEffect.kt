package com.savvasdalkitsis.librephotos.feed.mvflow

sealed class FeedEffect {
    object ReloadApp : FeedEffect()
}
