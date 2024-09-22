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

import com.savvasdalkitsis.uhuruphotos.feature.collage.view.api.ui.state.PredefinedCollageDisplayState
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.extensions.isVideo
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.media.remote.GetTrash
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaCollection
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaCollectionSource
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.usecase.MediaUseCase
import com.savvasdalkitsis.uhuruphotos.feature.trash.domain.api.usecase.TrashUseCase
import com.savvasdalkitsis.uhuruphotos.feature.trash.domain.implementation.repository.TrashRepository
import com.savvasdalkitsis.uhuruphotos.foundation.group.api.model.mapValues
import com.savvasdalkitsis.uhuruphotos.foundation.preferences.api.PlainTextPreferences
import com.savvasdalkitsis.uhuruphotos.foundation.preferences.api.Preferences
import com.savvasdalkitsis.uhuruphotos.foundation.preferences.api.get
import com.savvasdalkitsis.uhuruphotos.foundation.preferences.api.set
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import se.ansman.dagger.auto.AutoBind
import javax.inject.Inject

@AutoBind
internal class TrashUseCase @Inject constructor(
    private val mediaUseCase: MediaUseCase,
    private val trashRepository: TrashRepository,
    @PlainTextPreferences
    private val preferences: Preferences,
) : TrashUseCase {

    private val key = "trashGalleryDisplay"

    override suspend fun refreshTrash() =
        trashRepository.refreshTrash()

    override fun getTrashGalleryDisplay() : PredefinedCollageDisplayState =
        preferences.get(key, PredefinedCollageDisplayState.default)

    override fun setTrashGalleryDisplay(galleryDisplay: PredefinedCollageDisplayState) {
        preferences.set(key, galleryDisplay)
    }

    override suspend fun hasTrash(): Boolean = trashRepository.hasTrash()

    override fun observeTrashAlbums(): Flow<List<MediaCollection>> = trashRepository.observeTrash()
        .distinctUntilChanged()
        .map {
            it.mapValues {
                    getTrash -> getTrash.toMediaCollectionSource()
            }
        }
        .map {
            with(mediaUseCase) {
                toMediaCollection(it)
            }
        }

    override suspend fun getTrash() = with(mediaUseCase) {
        toMediaCollection(trashRepository.getTrash()
            .mapValues { it.toMediaCollectionSource() })
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
        lat = null,
        lon = null,
    )
}
