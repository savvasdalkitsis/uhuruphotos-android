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
package com.savvasdalkitsis.uhuruphotos.autoalbum.seam

import com.savvasdalkitsis.uhuruphotos.autoalbum.view.state.AutoAlbum
import com.savvasdalkitsis.uhuruphotos.autoalbum.view.state.AutoAlbumFeedDisplay
import com.savvasdalkitsis.uhuruphotos.autoalbum.view.state.AutoAlbumState
import com.savvasdalkitsis.uhuruphotos.strings.R
import com.savvasdalkitsis.uhuruphotos.api.seam.Mutation

sealed class AutoAlbumMutation(
    mutation: Mutation<AutoAlbumState>,
) : Mutation<AutoAlbumState> by mutation {

    object ErrorLoading : AutoAlbumMutation({
        it.copy(error = R.string.error_loading_album)
    })

    data class ShowAutoAlbum(val album: AutoAlbum) : AutoAlbumMutation({
        it.copy(
            title = album.title,
            people = album.people,
            feedState = it.feedState.copy(
                albums = album.albums,
                feedDisplay = AutoAlbumFeedDisplay,
            )
        )
    })

    data class Loading(val loading: Boolean) : AutoAlbumMutation({
        it.copy(feedState = it.feedState.copy(
            isLoading = loading,
        ))
    })
}
