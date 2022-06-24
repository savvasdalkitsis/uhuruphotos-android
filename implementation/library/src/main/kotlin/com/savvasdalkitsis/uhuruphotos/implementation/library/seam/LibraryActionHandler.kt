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
import com.savvasdalkitsis.uhuruphotos.implementation.library.seam.LibraryAction.AutoAlbumSelected
import com.savvasdalkitsis.uhuruphotos.implementation.library.seam.LibraryAction.ChangeAutoAlbumsSorting
import com.savvasdalkitsis.uhuruphotos.implementation.library.seam.LibraryAction.ChangeUserAlbumsSorting
import com.savvasdalkitsis.uhuruphotos.implementation.library.seam.LibraryAction.Load
import com.savvasdalkitsis.uhuruphotos.implementation.library.seam.LibraryAction.RefreshAutoAlbums
import com.savvasdalkitsis.uhuruphotos.implementation.library.seam.LibraryAction.RefreshUserAlbums
import com.savvasdalkitsis.uhuruphotos.implementation.library.seam.LibraryAction.UserAlbumSelected
import com.savvasdalkitsis.uhuruphotos.implementation.library.seam.LibraryEffect.ErrorLoadingAlbums
import com.savvasdalkitsis.uhuruphotos.implementation.library.seam.LibraryEffect.NavigateToAutoAlbum
import com.savvasdalkitsis.uhuruphotos.implementation.library.seam.LibraryEffect.NavigateToUserAlbum
import com.savvasdalkitsis.uhuruphotos.implementation.library.seam.LibraryMutation.DisplayAutoAlbums
import com.savvasdalkitsis.uhuruphotos.implementation.library.seam.LibraryMutation.AutoAlbumsLoading
import com.savvasdalkitsis.uhuruphotos.implementation.library.seam.LibraryMutation.DisplayUserAlbums
import com.savvasdalkitsis.uhuruphotos.implementation.library.seam.LibraryMutation.ShowAutoAlbumSorting
import com.savvasdalkitsis.uhuruphotos.implementation.library.seam.LibraryMutation.ShowUserAlbumSorting
import com.savvasdalkitsis.uhuruphotos.implementation.library.seam.LibraryMutation.UserAlbumsLoading
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

    private val autoAlbumsLoading = MutableSharedFlow<Boolean>()
    private val userAlbumsLoading = MutableSharedFlow<Boolean>()

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
            autoAlbumsLoading
                .map(::AutoAlbumsLoading),
            libraryUseCase.observeUserAlbums()
                .map(::DisplayUserAlbums),
            libraryUseCase.observeUserAlbumsSorting()
                .map(::ShowUserAlbumSorting),
            userAlbumsLoading
                .map(::UserAlbumsLoading),
        )
            .safelyOnStartIgnoring {
                if (libraryUseCase.getAutoAlbums().isEmpty()) {
                    refreshAutoAlbums(effect)
                }
                if (libraryUseCase.getUserAlbums().isEmpty()) {
                    refreshUserAlbums(effect)
                }
            }
        is ChangeAutoAlbumsSorting -> flow {
            libraryUseCase.changeAutoAlbumsSorting(action.sorting)
        }
        is ChangeUserAlbumsSorting -> flow {
            libraryUseCase.changeUserAlbumsSorting(action.sorting)
        }
        RefreshAutoAlbums -> flow {
            refreshAutoAlbums(effect)
        }
        RefreshUserAlbums -> flow {
            refreshUserAlbums(effect)
        }
        is AutoAlbumSelected -> flow {
            effect(NavigateToAutoAlbum(action.album))
        }
        is UserAlbumSelected -> flow {
            effect(NavigateToUserAlbum(action.album))
        }
    }

    private suspend fun refreshAutoAlbums(effect: suspend (LibraryEffect) -> Unit) {
        autoAlbumsLoading.emit(true)
        try {
            libraryUseCase.refreshAutoAlbums()
        } catch (e: IOException) {
            log(e)
            effect(ErrorLoadingAlbums)
        } finally {
            // delaying to give ui time to receive the new albums before
            // dismissing the loading bar since no albums logic relies on that
            delay(500)
            autoAlbumsLoading.emit(false)
        }
    }

    private suspend fun refreshUserAlbums(effect: suspend (LibraryEffect) -> Unit) {
        userAlbumsLoading.emit(true)
        try {
            libraryUseCase.refreshUserAlbums()
        } catch (e: IOException) {
            log(e)
            effect(ErrorLoadingAlbums)
        } finally {
            // delaying to give ui time to receive the new albums before
            // dismissing the loading bar since no albums logic relies on that
            delay(500)
            userAlbumsLoading.emit(false)
        }
    }
}
