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
package com.savvasdalkitsis.uhuruphotos.implementation.localalbum.usecase

import com.fredporciuncula.flow.preferences.FlowSharedPreferences
import com.savvasdalkitsis.uhuruphotos.api.albums.model.Album
import com.savvasdalkitsis.uhuruphotos.api.feed.view.state.FeedDisplay
import com.savvasdalkitsis.uhuruphotos.api.feed.view.state.FeedDisplays
import com.savvasdalkitsis.uhuruphotos.api.localalbum.usecase.LocalAlbumUseCase
import com.savvasdalkitsis.uhuruphotos.api.mediastore.model.LocalBucket
import com.savvasdalkitsis.uhuruphotos.api.mediastore.model.MediaBucket
import com.savvasdalkitsis.uhuruphotos.api.mediastore.usecase.MediaStoreUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.mapNotNull
import javax.inject.Inject

internal class LocalAlbumUseCase @Inject constructor(
    private val mediaStoreUseCase: MediaStoreUseCase,
    private val flowSharedPreferences: FlowSharedPreferences,
) : LocalAlbumUseCase {

    override fun observeLocalAlbum(albumId: Int): Flow<Pair<MediaBucket, List<Album>>> =
        mediaStoreUseCase.observeBucket(albumId)
            .mapNotNull { localMedia ->
                when (localMedia) {
                    is LocalBucket.Found -> localMedia.bucket
                    else -> null
                }
            }

    override suspend fun refreshLocalAlbum(albumId: Int) {
        mediaStoreUseCase.refreshBucket(albumId)
    }

    override fun getLocalAlbumFeedDisplay(albumId: Int) : FeedDisplays =
        userAlbumFeedDisplay(albumId).get()

    override suspend fun setLocalAlbumFeedDisplay(albumId: Int, feedDisplay: FeedDisplays) {
        userAlbumFeedDisplay(albumId).setAndCommit(feedDisplay)
    }

    override suspend fun getLocalAlbum(albumId: Int): List<Album> =
        observeLocalAlbum(albumId).first().second

    private fun userAlbumFeedDisplay(albumId: Int) =
        flowSharedPreferences.getEnum("localAlbumFeedDisplay/$albumId", FeedDisplays.default)

}
