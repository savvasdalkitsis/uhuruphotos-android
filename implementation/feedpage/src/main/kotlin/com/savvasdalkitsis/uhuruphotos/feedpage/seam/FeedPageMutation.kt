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
package com.savvasdalkitsis.uhuruphotos.feedpage.seam

import com.savvasdalkitsis.uhuruphotos.albums.api.model.Album
import com.savvasdalkitsis.uhuruphotos.api.seam.Mutation
import com.savvasdalkitsis.uhuruphotos.feed.view.state.FeedDisplays
import com.savvasdalkitsis.uhuruphotos.feed.view.state.FeedState
import com.savvasdalkitsis.uhuruphotos.feedpage.view.state.FeedPageState
import com.savvasdalkitsis.uhuruphotos.userbadge.api.view.state.UserInformationState

internal sealed class FeedPageMutation(
    mutation: Mutation<FeedPageState>,
) : Mutation<FeedPageState> by mutation {

    object Loading : FeedPageMutation({
        it.copyFeed { copy(isLoading = true) }
    })

    object ShowAccountOverview : FeedPageMutation({
        it.copy(showAccountOverview = true)
    })

    object HideAccountOverview : FeedPageMutation({
        it.copy(showAccountOverview = false)
    })

    object ShowLogOutConfirmation : FeedPageMutation({
        it.copy(showLogOutConfirmation = true)
    })

    object HideLogOutConfirmation : FeedPageMutation({
        it.copy(showLogOutConfirmation = false)
    })

    object StartRefreshing : FeedPageMutation({
        it.copy(isRefreshing = true)
    })

    object StopRefreshing : FeedPageMutation({
        it.copy(isRefreshing = false)
    })

    object ShowDeletionConfirmationDialog : FeedPageMutation({
        it.copy(showPhotoDeletionConfirmationDialog = true)
    })

    object HideDeletionConfirmationDialog : FeedPageMutation({
        it.copy(showPhotoDeletionConfirmationDialog = false)
    })

    object ShowNoPhotosFound : FeedPageMutation({
        it.copyFeed { copy(isLoading = false, isEmpty = true, albums = emptyList()) }
    })

    data class ShowAlbums(val albums: List<Album>) : FeedPageMutation({
        it.copyFeed { copy(isLoading = false, isEmpty = false, albums = albums) }
    }) {
        override fun toString() = "Showing ${albums.size} albums"
    }

    data class UserBadgeUpdate(val userInformationState: UserInformationState) : FeedPageMutation({
        it.copy(userInformationState = userInformationState)
    })

    data class ChangeDisplay(val display: FeedDisplays) : FeedPageMutation({
        it.copyFeed { copy(feedDisplay = display) }
    })

    data class ShowLibrary(val showLibrary: Boolean) : FeedPageMutation({
        it.copy(showLibrary = showLibrary)
    })
}

private fun FeedPageState.copyFeed(feedStateMutation: FeedState.() -> FeedState) =
    copy(feedState = feedStateMutation(feedState))
