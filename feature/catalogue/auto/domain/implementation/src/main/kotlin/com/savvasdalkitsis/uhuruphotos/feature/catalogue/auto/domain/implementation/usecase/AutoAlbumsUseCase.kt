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

import com.github.michaelbull.result.mapOr
import com.savvasdalkitsis.uhuruphotos.feature.catalogue.auto.domain.api.usecase.AutoAlbumsUseCase
import com.savvasdalkitsis.uhuruphotos.feature.catalogue.auto.domain.implementation.repository.AutoAlbumsRepository
import com.savvasdalkitsis.uhuruphotos.feature.catalogue.auto.view.api.state.AutoAlbum
import com.savvasdalkitsis.uhuruphotos.feature.catalogue.view.api.ui.state.CatalogueSortingState
import com.savvasdalkitsis.uhuruphotos.feature.catalogue.view.api.ui.state.CatalogueSortingState.Companion.sorted
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.album.auto.AutoAlbums
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaIdModel.RemoteIdModel
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItemHashModel
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItemInstanceModel
import com.savvasdalkitsis.uhuruphotos.feature.user.domain.api.model.RemoteUserModel
import com.savvasdalkitsis.uhuruphotos.feature.user.domain.api.usecase.UserUseCase
import com.savvasdalkitsis.uhuruphotos.foundation.preferences.api.PlainTextPreferences
import com.savvasdalkitsis.uhuruphotos.foundation.preferences.api.Preferences
import com.savvasdalkitsis.uhuruphotos.foundation.preferences.api.get
import com.savvasdalkitsis.uhuruphotos.foundation.preferences.api.observe
import com.savvasdalkitsis.uhuruphotos.foundation.preferences.api.set
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import org.jetbrains.compose.resources.getString
import se.ansman.dagger.auto.AutoBind
import uhuruphotos_android.foundation.strings.api.generated.resources.Res.string
import uhuruphotos_android.foundation.strings.api.generated.resources.missing_album_title
import javax.inject.Inject

@AutoBind
class AutoAlbumsUseCase @Inject constructor(
    private val autoAlbumsRepository: AutoAlbumsRepository,
    @PlainTextPreferences
    private val preferences: Preferences,
    private val userUseCase: UserUseCase,
) : AutoAlbumsUseCase {

    private val key = "autoAlbumsSorting"

    override fun observeAutoAlbumsSorting(): Flow<CatalogueSortingState> =
        preferences.observe(key, CatalogueSortingState.default)

    override suspend fun changeAutoAlbumsSorting(sorting: CatalogueSortingState) {
        preferences.set(key, sorting)
    }

    override fun observeAutoAlbums(): Flow<List<AutoAlbum>> =
        combine(
            autoAlbumsRepository.observeAutoAlbums(),
            observeAutoAlbumsSorting(),
        ) { albums, sorting ->
            userUseCase.getRemoteUserOrRefresh().mapOr(null) { user ->
                albums.toAutoAlbums(sorting, user)
            }
        }.filterNotNull()

    override suspend fun refreshAutoAlbums() =
        autoAlbumsRepository.refreshAutoAlbums()

    override suspend fun getAutoAlbums(): List<AutoAlbum> =
        userUseCase.getRemoteUserOrRefresh().mapOr(emptyList()) { user ->
            autoAlbumsRepository.getAutoAlbums()
                .toAutoAlbums(preferences.get(key, CatalogueSortingState.default), user)
        }

    private suspend fun List<AutoAlbums>.toAutoAlbums(
        sorting: CatalogueSortingState,
        user: RemoteUserModel,
    ): List<AutoAlbum> =
        sorted(
            sorting,
            timeStamp = { it.timestamp },
            title = { it.title },
        ).map {
            AutoAlbum(
                id = it.id,
                cover = MediaItemInstanceModel(
                    id = RemoteIdModel(
                        it.coverPhotoHash,
                        it.coverPhotoIsVideo == true,
                        MediaItemHashModel.fromRemoteMediaHash(
                            it.coverPhotoHash,
                            user.id
                        )
                    ),
                    mediaHash = MediaItemHashModel.fromRemoteMediaHash(
                        it.coverPhotoHash,
                        user.id
                    ),
                    displayDayDate = null,
                    sortableDate = it.timestamp,
                    ratio = 1f,
                ),
                title = it.title ?: getString(string.missing_album_title),
                photoCount = it.photoCount,
            )
        }
}