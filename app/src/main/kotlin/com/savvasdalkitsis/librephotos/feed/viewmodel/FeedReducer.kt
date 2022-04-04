package com.savvasdalkitsis.librephotos.feed.viewmodel

import com.savvasdalkitsis.librephotos.feed.mvflow.FeedPageMutation
import com.savvasdalkitsis.librephotos.feed.mvflow.FeedPageMutation.*
import com.savvasdalkitsis.librephotos.feed.view.state.FeedPageState
import com.savvasdalkitsis.librephotos.viewmodel.Reducer

fun feedPageReducer() : Reducer<FeedPageState, FeedPageMutation> = { state, mutation ->
    when (mutation) {
        is Loading -> state.copy(feedState = state.feedState.copy(isLoading = true))
        is ShowAlbums -> state.copy(feedState = state.feedState.copy(albums = mutation.albums))
        FinishedLoading -> state.copy(feedState = state.feedState.copy(isLoading = false))
        is UserBadgeUpdate -> state.copy(userBadgeState = mutation.state)
        ShowAccountOverview -> state.copy(showAccountOverview = true)
        HideAccountOverview -> state.copy(showAccountOverview = false)
        StartRefreshing -> state.copy(isRefreshing = true)
        StopRefreshing -> state.copy(isRefreshing = false)
    }
}
