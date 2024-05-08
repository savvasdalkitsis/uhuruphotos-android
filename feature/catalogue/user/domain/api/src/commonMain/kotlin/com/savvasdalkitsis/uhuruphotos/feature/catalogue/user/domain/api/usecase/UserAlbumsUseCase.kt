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
package com.savvasdalkitsis.uhuruphotos.feature.catalogue.user.domain.api.usecase

import com.savvasdalkitsis.uhuruphotos.feature.catalogue.view.api.ui.state.CatalogueSorting
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.album.user.UserAlbums
import com.savvasdalkitsis.uhuruphotos.foundation.result.api.SimpleResult
import kotlinx.coroutines.flow.Flow

interface UserAlbumsUseCase {
    fun observeUserAlbumsSorting(): Flow<CatalogueSorting>
    suspend fun changeUserAlbumsSorting(sorting: CatalogueSorting)
    fun observeUserAlbums(): Flow<List<UserAlbums>>
    suspend fun refreshUserAlbums(): SimpleResult
    suspend fun getUserAlbums(): List<UserAlbums>
}