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
package com.savvasdalkitsis.uhuruphotos.api.albumpage.seam

import com.savvasdalkitsis.uhuruphotos.api.albumpage.view.state.AlbumDetails
import com.savvasdalkitsis.uhuruphotos.api.albumpage.view.state.AlbumPageState
import com.savvasdalkitsis.uhuruphotos.api.feed.view.state.FeedDisplay
import com.savvasdalkitsis.uhuruphotos.api.seam.Mutation

sealed class AlbumPageMutation(
    mutation: Mutation<AlbumPageState>,
) : Mutation<AlbumPageState> by mutation {

    data class ShowAlbumPage(val albumPage: AlbumDetails) : AlbumPageMutation({
        it.copy(
            title = albumPage.title,
            people = albumPage.people,
            feedState = it.feedState.copy(
                albums = albumPage.albums,
            )
        )
    })

    data class Loading(val loading: Boolean) : AlbumPageMutation({
        it.copy(feedState = it.feedState.copy(
            isLoading = loading,
            isEmpty = !loading && !it.feedState.hasPhotos
        ))
    })

    data class ChangeFeedDisplay(val feedDisplay: FeedDisplay) : AlbumPageMutation({
        it.copy(feedState = it.feedState.copy(
            feedDisplay = feedDisplay,
        ))
    })
}
