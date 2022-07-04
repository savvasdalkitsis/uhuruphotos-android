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

import com.savvasdalkitsis.uhuruphotos.api.autoalbums.usecase.AutoAlbumsUseCase
import com.savvasdalkitsis.uhuruphotos.api.coroutines.safelyOnStartIgnoring
import com.savvasdalkitsis.uhuruphotos.api.log.log
import com.savvasdalkitsis.uhuruphotos.api.photos.model.Photo
import com.savvasdalkitsis.uhuruphotos.api.photos.model.PhotoGrid
import com.savvasdalkitsis.uhuruphotos.api.photos.usecase.PhotosUseCase
import com.savvasdalkitsis.uhuruphotos.api.seam.ActionHandler
import com.savvasdalkitsis.uhuruphotos.api.useralbums.usecase.UserAlbumsUseCase
import com.savvasdalkitsis.uhuruphotos.implementation.library.seam.LibraryAction.AutoAlbumsSelected
import com.savvasdalkitsis.uhuruphotos.implementation.library.seam.LibraryAction.FavouritePhotosSelected
import com.savvasdalkitsis.uhuruphotos.implementation.library.seam.LibraryAction.HiddenPhotosSelected
import com.savvasdalkitsis.uhuruphotos.implementation.library.seam.LibraryAction.Load
import com.savvasdalkitsis.uhuruphotos.implementation.library.seam.LibraryAction.Refresh
import com.savvasdalkitsis.uhuruphotos.implementation.library.seam.LibraryAction.UserAlbumsSelected
import com.savvasdalkitsis.uhuruphotos.implementation.library.seam.LibraryEffect.ErrorLoadingAlbums
import com.savvasdalkitsis.uhuruphotos.implementation.library.seam.LibraryEffect.NavigateToAutoAlbums
import com.savvasdalkitsis.uhuruphotos.implementation.library.seam.LibraryEffect.NavigateToFavourites
import com.savvasdalkitsis.uhuruphotos.implementation.library.seam.LibraryEffect.NavigateToHidden
import com.savvasdalkitsis.uhuruphotos.implementation.library.seam.LibraryEffect.NavigateToUserAlbums
import com.savvasdalkitsis.uhuruphotos.implementation.library.seam.LibraryMutation.DisplayAutoAlbums
import com.savvasdalkitsis.uhuruphotos.implementation.library.seam.LibraryMutation.DisplayFavouritePhotos
import com.savvasdalkitsis.uhuruphotos.implementation.library.seam.LibraryMutation.DisplayHiddenPhotos
import com.savvasdalkitsis.uhuruphotos.implementation.library.seam.LibraryMutation.DisplayUserAlbums
import com.savvasdalkitsis.uhuruphotos.implementation.library.seam.LibraryMutation.Loading
import com.savvasdalkitsis.uhuruphotos.implementation.library.view.state.LibraryState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import java.io.IOException
import javax.inject.Inject

class LibraryActionHandler @Inject constructor(
    private val autoAlbumsUseCase: AutoAlbumsUseCase,
    private val userAlbumsUseCase: UserAlbumsUseCase,
    private val photosUseCase: PhotosUseCase,
) : ActionHandler<LibraryState, LibraryEffect, LibraryAction, LibraryMutation> {

    private val loading = MutableSharedFlow<Boolean>()

    override fun handleAction(
        state: LibraryState,
        action: LibraryAction,
        effect: suspend (LibraryEffect) -> Unit,
    ): Flow<LibraryMutation> = when (action) {
        Load -> merge(
            autoAlbumsUseCase.observeAutoAlbums().mapToCover { it.cover }
                .map(::DisplayAutoAlbums),
            userAlbumsUseCase.observeUserAlbums().mapToCover { it.cover.photo1 }
                .map(::DisplayUserAlbums),
            favouritePhotos().mapToCover { it }
                .map(::DisplayFavouritePhotos),
            hiddenPhotos()
                .map { DisplayHiddenPhotos(it.isNotEmpty()) },
            loading
                .map(::Loading),
        ).safelyOnStartIgnoring {
            initialRefresh(effect)
        }
        Refresh -> flow {
            refreshAutoAlbums(effect)
            refreshUserAlbums(effect)
            refreshFavouritePhotos(effect)
            refreshHiddenPhotos(effect)
        }
        is AutoAlbumsSelected -> flow {
            effect(NavigateToAutoAlbums)
        }
        is UserAlbumsSelected -> flow {
            effect(NavigateToUserAlbums)
        }
        FavouritePhotosSelected -> flow {
            effect(NavigateToFavourites)
        }
        HiddenPhotosSelected -> flow {
            effect(NavigateToHidden)
        }
    }

    private fun favouritePhotos() = flow {
        emitAll(photosUseCase.observeFavouritePhotos()
            .getOrElse { emptyFlow() }
        )
    }

    private fun hiddenPhotos() = flow {
        emitAll(photosUseCase.observeHiddenPhotos()
            .getOrElse { emptyFlow() }
        )
    }

    private suspend fun initialRefresh(effect: suspend (LibraryEffect) -> Unit) {
        if (autoAlbumsUseCase.getAutoAlbums().isEmpty()) {
            refreshAutoAlbums(effect)
        }
        if (userAlbumsUseCase.getUserAlbums().isEmpty()) {
            refreshUserAlbums(effect)
        }
        if (photosUseCase.getFavouritePhotoSummaries().map { it.size }.getOrDefault(0) == 0) {
            refreshFavouritePhotos(effect)
        }
        if (photosUseCase.getHiddenPhotoSummaries().isEmpty()) {
            refreshHiddenPhotos(effect)
        }
    }

    private suspend fun refreshAutoAlbums(effect: suspend (LibraryEffect) -> Unit) {
        refresh(effect) {
            autoAlbumsUseCase.refreshAutoAlbums()
        }
    }

    private suspend fun refreshUserAlbums(effect: suspend (LibraryEffect) -> Unit) {
        refresh(effect) {
            userAlbumsUseCase.refreshUserAlbums()
        }
    }


    private suspend fun refreshFavouritePhotos(effect: suspend (LibraryEffect) -> Unit) {
        refresh(effect) {
            photosUseCase.refreshFavourites()
        }
    }


    private suspend fun refreshHiddenPhotos(effect: suspend (LibraryEffect) -> Unit) {
        refresh(effect) {
            photosUseCase.refreshHiddenPhotos()
        }
    }

    private suspend fun refresh(effect: suspend (LibraryEffect) -> Unit, refresh: suspend () -> Unit) {
        loading.emit(true)
        try {
            refresh()
        } catch (e: IOException) {
            log(e)
            effect(ErrorLoadingAlbums)
        } finally {
            // delaying to give ui time to receive the new albums before
            // dismissing the loading bar since no albums logic relies on that
            delay(500)
            loading.emit(false)
        }
    }

    private fun <T> Flow<List<T>>.mapToCover(cover: (T) -> Photo?): Flow<PhotoGrid> =
        map { albums ->
            albums.take(4).map(cover).let {
                PhotoGrid(
                    it.getOrNull(0),
                    it.getOrNull(1),
                    it.getOrNull(2),
                    it.getOrNull(3),
                )
            }
        }
}
