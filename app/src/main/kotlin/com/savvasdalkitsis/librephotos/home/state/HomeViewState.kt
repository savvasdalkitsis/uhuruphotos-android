package com.savvasdalkitsis.librephotos.home.state

import com.savvasdalkitsis.librephotos.feed.state.FeedState

data class HomeViewState(
    val isLoading: Boolean = true,
    val feedState: FeedState = FeedState(),
)