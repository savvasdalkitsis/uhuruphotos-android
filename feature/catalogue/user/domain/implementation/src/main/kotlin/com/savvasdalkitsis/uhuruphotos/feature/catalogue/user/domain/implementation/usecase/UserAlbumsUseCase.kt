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
package com.savvasdalkitsis.uhuruphotos.feature.catalogue.user.domain.implementation.usecase

import com.fredporciuncula.flow.preferences.FlowSharedPreferences
import com.savvasdalkitsis.uhuruphotos.api.albums.repository.AlbumsRepository
import com.savvasdalkitsis.uhuruphotos.api.db.albums.UserAlbums
import com.savvasdalkitsis.uhuruphotos.feature.catalogue.user.domain.api.usecase.UserAlbumsUseCase
import com.savvasdalkitsis.uhuruphotos.feature.catalogue.view.api.ui.state.CatalogueSorting
import com.savvasdalkitsis.uhuruphotos.feature.catalogue.view.api.ui.state.CatalogueSorting.Companion.sorted
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class UserAlbumsUseCase @Inject constructor(
    private val albumsRepository: AlbumsRepository,
    flowSharedPreferences: FlowSharedPreferences,
) : UserAlbumsUseCase {

    private val userAlbumsSorting =
        flowSharedPreferences.getEnum("userAlbumsSorting", CatalogueSorting.default)

    override fun observeUserAlbumsSorting(): Flow<CatalogueSorting> = userAlbumsSorting.asFlow()

    override suspend fun changeUserAlbumsSorting(sorting: CatalogueSorting) {
        userAlbumsSorting.setAndCommit(sorting)
    }

    override fun observeUserAlbums(): Flow<List<UserAlbums>> =
        combine(
            albumsRepository.observeUserAlbums(),
            observeUserAlbumsSorting(),
        ) { albums, sorting ->
            albums.sorted(sorting)
        }

    override suspend fun refreshUserAlbums() =
        albumsRepository.refreshUserAlbums()

    override suspend fun getUserAlbums(): List<UserAlbums> =
        albumsRepository.getUserAlbums().sorted(userAlbumsSorting.get())

    private fun List<UserAlbums>.sorted(sorting: CatalogueSorting): List<UserAlbums> =
        sorted(
            sorting,
            timeStamp = { it.timestamp },
            title = { it.title },
        )
}