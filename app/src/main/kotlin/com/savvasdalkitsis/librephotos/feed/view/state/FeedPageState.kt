package com.savvasdalkitsis.librephotos.feed.view.state

import com.savvasdalkitsis.librephotos.userbadge.view.state.UserBadgeState

data class FeedPageState(
    val feedState: FeedState = FeedState(),
    val userBadgeState: UserBadgeState = UserBadgeState(),
    val showAccountOverview: Boolean = false,
    val isRefreshing: Boolean = false,
    val showFeedDisplayChoice: Boolean = false,
)