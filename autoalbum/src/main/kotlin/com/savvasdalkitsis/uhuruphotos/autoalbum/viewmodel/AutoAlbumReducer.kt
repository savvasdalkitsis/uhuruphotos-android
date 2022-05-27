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
package com.savvasdalkitsis.uhuruphotos.autoalbum.viewmodel

import android.content.Context
import com.savvasdalkitsis.uhuruphotos.autoalbum.mvflow.AutoAlbumMutation
import com.savvasdalkitsis.uhuruphotos.autoalbum.mvflow.AutoAlbumMutation.ErrorLoading
import com.savvasdalkitsis.uhuruphotos.autoalbum.mvflow.AutoAlbumMutation.Loading
import com.savvasdalkitsis.uhuruphotos.autoalbum.mvflow.AutoAlbumMutation.ShowAutoAlbum
import com.savvasdalkitsis.uhuruphotos.autoalbum.view.state.AutoAlbumFeedDisplay
import com.savvasdalkitsis.uhuruphotos.autoalbum.view.state.AutoAlbumState
import com.savvasdalkitsis.uhuruphotos.strings.R
import com.savvasdalkitsis.uhuruphotos.viewmodel.Reducer
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class AutoAlbumReducer @Inject constructor(
    @ApplicationContext private val context: Context,
) : Reducer<AutoAlbumState, AutoAlbumMutation> {

    override suspend fun invoke(
        state: AutoAlbumState,
        mutation: AutoAlbumMutation
    ): AutoAlbumState = when (mutation) {
        ErrorLoading -> state.copy(error = context.getString(R.string.error_loading_album))
        is Loading -> state.copy(feedState = state.feedState.copy(
            isLoading = mutation.loading,
        ))
        is ShowAutoAlbum -> state.copy(
            title = mutation.album.title,
            people = mutation.album.people,
            feedState = state.feedState.copy(
                albums = mutation.album.albums,
                feedDisplay = AutoAlbumFeedDisplay,
            )
        )
    }
}