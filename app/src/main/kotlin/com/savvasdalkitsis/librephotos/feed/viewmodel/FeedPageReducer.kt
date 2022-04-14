package com.savvasdalkitsis.librephotos.feed.viewmodel

import com.savvasdalkitsis.librephotos.feed.mvflow.FeedPageMutation
import com.savvasdalkitsis.librephotos.feed.mvflow.FeedPageMutation.*
import com.savvasdalkitsis.librephotos.feed.view.state.FeedPageState
import com.savvasdalkitsis.librephotos.feed.view.state.FeedState

fun feedPageReducer() : com.savvasdalkitsis.librephotos.viewmodel.Reducer<FeedPageState, FeedPageMutation> = { state, mutation ->
    when (mutation) {
        is Loading -> state.mutateFeed { copy(isLoading = true) }
        is ShowAlbums -> state.mutateFeed { copy(albums = mutation.albums) }
        FinishedLoading -> state.mutateFeed { copy(isLoading = false) }
        is UserBadgeUpdate -> state.copy(userBadgeState = mutation.state)
        ShowAccountOverview -> state.copy(showAccountOverview = true)
        HideAccountOverview -> state.copy(showAccountOverview = false)
        StartRefreshing -> state.copy(isRefreshing = true)
        StopRefreshing -> state.copy(isRefreshing = false)
        is ChangeDisplay -> state.mutateFeed { copy(feedDisplay = mutation.display) }
        HideFeedDisplayChoice -> state.copy(showFeedDisplayChoice = false)
        ShowFeedDisplayChoice -> state.copy(showFeedDisplayChoice = true)
    }
}

private fun FeedPageState.mutateFeed(feedStateMutation: FeedState.() -> FeedState) =
    copy(feedState = feedStateMutation(feedState))
