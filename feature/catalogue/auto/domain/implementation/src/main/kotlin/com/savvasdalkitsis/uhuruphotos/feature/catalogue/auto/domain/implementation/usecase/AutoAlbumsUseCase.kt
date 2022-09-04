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
package com.savvasdalkitsis.uhuruphotos.feature.catalogue.auto.domain.implementation.usecase

import android.content.Context
import com.fredporciuncula.flow.preferences.FlowSharedPreferences
import com.savvasdalkitsis.uhuruphotos.api.albums.repository.AlbumsRepository
import com.savvasdalkitsis.uhuruphotos.feature.catalogue.auto.domain.api.usecase.AutoAlbumsUseCase
import com.savvasdalkitsis.uhuruphotos.feature.catalogue.auto.view.api.state.AutoAlbum
import com.savvasdalkitsis.uhuruphotos.feature.catalogue.view.api.ui.state.CatalogueSorting
import com.savvasdalkitsis.uhuruphotos.feature.catalogue.view.api.ui.state.CatalogueSorting.Companion.sorted
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.albums.AutoAlbums
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaId
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItem
import com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.api.usecase.RemoteMediaUseCase
import com.savvasdalkitsis.uhuruphotos.foundation.strings.api.R.string
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class AutoAlbumsUseCase @Inject constructor(
    private val albumsRepository: AlbumsRepository,
    private val remoteMediaUseCase: RemoteMediaUseCase,
    flowSharedPreferences: FlowSharedPreferences,
    @ApplicationContext private val context: Context,
) : AutoAlbumsUseCase {

    private val autoAlbumsSorting =
        flowSharedPreferences.getEnum("autoAlbumsSorting", CatalogueSorting.default)

    override fun observeAutoAlbumsSorting(): Flow<CatalogueSorting> = autoAlbumsSorting.asFlow()

    override suspend fun changeAutoAlbumsSorting(sorting: CatalogueSorting) {
        autoAlbumsSorting.setAndCommit(sorting)
    }

    override fun observeAutoAlbums(): Flow<List<AutoAlbum>> =
        combine(
            albumsRepository.observeAutoAlbums(),
            observeAutoAlbumsSorting(),
        ) { albums, sorting ->
            albums.toAutoAlbums(sorting)
        }

    override suspend fun refreshAutoAlbums() =
        albumsRepository.refreshAutoAlbums()

    override suspend fun getAutoAlbums(): List<AutoAlbum> =
        albumsRepository.getAutoAlbums().toAutoAlbums(autoAlbumsSorting.get())

    private fun List<AutoAlbums>.toAutoAlbums(sorting: CatalogueSorting): List<AutoAlbum> =
        sorted(
            sorting,
            timeStamp = { it.timestamp },
            title = { it.title },
        )
            .map {
                with(remoteMediaUseCase) {
                    AutoAlbum(
                        id = it.id,
                        cover = MediaItem(
                            id = MediaId.Remote(it.coverPhotoHash),
                            mediaHash = it.coverPhotoHash,
                            thumbnailUri = it.coverPhotoHash.toThumbnailUrlFromId(),
                            fullResUri = it.coverPhotoHash.toFullSizeUrlFromId(
                                it.coverPhotoIsVideo ?: false
                            ),
                            displayDayDate = null,
                            ratio = 1f,
                            isVideo = it.coverPhotoIsVideo ?: false,
                        ),
                        title = it.title ?: context.getString(string.missing_album_title),
                        photoCount = it.photoCount,
                    )
                }
            }

}