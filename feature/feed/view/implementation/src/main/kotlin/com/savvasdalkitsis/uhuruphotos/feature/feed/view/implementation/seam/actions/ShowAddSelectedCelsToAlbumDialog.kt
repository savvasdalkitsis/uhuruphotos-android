/*
Copyright 2023 Savvas Dalkitsis

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
package com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.actions

import com.savvasdalkitsis.uhuruphotos.feature.catalogue.user.view.api.state.toUserAlbumState
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.album.user.UserAlbums
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.FeedActionsContext
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.FeedMutation
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.FeedMutation.ShowAddToAlbumDialog
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.FeedMutation.StartRefreshing
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.ui.state.FeedState
import com.savvasdalkitsis.uhuruphotos.foundation.coroutines.api.safelyOnStartIgnoring
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

data object ShowAddSelectedCelsToAlbumDialog : FeedAction() {
    override fun FeedActionsContext.handle(
        state: FeedState
    ) = userAlbumsUseCase.observeUserAlbums()
        .map<List<UserAlbums>, FeedMutation> { albums ->
            ShowAddToAlbumDialog(albums.map { it.toUserAlbumState()})
        }
        .onStart {
            emit(StartRefreshing)
        }
        .safelyOnStartIgnoring {
            userAlbumsUseCase.refreshUserAlbums()
        }
}
