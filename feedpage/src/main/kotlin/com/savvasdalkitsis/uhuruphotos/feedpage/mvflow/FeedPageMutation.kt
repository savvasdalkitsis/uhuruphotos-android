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
package com.savvasdalkitsis.uhuruphotos.feedpage.mvflow

import com.savvasdalkitsis.uhuruphotos.albums.api.model.Album
import com.savvasdalkitsis.uhuruphotos.feed.view.state.FeedDisplays
import com.savvasdalkitsis.uhuruphotos.userbadge.api.view.state.UserInformationState

sealed class FeedPageMutation {
    object Loading : FeedPageMutation()
    object FinishedLoading : FeedPageMutation()
    object ShowAccountOverview : FeedPageMutation()
    object HideAccountOverview : FeedPageMutation()
    object ShowLogOutConfirmation : FeedPageMutation()
    object HideLogOutConfirmation : FeedPageMutation()
    object StartRefreshing : FeedPageMutation()
    object StopRefreshing : FeedPageMutation()
    object ShowDeletionConfirmationDialog : FeedPageMutation()
    object HideDeletionConfirmationDialog : FeedPageMutation()

    data class ShowAlbums(val albums: List<Album>) : FeedPageMutation() {
        override fun toString() = "Showing ${albums.size} albums"
    }
    data class UserBadgeUpdate(val state: UserInformationState) : FeedPageMutation()
    data class ChangeDisplay(val display: FeedDisplays) : FeedPageMutation()
}
