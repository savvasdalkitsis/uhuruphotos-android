package com.savvasdalkitsis.librephotos.home.mvflow

import com.savvasdalkitsis.librephotos.feed.state.FeedState

sealed class HomeMutation {
    object Loading : HomeMutation()
    data class Loaded(val feedState: FeedState) : HomeMutation()
}
