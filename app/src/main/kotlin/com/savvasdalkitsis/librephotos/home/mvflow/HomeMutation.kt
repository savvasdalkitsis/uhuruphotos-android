package com.savvasdalkitsis.librephotos.home.mvflow

import com.savvasdalkitsis.librephotos.feed.view.FeedState

sealed class HomeMutation {
    object Loading : HomeMutation()
    data class PartiallyLoaded(val feedState: FeedState) : HomeMutation()
    data class Loaded(val feedState: FeedState) : HomeMutation()
}
