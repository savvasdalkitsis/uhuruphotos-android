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
import com.savvasdalkitsis.uhuruphotos.feature.auth.domain.api.usecase.ServerUseCase
import com.savvasdalkitsis.uhuruphotos.feature.catalogue.auto.domain.api.usecase.AutoAlbumsUseCase
import com.savvasdalkitsis.uhuruphotos.feature.catalogue.auto.domain.implementation.repository.AutoAlbumsRepository
import com.savvasdalkitsis.uhuruphotos.feature.catalogue.auto.view.api.state.AutoAlbum
import com.savvasdalkitsis.uhuruphotos.feature.catalogue.view.api.ui.state.CatalogueSorting
import com.savvasdalkitsis.uhuruphotos.feature.catalogue.view.api.ui.state.CatalogueSorting.Companion.sorted
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.album.auto.AutoAlbums
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaId.Remote
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItemHash
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItemInstance
import com.savvasdalkitsis.uhuruphotos.foundation.preferences.api.Preferences
import com.savvasdalkitsis.uhuruphotos.foundation.preferences.api.get
import com.savvasdalkitsis.uhuruphotos.foundation.preferences.api.observe
import com.savvasdalkitsis.uhuruphotos.foundation.preferences.api.set
import com.savvasdalkitsis.uhuruphotos.foundation.strings.api.R.string
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import se.ansman.dagger.auto.AutoBind
import javax.inject.Inject

@AutoBind
class AutoAlbumsUseCase @Inject constructor(
    private val autoAlbumsRepository: AutoAlbumsRepository,
    private val serverUseCase: ServerUseCase,
    private val preferences: Preferences,
    @ApplicationContext private val context: Context,
) : AutoAlbumsUseCase {

    private val key = "autoAlbumsSorting"

    override fun observeAutoAlbumsSorting(): Flow<CatalogueSorting> =
        preferences.observe(key, CatalogueSorting.default)

    override suspend fun changeAutoAlbumsSorting(sorting: CatalogueSorting) {
        preferences.set(key, sorting)
    }

    override fun observeAutoAlbums(): Flow<List<AutoAlbum>> =
        combine(
            autoAlbumsRepository.observeAutoAlbums(),
            observeAutoAlbumsSorting(),
        ) { albums, sorting ->
            albums.toAutoAlbums(sorting)
        }

    override suspend fun refreshAutoAlbums() =
        autoAlbumsRepository.refreshAutoAlbums()

    override suspend fun getAutoAlbums(): List<AutoAlbum> =
        autoAlbumsRepository.getAutoAlbums()
            .toAutoAlbums(preferences.get(key, CatalogueSorting.default))

    private fun List<AutoAlbums>.toAutoAlbums(sorting: CatalogueSorting): List<AutoAlbum> {
        val serverUrl = serverUseCase.getServerUrl()!!
        return sorted(
            sorting,
            timeStamp = { it.timestamp },
            title = { it.title },
        )
            .map {
                AutoAlbum(
                    id = it.id,
                    cover = MediaItemInstance(
                        id = Remote(it.coverPhotoHash, it.coverPhotoIsVideo ?: false, serverUrl),
                        mediaHash = MediaItemHash(it.coverPhotoHash),
                        displayDayDate = null,
                        sortableDate = it.timestamp,
                        ratio = 1f,
                    ),
                    title = it.title ?: context.getString(string.missing_album_title),
                    photoCount = it.photoCount,
                )
            }
    }

}