package com.savvasdalkitsis.librephotos.home.state

import com.savvasdalkitsis.librephotos.feed.view.FeedState

data class HomeState(
    val isLoading: Boolean = true,
    val feedState: FeedState = FeedState(),
)