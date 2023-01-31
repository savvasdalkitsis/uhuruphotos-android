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
package com.savvasdalkitsis.uhuruphotos.feature.library.view.implementation.seam

import com.savvasdalkitsis.uhuruphotos.feature.catalogue.auto.domain.api.usecase.AutoAlbumsUseCase
import com.savvasdalkitsis.uhuruphotos.feature.catalogue.user.domain.api.usecase.UserAlbumsUseCase
import com.savvasdalkitsis.uhuruphotos.feature.catalogue.user.view.api.state.toUserAlbumState
import com.savvasdalkitsis.uhuruphotos.feature.library.view.implementation.seam.LibraryAction.AutoAlbumsSelected
import com.savvasdalkitsis.uhuruphotos.feature.library.view.implementation.seam.LibraryAction.FavouritePhotosSelected
import com.savvasdalkitsis.uhuruphotos.feature.library.view.implementation.seam.LibraryAction.HiddenPhotosSelected
import com.savvasdalkitsis.uhuruphotos.feature.library.view.implementation.seam.LibraryAction.Load
import com.savvasdalkitsis.uhuruphotos.feature.library.view.implementation.seam.LibraryAction.LocalBucketSelected
import com.savvasdalkitsis.uhuruphotos.feature.library.view.implementation.seam.LibraryAction.Refresh
import com.savvasdalkitsis.uhuruphotos.feature.library.view.implementation.seam.LibraryAction.RefreshLocalMedia
import com.savvasdalkitsis.uhuruphotos.feature.library.view.implementation.seam.LibraryAction.TrashSelected
import com.savvasdalkitsis.uhuruphotos.feature.library.view.implementation.seam.LibraryAction.UserAlbumsSelected
import com.savvasdalkitsis.uhuruphotos.feature.library.view.implementation.seam.LibraryEffect.ErrorLoadingAlbums
import com.savvasdalkitsis.uhuruphotos.feature.library.view.implementation.seam.LibraryEffect.NavigateToAutoAlbums
import com.savvasdalkitsis.uhuruphotos.feature.library.view.implementation.seam.LibraryEffect.NavigateToFavourites
import com.savvasdalkitsis.uhuruphotos.feature.library.view.implementation.seam.LibraryEffect.NavigateToHidden
import com.savvasdalkitsis.uhuruphotos.feature.library.view.implementation.seam.LibraryEffect.NavigateToLocalBucket
import com.savvasdalkitsis.uhuruphotos.feature.library.view.implementation.seam.LibraryEffect.NavigateToTrash
import com.savvasdalkitsis.uhuruphotos.feature.library.view.implementation.seam.LibraryEffect.NavigateToUserAlbums
import com.savvasdalkitsis.uhuruphotos.feature.library.view.implementation.seam.LibraryMutation.DisplayAutoAlbums
import com.savvasdalkitsis.uhuruphotos.feature.library.view.implementation.seam.LibraryMutation.DisplayFavouritePhotos
import com.savvasdalkitsis.uhuruphotos.feature.library.view.implementation.seam.LibraryMutation.DisplayLocalAlbums
import com.savvasdalkitsis.uhuruphotos.feature.library.view.implementation.seam.LibraryMutation.DisplayUserAlbums
import com.savvasdalkitsis.uhuruphotos.feature.library.view.implementation.seam.LibraryMutation.Loading
import com.savvasdalkitsis.uhuruphotos.feature.library.view.implementation.ui.state.LibraryLocalMedia
import com.savvasdalkitsis.uhuruphotos.feature.library.view.implementation.ui.state.LibraryState
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItem
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItemsOnDevice.*
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.usecase.MediaUseCase
import com.savvasdalkitsis.uhuruphotos.feature.media.common.view.api.ui.state.CelState
import com.savvasdalkitsis.uhuruphotos.feature.media.common.view.api.ui.state.VitrineState
import com.savvasdalkitsis.uhuruphotos.feature.media.common.view.api.ui.state.toCel
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.model.LocalMediaFolder
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.model.LocalMediaItems
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.usecase.LocalMediaUseCase
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.worker.LocalMediaWorkScheduler
import com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.api.usecase.RemoteMediaUseCase
import com.savvasdalkitsis.uhuruphotos.foundation.coroutines.api.safelyOnStartIgnoring
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.ActionHandler
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.merge
import javax.inject.Inject

class LibraryActionHandler @Inject constructor(
    private val autoAlbumsUseCase: AutoAlbumsUseCase,
    private val userAlbumsUseCase: UserAlbumsUseCase,
    private val mediaUseCase: MediaUseCase,
    private val remoteMediaUseCase: RemoteMediaUseCase,
    private val localMediaUseCase: LocalMediaUseCase,
    private val localMediaWorkScheduler: LocalMediaWorkScheduler,
) : ActionHandler<LibraryState, LibraryEffect, LibraryAction, LibraryMutation> {

    private val loading = MutableSharedFlow<Boolean>()

    override fun handleAction(
        state: LibraryState,
        action: LibraryAction,
        effect: suspend (LibraryEffect) -> Unit,
    ): Flow<LibraryMutation> = when (action) {
        Load -> merge(
            autoAlbumsUseCase.observeAutoAlbums()
                .mapToCover { it.cover.toCel() }
                .map(::DisplayAutoAlbums),
            userAlbumsUseCase.observeUserAlbums()
                .map { albums ->
                    with(remoteMediaUseCase) {
                        albums.map { it.toUserAlbumState() }
                    }
                }
                .mapToCover { it.cover.cel1 }
                .map(::DisplayUserAlbums),
            favouriteMedia().mapToCover { it.toCel() }
                .map(::DisplayFavouritePhotos),
            loading
                .map(::Loading),
            mediaUseCase.observeLocalMedia()
                .debounce(200)
                .map { media ->
                    when (media) {
                        is Found -> {
                            val primary = listOfNotNull(media.primaryFolder)
                            val vitrines = (primary + media.mediaFolders).map { it.toVitrine() }
                            if (vitrines.isEmpty()) {
                                refreshLocalMedia()
                            }
                            LibraryLocalMedia.Found(vitrines)
                        }
                        is RequiresPermissions ->
                            LibraryLocalMedia.RequiresPermissions(media.deniedPermissions)
                        is Error -> LibraryLocalMedia.Found(emptyList())
                    }
                }
                .distinctUntilChanged()
                .map(::DisplayLocalAlbums)
        ).safelyOnStartIgnoring {
            initialRefresh(effect)
        }
        Refresh -> flow {
            refreshAutoAlbums(effect)
            refreshUserAlbums(effect)
            refreshFavouriteMedia(effect)
            refreshHiddenMedia(effect)
            refreshLocalMedia()
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
        TrashSelected -> flow {
            effect(NavigateToTrash)
        }
        RefreshLocalMedia -> flow {
            refreshLocalMedia()
        }
        is LocalBucketSelected -> flow {
           effect(NavigateToLocalBucket(action.bucket))
        }
    }

    private fun favouriteMedia() = mediaUseCase.observeFavouriteMedia()
        .mapNotNull { it.getOrNull() }

    private suspend fun initialRefresh(effect: suspend (LibraryEffect) -> Unit) {
        if (autoAlbumsUseCase.getAutoAlbums().isEmpty()) {
            refreshAutoAlbums(effect)
        }
        if (userAlbumsUseCase.getUserAlbums().isEmpty()) {
            refreshUserAlbums(effect)
        }
        if (mediaUseCase.getFavouriteMediaCount().map { it }.getOrDefault(0) == 0L) {
            refreshFavouriteMedia(effect)
        }
        if (mediaUseCase.getHiddenMedia().getOrDefault(emptyList()).isEmpty()) {
            refreshHiddenMedia(effect)
        }
        val localItems = localMediaUseCase.getLocalMediaItems()
        if (localItems is LocalMediaItems.Found && localItems.allFolders.isEmpty()) {
            refreshLocalMedia()
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

    private suspend fun refreshFavouriteMedia(effect: suspend (LibraryEffect) -> Unit) {
        refresh(effect) {
            mediaUseCase.refreshFavouriteMedia()
        }
    }

    private suspend fun refreshHiddenMedia(effect: suspend (LibraryEffect) -> Unit) {
        refresh(effect) {
            mediaUseCase.refreshHiddenMedia()
        }
    }

    private fun refreshLocalMedia() {
        localMediaWorkScheduler.scheduleLocalMediaSyncNowIfNotRunning()
    }

    private suspend fun refresh(
        effect: suspend (LibraryEffect) -> Unit,
        refresh: suspend () -> Result<Unit>,
    ) {
        loading.emit(true)
        val result = refresh()
        if (result.isFailure) {
            effect(ErrorLoadingAlbums)
        }
        // delaying to give ui time to receive the new albums before
        // dismissing the loading bar since no albums logic relies on that
        delay(500)
        loading.emit(false)
    }

    private fun <T> Flow<List<T>>.mapToCover(cover: (T) -> CelState?): Flow<VitrineState> =
        map { albums ->
            albums.take(4).map(cover).let {
                VitrineState(it)
            }
        }

    private fun Pair<LocalMediaFolder, List<MediaItem>>.toVitrine(): Pair<LocalMediaFolder, VitrineState> =
        first to VitrineState(second.map { it.toCel() })
}
