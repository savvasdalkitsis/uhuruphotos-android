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
package com.savvasdalkitsis.uhuruphotos.implementation.library.usecase

import android.content.Context
import com.fredporciuncula.flow.preferences.FlowSharedPreferences
import com.savvasdalkitsis.uhuruphotos.api.albums.repository.AlbumsRepository
import com.savvasdalkitsis.uhuruphotos.implementation.library.view.state.AlbumSorting
import com.savvasdalkitsis.uhuruphotos.implementation.library.view.state.AlbumSorting.ALPHABETICAL_ASC
import com.savvasdalkitsis.uhuruphotos.implementation.library.view.state.AlbumSorting.ALPHABETICAL_DESC
import com.savvasdalkitsis.uhuruphotos.implementation.library.view.state.AlbumSorting.DATE_ASC
import com.savvasdalkitsis.uhuruphotos.implementation.library.view.state.AlbumSorting.DATE_DESC
import com.savvasdalkitsis.uhuruphotos.implementation.library.view.state.LibraryAutoAlbum
import com.savvasdalkitsis.uhuruphotos.api.photos.model.Photo
import com.savvasdalkitsis.uhuruphotos.api.photos.usecase.PhotosUseCase
import com.savvasdalkitsis.uhuruphotos.api.strings.R
import com.savvasdalkitsis.uhuruphotos.implementation.library.view.state.LibraryUserAlbum
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class LibraryUseCase @Inject constructor(
    private val albumsRepository: AlbumsRepository,
    private val photosUseCase: PhotosUseCase,
    flowSharedPreferences: FlowSharedPreferences,
    @ApplicationContext private val context: Context,
) {

    private val autoAlbumsSorting =
        flowSharedPreferences.getEnum("autoAlbumsSorting", AlbumSorting.default)
    private val userAlbumsSorting =
        flowSharedPreferences.getEnum("userAlbumsSorting", AlbumSorting.default)

    fun observeAutoAlbumsSorting(): Flow<AlbumSorting> = autoAlbumsSorting.asFlow()
    fun observeUserAlbumsSorting(): Flow<AlbumSorting> = userAlbumsSorting.asFlow()

    suspend fun changeAutoAlbumsSorting(sorting: AlbumSorting) {
        autoAlbumsSorting.setAndCommit(sorting)
    }

    suspend fun changeUserAlbumsSorting(sorting: AlbumSorting) {
        userAlbumsSorting.setAndCommit(sorting)
    }

    fun observeAutoAlbums(): Flow<List<LibraryAutoAlbum>> =
        combine(
            albumsRepository.observeAutoAlbums(),
            observeAutoAlbumsSorting(),
        ) { albums, sorting ->
            albums
                .sorted(sorting,
                    timeStamp = { it.timestamp },
                    title = { it.title },
                )
                .map {
                    with(photosUseCase) {
                        LibraryAutoAlbum(
                            id = it.id,
                            cover = Photo(
                                id = it.coverPhotoHash,
                                thumbnailUrl = it.coverPhotoHash.toThumbnailUrlFromId(),
                                ratio = 1f,
                                isVideo = it.coverPhotoIsVideo ?: false,
                            ),
                            title = it.title ?: context.getString(R.string.missing_album_title),
                            photoCount = it.photoCount,
                        )
                    }
                }
        }

    fun observeUserAlbums(): Flow<List<LibraryUserAlbum>> =
        combine(
            albumsRepository.observeUserAlbums(),
            observeUserAlbumsSorting(),
        ) { albums, sorting ->
            albums
                .sorted(sorting,
                    timeStamp = { it.timestamp },
                    title = { it.title },
                )
                .map {
                    LibraryUserAlbum(
                        id = it.id,
                        mainCover = photo(
                            it.coverPhoto1Hash,
                            it.coverPhoto1IsVideo
                        ),
                        cover2 = photo(
                            it.coverPhoto2Hash,
                            it.coverPhoto2IsVideo
                        ),
                        cover3 = photo(
                            it.coverPhoto3Hash,
                            it.coverPhoto3IsVideo
                        ),
                        cover4 = photo(
                            it.coverPhoto4Hash,
                            it.coverPhoto4IsVideo
                        ),
                        title = it.title ?: context.getString(R.string.missing_album_title),
                        photoCount = it.photoCount,
                    )
                }
        }

    suspend fun refreshAutoAlbums() {
        albumsRepository.refreshAutoAlbums()
    }

    suspend fun refreshUserAlbums() {
        albumsRepository.refreshUserAlbums()
    }

    private fun photo(imageHash: String?, coverFavourite: Boolean?): Photo? = with(photosUseCase) {
        imageHash?.let { imageHash ->
            Photo(
                id = imageHash,
                thumbnailUrl = imageHash.toThumbnailUrlFromId(),
                ratio = 1f,
                isVideo = coverFavourite ?: false,
            )
        }
    }

    private fun <T> List<T>.sorted(
        sorting: AlbumSorting,
        timeStamp: (T) -> String?,
        title: (T) -> String?,
    ): List<T> = sortedBy {
        when (sorting) {
            DATE_DESC, DATE_ASC -> timeStamp(it)
            ALPHABETICAL_ASC, ALPHABETICAL_DESC -> title(it)
        }
    }.let {
        when (sorting) {
            DATE_ASC, ALPHABETICAL_ASC -> it
            DATE_DESC, ALPHABETICAL_DESC -> it.reversed()
        }
    }

}
