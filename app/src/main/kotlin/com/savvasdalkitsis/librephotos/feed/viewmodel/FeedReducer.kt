package com.savvasdalkitsis.librephotos.feed.viewmodel

import com.savvasdalkitsis.librephotos.feed.mvflow.FeedMutation
import com.savvasdalkitsis.librephotos.feed.mvflow.FeedMutation.*
import com.savvasdalkitsis.librephotos.feed.view.state.FeedState
import net.pedroloureiro.mvflow.Reducer

class FeedReducer : Reducer<FeedState, FeedMutation> {

    override fun invoke(
        state: FeedState,
        mutation: FeedMutation
    ): FeedState = when (mutation) {
        is Loading -> state.copy(isLoading = true)
        is ShowAlbums -> state.copy(albums = mutation.albums)
        FinishedLoading -> state.copy(isLoading = false)
        is SyncJobUpdate -> state.copy(syncJobState = mutation.state)
    }

}
