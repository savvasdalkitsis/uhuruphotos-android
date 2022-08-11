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
package com.savvasdalkitsis.uhuruphotos.implementation.gallery.album.user.usecase

import com.fredporciuncula.flow.preferences.FlowSharedPreferences
import com.savvasdalkitsis.uhuruphotos.api.albums.repository.AlbumsRepository
import com.savvasdalkitsis.uhuruphotos.api.db.albums.GetUserAlbum
import com.savvasdalkitsis.uhuruphotos.api.feed.view.state.FeedDisplay
import com.savvasdalkitsis.uhuruphotos.api.feed.view.state.FeedDisplays
import com.savvasdalkitsis.uhuruphotos.api.group.model.Group
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class UserAlbumsUseCase @Inject constructor(
    private val albumsRepository: AlbumsRepository,
    private val flowSharedPreferences: FlowSharedPreferences,
) {

    fun observeUserAlbum(albumId: Int): Flow<List<GetUserAlbum>> =
            albumsRepository.observeUserAlbum(albumId)

    suspend fun getUserAlbum(albumId: Int): Group<String, GetUserAlbum> =
            albumsRepository.getUserAlbum(albumId)

    suspend fun refreshUserAlbum(albumId: Int) =
        albumsRepository.refreshUserAlbum(albumId)

    fun getUserAlbumFeedDisplay(albumId: Int) : FeedDisplay =
        userAlbumFeedDisplay(albumId).get()

    suspend fun setUserAlbumFeedDisplay(albumId: Int, feedDisplay: FeedDisplays) {
        userAlbumFeedDisplay(albumId).setAndCommit(feedDisplay)
    }

    private fun userAlbumFeedDisplay(albumId: Int) =
        flowSharedPreferences.getEnum("userAlbumFeedDisplay/$albumId", FeedDisplays.default)

}
