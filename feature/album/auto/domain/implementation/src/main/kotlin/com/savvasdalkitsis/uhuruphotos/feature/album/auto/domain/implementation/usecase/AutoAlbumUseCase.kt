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
package com.savvasdalkitsis.uhuruphotos.feature.album.auto.domain.implementation.usecase

import com.savvasdalkitsis.uhuruphotos.feature.album.auto.domain.api.usecase.AutoAlbumUseCase
import com.savvasdalkitsis.uhuruphotos.feature.album.auto.domain.implementation.repository.AutoAlbumRepository
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.album.auto.GetAutoAlbum
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.album.auto.GetPeopleForAutoAlbum
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaCollection
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaCollectionSource
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.usecase.MediaUseCase
import com.savvasdalkitsis.uhuruphotos.foundation.group.api.model.mapValues
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import se.ansman.dagger.auto.AutoBind
import javax.inject.Inject

@AutoBind
internal class AutoAlbumUseCase @Inject constructor(
    private val autoAlbumRepository: AutoAlbumRepository,
    private val mediaUseCase: MediaUseCase,
) : AutoAlbumUseCase {

    override fun observeAutoAlbum(albumId: Int): Flow<List<MediaCollection>> = with(mediaUseCase) {
        autoAlbumRepository.observeAutoAlbum(albumId)
            .distinctUntilChanged()
            .map { albums ->
                albums.map {
                    it.toMediaCollectionSource()
                }.toMediaCollections()
            }
    }

    override fun observeAutoAlbumWithPeople(albumId: Int): Flow<Pair<List<GetAutoAlbum>, List<GetPeopleForAutoAlbum>>> =
        combine(
            autoAlbumRepository.observeAutoAlbum(albumId),
            autoAlbumRepository.observeAutoAlbumPeople(albumId),
        ) { album, people ->
            album to people
        }

    override suspend fun getAutoAlbum(albumId: Int): List<MediaCollection> = with(mediaUseCase) {
        autoAlbumRepository.getAutoAlbum(albumId)
            .mapValues { it.toMediaCollectionSource() }
            .toMediaCollection()
    }

    override suspend fun refreshAutoAlbum(albumId: Int) =
        autoAlbumRepository.refreshAutoAlbum(albumId)

    private fun GetAutoAlbum.toMediaCollectionSource() = MediaCollectionSource(
        id = id,
        date = albumTimestamp,
        location = null,
        mediaItemId = photoId,
        dominantColor = null,
        rating = rating,
        aspectRatio = 1f,
        isVideo = video == true,
        lat = null,
        lon = null,
    )
}

