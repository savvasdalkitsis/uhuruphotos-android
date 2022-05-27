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
package com.savvasdalkitsis.uhuruphotos.autoalbum.usecase

import com.savvasdalkitsis.uhuruphotos.albums.api.model.Album
import com.savvasdalkitsis.uhuruphotos.albums.repository.AlbumsRepository
import com.savvasdalkitsis.uhuruphotos.autoalbum.view.state.AutoAlbum
import com.savvasdalkitsis.uhuruphotos.infrastructure.date.DateDisplayer
import com.savvasdalkitsis.uhuruphotos.photos.api.model.Photo
import com.savvasdalkitsis.uhuruphotos.photos.usecase.PhotosUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AutoAlbumsUseCase @Inject constructor(
    private val albumsRepository: AlbumsRepository,
    private val photosUseCase: PhotosUseCase,
    private val dateDisplayer: DateDisplayer,
) {

    fun observeAutoAlbum(albumId: Int): Flow<AutoAlbum> =
        albumsRepository.observeAutoAlbum(albumId).map { entries ->
            val albums = entries.groupBy { entry ->
                dateDisplayer.dateString(entry.timestamp)
            }
            AutoAlbum(
                title = entries.firstOrNull()?.title ?: "",
                albums = albums.entries.map { (date, photos) ->
                    Album(
                        id = date,
                        date = date,
                        location = null,
                        photos = photos.map { Photo(
                            id = it.photoId.toString(),
                            thumbnailUrl = with(photosUseCase) {
                                it.photoId.toThumbnailUrlFromId()
                            },
                            isFavourite = it.isFavorite ?: false,
                            isVideo = it.video ?: false,
                        ) }
                    )
                }
            )
        }

    suspend fun refreshAutoAlbum(albumId: Int) {
        albumsRepository.refreshAutoAlbum(albumId)
    }

}
