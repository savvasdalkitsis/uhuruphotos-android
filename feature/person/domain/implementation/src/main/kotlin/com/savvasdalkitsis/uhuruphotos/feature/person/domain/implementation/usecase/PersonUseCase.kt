package com.savvasdalkitsis.uhuruphotos.feature.person.domain.implementation.usecase

import com.savvasdalkitsis.uhuruphotos.api.albums.repository.AlbumsRepository
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.extensions.isVideo
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.person.GetPersonAlbums
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaCollection
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaCollectionSource
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.usecase.MediaUseCase
import com.savvasdalkitsis.uhuruphotos.feature.person.domain.api.usecase.PersonUseCase
import com.savvasdalkitsis.uhuruphotos.feature.person.domain.implementation.repository.PersonRepository
import com.savvasdalkitsis.uhuruphotos.foundation.coroutines.api.safelyOnStartIgnoring
import com.savvasdalkitsis.uhuruphotos.foundation.group.api.model.mapValues
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PersonUseCase @Inject constructor(
    private val albumsRepository: AlbumsRepository,
    private val personRepository: PersonRepository,
    private val mediaUseCase: MediaUseCase,
) : PersonUseCase {

    override fun observePersonMedia(id: Int): Flow<List<MediaCollection>> =
        personRepository.observePersonAlbums(id)
            .map {
                with(mediaUseCase) {
                    it.mapValues { getPersonAlbums ->
                        getPersonAlbums.toMediaCollectionSource()
                    }.toMediaCollection()
                }
            }
            .initialize()

    private fun Flow<List<MediaCollection>>.initialize(): Flow<List<MediaCollection>> = distinctUntilChanged()
        .safelyOnStartIgnoring {
            if (!albumsRepository.hasAlbums()) {
                mediaUseCase.refreshMediaSummaries(shallow = false)
            }
        }

    override suspend fun getPersonMedia(id: Int): List<MediaCollection> = with(mediaUseCase) {
        personRepository.getPersonAlbums(id)
            .mapValues { it.toMediaCollectionSource() }
            .toMediaCollection()
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
    )
}
