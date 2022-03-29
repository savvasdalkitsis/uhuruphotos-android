package com.savvasdalkitsis.librephotos.feed.viewmodel

import com.savvasdalkitsis.librephotos.feed.mvflow.FeedMutation
import com.savvasdalkitsis.librephotos.feed.mvflow.FeedMutation.*
import com.savvasdalkitsis.librephotos.feed.view.state.FeedPageState
import com.savvasdalkitsis.librephotos.feed.view.state.FeedState
import net.pedroloureiro.mvflow.Reducer

class FeedReducer : Reducer<FeedPageState, FeedMutation> {

    override fun invoke(
        state: FeedPageState,
        mutation: FeedMutation
    ): FeedPageState = when (mutation) {
        is Loading -> state.copy(feedState = state.feedState.copy(isLoading = true))
        is ShowAlbums -> state.copy(feedState = state.feedState.copy(albums = mutation.albums))
        FinishedLoading -> state.copy(feedState = state.feedState.copy(isLoading = false))
        is UserBadgeUpdate -> state.copy(userBadgeState = mutation.state)
    }

}
