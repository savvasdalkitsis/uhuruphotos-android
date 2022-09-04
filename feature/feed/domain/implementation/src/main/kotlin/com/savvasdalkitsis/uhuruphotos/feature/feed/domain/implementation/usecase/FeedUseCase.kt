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
package com.savvasdalkitsis.uhuruphotos.feature.feed.domain.implementation.usecase

import com.fredporciuncula.flow.preferences.FlowSharedPreferences
import com.savvasdalkitsis.uhuruphotos.api.albums.repository.AlbumsRepository
import com.savvasdalkitsis.uhuruphotos.feature.collage.view.api.ui.state.PredefinedCollageDisplay
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.albums.GetAlbums
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.extensions.isVideo
import com.savvasdalkitsis.uhuruphotos.feature.feed.domain.api.usecase.FeedUseCase
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaCollection
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaCollectionSource
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.usecase.MediaUseCase
import com.savvasdalkitsis.uhuruphotos.foundation.coroutines.api.safelyOnStartIgnoring
import com.savvasdalkitsis.uhuruphotos.foundation.group.api.model.Group
import com.savvasdalkitsis.uhuruphotos.foundation.group.api.model.mapValues
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class FeedUseCase @Inject constructor(
    private val albumsRepository: AlbumsRepository,
    private val mediaUseCase: MediaUseCase,
    preferences: FlowSharedPreferences,
) : FeedUseCase {

    private val preference = preferences.getEnum("feedDisplay", defaultValue = PredefinedCollageDisplay.default)

    override fun observeFeed(): Flow<List<MediaCollection>> = albumsRepository.observeAlbumsByDate()
        .map {
            it.mapValues { albums ->
                albums.toMediaCollectionSource()
            }
        }.map {
            it.toCollection()
        }.initialize()

    override suspend fun getFeed(): List<MediaCollection> = albumsRepository.getAlbumsByDate()
        .mapValues { it.toMediaCollectionSource() }
        .toCollection()

    override suspend fun refreshCluster(clusterId: String) {
        albumsRepository.refreshAlbum(clusterId)
    }

    private fun Flow<List<MediaCollection>>.initialize() = distinctUntilChanged()
        .safelyOnStartIgnoring {
            if (!albumsRepository.hasAlbums()) {
                mediaUseCase.refreshMediaSummaries(shallow = false)
            }
        }

    override fun getFeedDisplay(): Flow<PredefinedCollageDisplay> = preference.asFlow()

    override suspend fun setFeedDisplay(feedDisplay: PredefinedCollageDisplay) {
        preference.setAndCommit(feedDisplay)
    }

    private fun GetAlbums.toMediaCollectionSource() = MediaCollectionSource(
        id = id,
        date = albumDate,
        location = albumLocation,
        mediaItemId = photoId,
        dominantColor = dominantColor,
        rating = rating,
        aspectRatio = aspectRatio,
        isVideo = isVideo,
    )

    private suspend fun Group<String, MediaCollectionSource>.toCollection() =
        with(mediaUseCase) {
            toMediaCollection()
        }
}
