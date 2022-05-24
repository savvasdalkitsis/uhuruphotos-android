/*
Copyright 2022 Savvas Dalkitsis

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
package com.savvasdalkitsis.uhuruphotos.feedpage.viewmodel

import com.savvasdalkitsis.uhuruphotos.feed.view.state.FeedState
import com.savvasdalkitsis.uhuruphotos.feedpage.mvflow.FeedPageMutation
import com.savvasdalkitsis.uhuruphotos.feedpage.mvflow.FeedPageMutation.ChangeDisplay
import com.savvasdalkitsis.uhuruphotos.feedpage.mvflow.FeedPageMutation.FinishedLoading
import com.savvasdalkitsis.uhuruphotos.feedpage.mvflow.FeedPageMutation.HideAccountOverview
import com.savvasdalkitsis.uhuruphotos.feedpage.mvflow.FeedPageMutation.HideDeletionConfirmationDialog
import com.savvasdalkitsis.uhuruphotos.feedpage.mvflow.FeedPageMutation.HideLogOutConfirmation
import com.savvasdalkitsis.uhuruphotos.feedpage.mvflow.FeedPageMutation.Loading
import com.savvasdalkitsis.uhuruphotos.feedpage.mvflow.FeedPageMutation.ShowAccountOverview
import com.savvasdalkitsis.uhuruphotos.feedpage.mvflow.FeedPageMutation.ShowAlbums
import com.savvasdalkitsis.uhuruphotos.feedpage.mvflow.FeedPageMutation.ShowDeletionConfirmationDialog
import com.savvasdalkitsis.uhuruphotos.feedpage.mvflow.FeedPageMutation.ShowLogOutConfirmation
import com.savvasdalkitsis.uhuruphotos.feedpage.mvflow.FeedPageMutation.StartRefreshing
import com.savvasdalkitsis.uhuruphotos.feedpage.mvflow.FeedPageMutation.StopRefreshing
import com.savvasdalkitsis.uhuruphotos.feedpage.mvflow.FeedPageMutation.UserBadgeUpdate
import com.savvasdalkitsis.uhuruphotos.feedpage.view.state.FeedPageState
import com.savvasdalkitsis.uhuruphotos.viewmodel.Reducer

fun feedPageReducer() : Reducer<FeedPageState, FeedPageMutation> = { state, mutation ->
    when (mutation) {
        is Loading -> state.copyFeed { copy(isLoading = true) }
        is ShowAlbums -> state.copyFeed { copy(albums = mutation.albums) }
        FinishedLoading -> state.copyFeed { copy(isLoading = false) }
        is UserBadgeUpdate -> state.copy(userInformationState = mutation.state)
        ShowAccountOverview -> state.copy(showAccountOverview = true)
        HideAccountOverview -> state.copy(showAccountOverview = false)
        StartRefreshing -> state.copy(isRefreshing = true)
        StopRefreshing -> state.copy(isRefreshing = false)
        is ChangeDisplay -> state.copyFeed { copy(feedDisplay = mutation.display) }
        ShowDeletionConfirmationDialog -> state.copy(showPhotoDeletionConfirmationDialog = true)
        HideDeletionConfirmationDialog -> state.copy(showPhotoDeletionConfirmationDialog = false)
        HideLogOutConfirmation -> state.copy(showLogOutConfirmation = false)
        ShowLogOutConfirmation -> state.copy(showLogOutConfirmation = true)
    }
}

private fun FeedPageState.copyFeed(feedStateMutation: FeedState.() -> FeedState) =
    copy(feedState = feedStateMutation(feedState))
