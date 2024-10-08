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

import com.savvasdalkitsis.uhuruphotos.feature.album.user.domain.api.model.UserAlbumModel
import com.savvasdalkitsis.uhuruphotos.feature.album.user.domain.api.usecase.UserAlbumUseCase
import com.savvasdalkitsis.uhuruphotos.feature.album.user.domain.implementation.repository.UserAlbumRepository
import com.savvasdalkitsis.uhuruphotos.feature.album.user.domain.implementation.work.UserAlbumWorkScheduler
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.album.user.GetUserAlbumMedia
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.extensions.isVideo
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaCollectionSourceModel
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaIdModel
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.usecase.MediaUseCase
import com.savvasdalkitsis.uhuruphotos.foundation.result.api.SimpleResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import se.ansman.dagger.auto.AutoBind
import javax.inject.Inject

@AutoBind
class UserAlbumUseCase @Inject constructor(
    private val userAlbumRepository: UserAlbumRepository,
    private val mediaUseCase: MediaUseCase,
    private val userAlbumWorkScheduler: UserAlbumWorkScheduler,
) : UserAlbumUseCase {

    override fun observeUserAlbum(albumId: Int): Flow<UserAlbumModel> = with(mediaUseCase) {
        userAlbumRepository.observeUserAlbum(albumId)
            .distinctUntilChanged()
            .map { entry ->
                UserAlbumModel(
                    title = entry.userAlbumTitle,
                    mediaCollections = entry.map {
                        it.toMediaCollectionSource()
                    }.toMediaCollections()
                )
            }
        }

    override suspend fun getUserAlbum(albumId: Int): UserAlbumModel = with(mediaUseCase) {
        val album = userAlbumRepository.getUserAlbum(albumId)
        UserAlbumModel(
            title = album.userAlbumTitle,
            mediaCollections = album
                .map { it.toMediaCollectionSource() }
                .toMediaCollections()
        )
    }

    override suspend fun refreshUserAlbum(albumId: Int) =
        userAlbumRepository.refreshUserAlbum(albumId)

    override fun addMediaToAlbum(albumId: Int, media: List<MediaIdModel.RemoteIdModel>) {
        userAlbumRepository.queueMediaAdditionToUserAlbum(albumId, media.map { it.value })
        userAlbumWorkScheduler.scheduleMediaAddition(albumId)
    }

    override suspend fun createNewUserAlbum(name: String, media: List<MediaIdModel.RemoteIdModel>) {
        if (media.isNotEmpty()) {
            userAlbumRepository.queueMediaAdditionToNewUserAlbum(name, media.map { it.value })
        }
        userAlbumWorkScheduler.scheduleNewAlbumCreation(name, performAdditionAfterCreation = media.isNotEmpty())
    }

    override suspend fun deleteUserAlbum(albumId: Int): SimpleResult =
        userAlbumRepository.deleteUserAlbum(albumId)

    private fun GetUserAlbumMedia.toMediaCollectionSource() = MediaCollectionSourceModel(
        id = id,
        date = photoDate,
        location = location,
        mediaItemId = photoId,
        dominantColor = dominantColor,
        rating = rating,
        aspectRatio = aspectRatio,
        isVideo = isVideo,
        lat = null,
        lon = null,
    )

    private val List<GetUserAlbumMedia>.userAlbumTitle: String
        get() = firstOrNull()?.title ?: ""
}

