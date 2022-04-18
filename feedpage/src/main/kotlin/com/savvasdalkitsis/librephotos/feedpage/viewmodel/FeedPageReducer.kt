package com.savvasdalkitsis.librephotos.feedpage.viewmodel

import com.savvasdalkitsis.librephotos.albums.model.Album
import com.savvasdalkitsis.librephotos.feedpage.mvflow.FeedPageMutation
import com.savvasdalkitsis.librephotos.feedpage.mvflow.FeedPageMutation.*
import com.savvasdalkitsis.librephotos.feedpage.view.state.FeedPageState
import com.savvasdalkitsis.librephotos.feed.view.state.FeedState
import com.savvasdalkitsis.librephotos.photos.model.Photo

fun feedPageReducer() : com.savvasdalkitsis.librephotos.viewmodel.Reducer<FeedPageState, FeedPageMutation> = { state, mutation ->
    when (mutation) {
        is Loading -> state.copyFeed { copy(isLoading = true) }
        is ShowAlbums -> state.copyFeed { copy(albums = mutation.albums) }
        FinishedLoading -> state.copyFeed { copy(isLoading = false) }
        is UserBadgeUpdate -> state.copy(userBadgeState = mutation.state)
        ShowAccountOverview -> state.copy(showAccountOverview = true)
        HideAccountOverview -> state.copy(showAccountOverview = false)
        StartRefreshing -> state.copy(isRefreshing = true)
        StopRefreshing -> state.copy(isRefreshing = false)
        is ChangeDisplay -> state.copyFeed { copy(feedDisplay = mutation.display) }
        HideFeedDisplayChoice -> state.copy(showFeedDisplayChoice = false)
        ShowFeedDisplayChoice -> state.copy(showFeedDisplayChoice = true)
        ShowDeletionConfirmationDialog -> state.copy(showPhotoDeletionConfirmationDialog = true)
        HideDeletionConfirmationDialog -> state.copy(showPhotoDeletionConfirmationDialog = false)
    }
}

private fun FeedPageState.copyFeed(feedStateMutation: FeedState.() -> FeedState) =
    copy(feedState = feedStateMutation(feedState))
