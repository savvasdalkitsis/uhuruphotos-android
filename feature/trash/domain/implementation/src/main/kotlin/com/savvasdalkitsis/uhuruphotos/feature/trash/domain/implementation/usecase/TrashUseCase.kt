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
package com.savvasdalkitsis.uhuruphotos.feature.trash.domain.implementation.usecase

import com.fredporciuncula.flow.preferences.FlowSharedPreferences
import com.savvasdalkitsis.uhuruphotos.api.albums.repository.AlbumsRepository
import com.savvasdalkitsis.uhuruphotos.api.db.albums.GetTrash
import com.savvasdalkitsis.uhuruphotos.api.db.extensions.isVideo
import com.savvasdalkitsis.uhuruphotos.feature.collage.view.api.ui.state.PredefinedCollageDisplay
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaCollection
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaCollectionSource
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.usecase.MediaUseCase
import com.savvasdalkitsis.uhuruphotos.feature.trash.domain.api.usecase.TrashUseCase
import com.savvasdalkitsis.uhuruphotos.foundation.group.api.model.mapValues
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class TrashUseCase @Inject constructor(
    private val albumsRepository: AlbumsRepository,
    private val mediaUseCase: MediaUseCase,
    flowSharedPreferences: FlowSharedPreferences,
) : TrashUseCase {

    private val trashGalleryDisplay =
        flowSharedPreferences.getEnum("trashGalleryDisplay", PredefinedCollageDisplay.default)

    override suspend fun refreshTrash(): Result<Unit> =
        albumsRepository.refreshTrash()

    override fun getTrashGalleryDisplay() : PredefinedCollageDisplay = trashGalleryDisplay.get()

    override suspend fun setTrashGalleryDisplay(galleryDisplay: PredefinedCollageDisplay) {
        trashGalleryDisplay.setAndCommit(galleryDisplay)
    }

    override suspend fun hasTrash(): Boolean = albumsRepository.hasTrash()

    override fun observeTrashAlbums(): Flow<List<MediaCollection>> = albumsRepository.observeTrash()
        .map {
            it.mapValues {
                    getTrash -> getTrash.toMediaCollectionSource()
            }
        }
        .map {
            with(mediaUseCase) {
                it.toMediaCollection()
            }
        }

    override suspend fun getTrash() = with(mediaUseCase) {
        albumsRepository.getTrash()
            .mapValues { it.toMediaCollectionSource() }
            .toMediaCollection()
    }

    private fun GetTrash.toMediaCollectionSource() = MediaCollectionSource(
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
