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
package com.savvasdalkitsis.uhuruphotos.library.usecase

import android.content.Context
import com.fredporciuncula.flow.preferences.FlowSharedPreferences
import com.savvasdalkitsis.uhuruphotos.albums.api.repository.AlbumsRepository
import com.savvasdalkitsis.uhuruphotos.library.view.state.AutoAlbumSorting
import com.savvasdalkitsis.uhuruphotos.library.view.state.AutoAlbumSorting.ALPHABETICAL_ASC
import com.savvasdalkitsis.uhuruphotos.library.view.state.AutoAlbumSorting.ALPHABETICAL_DESC
import com.savvasdalkitsis.uhuruphotos.library.view.state.AutoAlbumSorting.DATE_ASC
import com.savvasdalkitsis.uhuruphotos.library.view.state.AutoAlbumSorting.DATE_DESC
import com.savvasdalkitsis.uhuruphotos.library.view.state.LibraryAutoAlbum
import com.savvasdalkitsis.uhuruphotos.photos.api.model.Photo
import com.savvasdalkitsis.uhuruphotos.photos.usecase.PhotosUseCase
import com.savvasdalkitsis.uhuruphotos.strings.R
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
        flowSharedPreferences.getEnum("autoAlbumsSorting", AutoAlbumSorting.default)

    fun observeAutoAlbumsSorting(): Flow<AutoAlbumSorting> = autoAlbumsSorting.asFlow()

    suspend fun changeAutoAlbumsSorting(sorting: AutoAlbumSorting) {
        autoAlbumsSorting.setAndCommit(sorting)
    }

    fun observeAutoAlbums(): Flow<List<LibraryAutoAlbum>> =
        combine(
            albumsRepository.observeAutoAlbums(),
            observeAutoAlbumsSorting(),
        ) { albums, sorting ->
            albums
                .sortedBy {
                    when (sorting) {
                        DATE_DESC, DATE_ASC -> it.timestamp
                        ALPHABETICAL_ASC, ALPHABETICAL_DESC -> it.title
                    }
                }
                .let {
                    when (sorting) {
                        DATE_ASC, ALPHABETICAL_ASC -> it
                        DATE_DESC, ALPHABETICAL_DESC -> it.reversed()
                    }
                }
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

    suspend fun refreshAutoAlbums() {
        albumsRepository.refreshAutoAlbums()
    }

}
