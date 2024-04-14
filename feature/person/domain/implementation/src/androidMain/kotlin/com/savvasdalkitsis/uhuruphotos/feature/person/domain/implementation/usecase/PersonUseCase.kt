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
package com.savvasdalkitsis.uhuruphotos.feature.person.domain.implementation.usecase

import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.extensions.isVideo
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.person.GetPersonAlbums
import com.savvasdalkitsis.uhuruphotos.feature.feed.domain.api.usecase.FeedUseCase
import com.savvasdalkitsis.uhuruphotos.feature.feed.domain.api.worker.FeedWorkScheduler
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaCollection
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaCollectionSource
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.usecase.MediaUseCase
import com.savvasdalkitsis.uhuruphotos.feature.person.domain.api.usecase.PersonUseCase
import com.savvasdalkitsis.uhuruphotos.feature.person.domain.implementation.repository.PersonRepository
import com.savvasdalkitsis.uhuruphotos.foundation.coroutines.api.safelyOnStartIgnoring
import com.savvasdalkitsis.uhuruphotos.foundation.group.api.model.mapValues
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import se.ansman.dagger.auto.AutoBind
import javax.inject.Inject

@AutoBind
@ActivityRetainedScoped
class PersonUseCase @Inject constructor(
    private val personRepository: PersonRepository,
    private val mediaUseCase: MediaUseCase,
    private val feedUseCase: FeedUseCase,
    private val feedWorkScheduler: FeedWorkScheduler,
) : PersonUseCase {

    override fun observePersonMedia(id: Int): Flow<List<MediaCollection>> =
        personRepository.observePersonAlbums(id)
            .distinctUntilChanged()
            .map {
                with(mediaUseCase) {
                    toMediaCollection(it.mapValues { getPersonAlbums ->
                        getPersonAlbums.toMediaCollectionSource()
                    })
                }
            }
            .initialize()

    private fun Flow<List<MediaCollection>>.initialize(): Flow<List<MediaCollection>> = distinctUntilChanged()
        .safelyOnStartIgnoring {
            if (!feedUseCase.hasFeed()) {
                feedWorkScheduler.scheduleFeedRefreshNow(shallow = false)
            }
        }

    override suspend fun getPersonMedia(id: Int): List<MediaCollection> = with(mediaUseCase) {
        toMediaCollection(personRepository.getPersonAlbums(id)
            .mapValues { it.toMediaCollectionSource() })
    }

    private fun GetPersonAlbums.toMediaCollectionSource() = MediaCollectionSource(
        id = id,
        date = albumDate,
        location = albumLocation,
        mediaItemId = photoId,
        dominantColor = dominantColor,
        rating = rating,
        aspectRatio = aspectRatio,
        isVideo = isVideo,
        lat = null,
        lon = null,
    )
}
