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
package com.savvasdalkitsis.uhuruphotos.implementation.library.seam

import com.savvasdalkitsis.uhuruphotos.api.coroutines.safelyOnStartIgnoring
import com.savvasdalkitsis.uhuruphotos.api.log.log
import com.savvasdalkitsis.uhuruphotos.api.seam.ActionHandler
import com.savvasdalkitsis.uhuruphotos.implementation.library.seam.LibraryAction.AlbumSelected
import com.savvasdalkitsis.uhuruphotos.implementation.library.seam.LibraryAction.ChangeSorting
import com.savvasdalkitsis.uhuruphotos.implementation.library.seam.LibraryAction.Load
import com.savvasdalkitsis.uhuruphotos.implementation.library.seam.LibraryAction.RefreshAutoAlbums
import com.savvasdalkitsis.uhuruphotos.implementation.library.seam.LibraryEffect.ErrorLoadingAutoAlbums
import com.savvasdalkitsis.uhuruphotos.implementation.library.seam.LibraryEffect.NavigateToAutoAlbum
import com.savvasdalkitsis.uhuruphotos.implementation.library.seam.LibraryMutation.DisplayAutoAlbums
import com.savvasdalkitsis.uhuruphotos.implementation.library.seam.LibraryMutation.Loading
import com.savvasdalkitsis.uhuruphotos.implementation.library.seam.LibraryMutation.ShowAutoAlbumSorting
import com.savvasdalkitsis.uhuruphotos.implementation.library.usecase.LibraryUseCase
import com.savvasdalkitsis.uhuruphotos.implementation.library.view.state.LibraryState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import java.io.IOException
import javax.inject.Inject

class LibraryActionHandler @Inject constructor(
    private val libraryUseCase: LibraryUseCase,
) : ActionHandler<LibraryState, LibraryEffect, LibraryAction, LibraryMutation> {

    private val loading = MutableSharedFlow<Boolean>()

    override fun handleAction(
        state: LibraryState,
        action: LibraryAction,
        effect: suspend (LibraryEffect) -> Unit,
    ): Flow<LibraryMutation> = when (action) {
        Load -> merge(
            libraryUseCase.observeAutoAlbums()
                .map(::DisplayAutoAlbums),
            libraryUseCase.observeAutoAlbumsSorting()
                .map(::ShowAutoAlbumSorting),
            loading
                .map(::Loading)
        )
            .safelyOnStartIgnoring {
                refreshAutoAlbums(effect)
            }
        is ChangeSorting -> flow {
            libraryUseCase.changeAutoAlbumsSorting(action.sorting)
        }
        RefreshAutoAlbums -> flow {
            refreshAutoAlbums(effect)
        }
        is AlbumSelected -> flow {
            effect(NavigateToAutoAlbum(action.album))
        }
    }

    private suspend fun refreshAutoAlbums(effect: suspend (LibraryEffect) -> Unit) {
        loading.emit(true)
        try {
            libraryUseCase.refreshAutoAlbums()
        } catch (e: IOException) {
            log(e)
            effect(ErrorLoadingAutoAlbums)
        } finally {
            // delaying to give ui time to receive the new albums before
            // dismissing the loading bar since no albums logic relies on that
            delay(500)
            loading.emit(false)
        }
    }
}
