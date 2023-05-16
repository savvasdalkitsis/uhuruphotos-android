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
package com.savvasdalkitsis.uhuruphotos.feature.album.user.domain.implementation.usecase

import com.savvasdalkitsis.uhuruphotos.feature.album.user.domain.api.model.UserAlbum
import com.savvasdalkitsis.uhuruphotos.feature.album.user.domain.api.usecase.UserAlbumUseCase
import com.savvasdalkitsis.uhuruphotos.feature.album.user.domain.implementation.repository.UserAlbumRepository
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.album.user.GetUserAlbum
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.extensions.isVideo
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaCollectionSource
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.usecase.MediaUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UserAlbumUseCase @Inject constructor(
    private val userAlbumRepository: UserAlbumRepository,
    private val mediaUseCase: MediaUseCase,
) : UserAlbumUseCase {

    override fun observeUserAlbum(albumId: Int): Flow<UserAlbum> = with(mediaUseCase) {
        userAlbumRepository.observeUserAlbum(albumId)
            .distinctUntilChanged()
            .map { entry ->
                UserAlbum(
                    title = entry.userAlbumTitle,
                    mediaCollections = entry.map {
                        it.toMediaCollectionSource()
                    }.toMediaCollections()
                )
            }
        }

    override suspend fun getUserAlbum(albumId: Int): UserAlbum = with(mediaUseCase) {
        val album = userAlbumRepository.getUserAlbum(albumId)
        UserAlbum(
            title = album.userAlbumTitle,
            mediaCollections = album
                .map { it.toMediaCollectionSource() }
                .toMediaCollections()
        )
    }

    override suspend fun refreshUserAlbum(albumId: Int) =
        userAlbumRepository.refreshUserAlbum(albumId)

    private fun GetUserAlbum.toMediaCollectionSource() = MediaCollectionSource(
        id = id,
        date = photoDate,
        location = location,
        mediaItemId = photoId,
        dominantColor = dominantColor,
        rating = rating,
        aspectRatio = aspectRatio,
        isVideo = isVideo,
    )

    private val List<GetUserAlbum>.userAlbumTitle: String
        get() = firstOrNull()?.title ?: ""
}

