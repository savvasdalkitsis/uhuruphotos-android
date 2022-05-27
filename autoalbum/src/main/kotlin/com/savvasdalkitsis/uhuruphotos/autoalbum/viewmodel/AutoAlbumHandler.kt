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
package com.savvasdalkitsis.uhuruphotos.autoalbum.viewmodel

import com.savvasdalkitsis.uhuruphotos.autoalbum.mvflow.AutoAlbumAction
import com.savvasdalkitsis.uhuruphotos.autoalbum.mvflow.AutoAlbumAction.*
import com.savvasdalkitsis.uhuruphotos.autoalbum.mvflow.AutoAlbumEffect
import com.savvasdalkitsis.uhuruphotos.autoalbum.mvflow.AutoAlbumEffect.OpenPhotoDetails
import com.savvasdalkitsis.uhuruphotos.autoalbum.mvflow.AutoAlbumMutation
import com.savvasdalkitsis.uhuruphotos.autoalbum.mvflow.AutoAlbumMutation.*
import com.savvasdalkitsis.uhuruphotos.autoalbum.usecase.AutoAlbumsUseCase
import com.savvasdalkitsis.uhuruphotos.autoalbum.view.state.AutoAlbumState
import com.savvasdalkitsis.uhuruphotos.infrastructure.extensions.safelyOnStartIgnoring
import com.savvasdalkitsis.uhuruphotos.log.log
import com.savvasdalkitsis.uhuruphotos.viewmodel.Handler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import java.io.IOException
import javax.inject.Inject

class AutoAlbumHandler @Inject constructor(
    private val autoAlbumsUseCase: AutoAlbumsUseCase,
) : Handler<AutoAlbumState, AutoAlbumEffect, AutoAlbumAction, AutoAlbumMutation> {

    private val loading = MutableSharedFlow<AutoAlbumMutation>()
    private var albumId: Int? = null

    override fun invoke(
        state: AutoAlbumState,
        action: AutoAlbumAction,
        effect: suspend (AutoAlbumEffect) -> Unit,
    ): Flow<AutoAlbumMutation> = when (action) {
        is LoadAlbum -> merge(
            autoAlbumsUseCase.observeAutoAlbum(action.albumId)
                .map(::ShowAutoAlbum),
            loading
        ).safelyOnStartIgnoring {
            albumId = action.albumId
            refreshAlbum()
        }
        SwipeToRefresh -> flow {
            refreshAlbum()
        }
        is SelectedPhoto -> flow {
            effect(
                with(action) {
                    OpenPhotoDetails(photo.id, center, scale, photo.isVideo)
                }
            )
        }
        NavigateBack -> flow {
            effect(AutoAlbumEffect.NavigateBack)
        }
    }

    private suspend fun refreshAlbum() {
        loading.emit(Loading(true))
        try {
            autoAlbumsUseCase.refreshAutoAlbum(albumId!!)
        } catch (e: IOException) {
            log(e)
            loading.emit(ErrorLoading)
        } finally {
            loading.emit(Loading(false))
        }
    }

}
