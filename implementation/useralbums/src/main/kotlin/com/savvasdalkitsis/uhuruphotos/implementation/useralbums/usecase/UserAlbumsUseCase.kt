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
package com.savvasdalkitsis.uhuruphotos.implementation.useralbums.usecase

import android.content.Context
import com.fredporciuncula.flow.preferences.FlowSharedPreferences
import com.savvasdalkitsis.uhuruphotos.api.albums.repository.AlbumsRepository
import com.savvasdalkitsis.uhuruphotos.api.albums.view.state.AlbumSorting
import com.savvasdalkitsis.uhuruphotos.api.albums.view.state.AlbumSorting.Companion.sorted
import com.savvasdalkitsis.uhuruphotos.api.db.albums.UserAlbums
import com.savvasdalkitsis.uhuruphotos.api.media.page.domain.model.MediaGrid
import com.savvasdalkitsis.uhuruphotos.api.media.page.domain.model.MediaId
import com.savvasdalkitsis.uhuruphotos.api.media.page.domain.model.MediaItem
import com.savvasdalkitsis.uhuruphotos.api.media.remote.domain.usecase.RemoteMediaUseCase
import com.savvasdalkitsis.uhuruphotos.api.strings.R.string
import com.savvasdalkitsis.uhuruphotos.api.useralbums.usecase.UserAlbumsUseCase
import com.savvasdalkitsis.uhuruphotos.api.useralbums.view.state.UserAlbum
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class UserAlbumsUseCase @Inject constructor(
    private val albumsRepository: AlbumsRepository,
    private val remoteMediaUseCase: RemoteMediaUseCase,
    flowSharedPreferences: FlowSharedPreferences,
    @ApplicationContext private val context: Context,
) : UserAlbumsUseCase {

    private val userAlbumsSorting =
        flowSharedPreferences.getEnum("userAlbumsSorting", AlbumSorting.default)

    override fun observeUserAlbumsSorting(): Flow<AlbumSorting> = userAlbumsSorting.asFlow()

    override suspend fun changeUserAlbumsSorting(sorting: AlbumSorting) {
        userAlbumsSorting.setAndCommit(sorting)
    }

    override fun observeUserAlbums(): Flow<List<UserAlbum>> =
        combine(
            albumsRepository.observeUserAlbums(),
            observeUserAlbumsSorting(),
        ) { albums, sorting ->
            albums.toUserAlbums(sorting)
        }

    override suspend fun refreshUserAlbums() {
        albumsRepository.refreshUserAlbums()
    }

    override suspend fun getUserAlbums(): List<UserAlbum> =
        albumsRepository.getUserAlbums().toUserAlbums(userAlbumsSorting.get())

    private fun List<UserAlbums>.toUserAlbums(sorting: AlbumSorting): List<UserAlbum> =
        sorted(
            sorting,
            timeStamp = { it.timestamp },
            title = { it.title },
        )
            .map {
                UserAlbum(
                    id = it.id,
                    cover = MediaGrid(
                        mediaItem1 = photo(
                            it.coverPhoto1Hash,
                            it.coverPhoto1IsVideo
                        ),
                        mediaItem2 = photo(
                            it.coverPhoto2Hash,
                            it.coverPhoto2IsVideo
                        ),
                        mediaItem3 = photo(
                            it.coverPhoto3Hash,
                            it.coverPhoto3IsVideo
                        ),
                        mediaItem4 = photo(
                            it.coverPhoto4Hash,
                            it.coverPhoto4IsVideo
                        ),
                    ),
                    title = it.title ?: context.getString(string.missing_album_title),
                    photoCount = it.photoCount,
                )
            }

    private fun photo(imageHash: String?, coverIsVideo: Boolean?): MediaItem? = with(remoteMediaUseCase) {
        imageHash?.let { imageHash ->
            MediaItem(
                id = MediaId.Remote(imageHash),
                mediaHash = imageHash,
                thumbnailUri = imageHash.toThumbnailUrlFromId(),
                fullResUri = imageHash.toFullSizeUrlFromId(coverIsVideo ?: false),
                displayDayDate = null,
                ratio = 1f,
                isVideo = coverIsVideo ?: false,
            )
        }
    }

}