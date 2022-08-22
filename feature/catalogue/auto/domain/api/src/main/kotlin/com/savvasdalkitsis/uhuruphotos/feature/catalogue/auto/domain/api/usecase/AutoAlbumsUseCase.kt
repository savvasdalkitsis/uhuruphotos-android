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
package com.savvasdalkitsis.uhuruphotos.feature.catalogue.auto.domain.api.usecase

import com.savvasdalkitsis.uhuruphotos.feature.catalogue.auto.view.api.state.AutoAlbum
import com.savvasdalkitsis.uhuruphotos.feature.catalogue.view.api.ui.state.CatalogueSorting
import kotlinx.coroutines.flow.Flow

interface AutoAlbumsUseCase {
    fun observeAutoAlbumsSorting(): Flow<CatalogueSorting>
    suspend fun changeAutoAlbumsSorting(sorting: CatalogueSorting)
    fun observeAutoAlbums(): Flow<List<AutoAlbum>>
    suspend fun refreshAutoAlbums(): Result<Unit>
    suspend fun getAutoAlbums(): List<AutoAlbum>
}