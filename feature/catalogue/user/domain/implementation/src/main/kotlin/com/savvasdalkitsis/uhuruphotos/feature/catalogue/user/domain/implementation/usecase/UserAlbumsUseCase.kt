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

import com.savvasdalkitsis.uhuruphotos.feature.catalogue.user.domain.api.usecase.UserAlbumsUseCase
import com.savvasdalkitsis.uhuruphotos.feature.catalogue.user.domain.implementation.repository.UserAlbumsRepository
import com.savvasdalkitsis.uhuruphotos.feature.catalogue.view.api.ui.state.CatalogueSorting
import com.savvasdalkitsis.uhuruphotos.feature.catalogue.view.api.ui.state.CatalogueSorting.Companion.sorted
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.album.user.UserAlbums
import com.savvasdalkitsis.uhuruphotos.foundation.preferences.api.Preferences
import com.savvasdalkitsis.uhuruphotos.foundation.preferences.api.get
import com.savvasdalkitsis.uhuruphotos.foundation.preferences.api.observe
import com.savvasdalkitsis.uhuruphotos.foundation.preferences.api.set
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import se.ansman.dagger.auto.AutoBind
import javax.inject.Inject

@AutoBind
class UserAlbumsUseCase @Inject constructor(
    private val userAlbumsRepository: UserAlbumsRepository,
    private val preferences: Preferences,
) : UserAlbumsUseCase {

    private val key = "userAlbumsSorting"

    override fun observeUserAlbumsSorting(): Flow<CatalogueSorting> =
        preferences.observe(key, CatalogueSorting.default)

    override suspend fun changeUserAlbumsSorting(sorting: CatalogueSorting) {
        preferences.set(key, sorting)
    }

    override fun observeUserAlbums(): Flow<List<UserAlbums>> =
        combine(
            userAlbumsRepository.observeUserAlbums(),
            observeUserAlbumsSorting(),
        ) { albums, sorting ->
            albums.sorted(sorting)
        }

    override suspend fun refreshUserAlbums() =
        userAlbumsRepository.refreshUserAlbums()

    override suspend fun getUserAlbums(): List<UserAlbums> =
        userAlbumsRepository.getUserAlbums().sorted(preferences.get(key, CatalogueSorting.default))

    private fun List<UserAlbums>.sorted(sorting: CatalogueSorting): List<UserAlbums> =
        sorted(
            sorting,
            timeStamp = { it.timestamp },
            title = { it.title },
        )
}