/*
Copyright 2023 Savvas Dalkitsis

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
package com.savvasdalkitsis.uhuruphotos.feature.library.view.implementation.seam.actions

import com.savvasdalkitsis.uhuruphotos.feature.catalogue.user.view.api.state.toUserAlbumState
import com.savvasdalkitsis.uhuruphotos.feature.library.view.implementation.seam.LibraryActionsContext
import com.savvasdalkitsis.uhuruphotos.feature.library.view.implementation.seam.LibraryEffect
import com.savvasdalkitsis.uhuruphotos.feature.library.view.implementation.seam.LibraryMutation
import com.savvasdalkitsis.uhuruphotos.feature.library.view.implementation.ui.state.LibraryLocalMedia
import com.savvasdalkitsis.uhuruphotos.feature.library.view.implementation.ui.state.LibraryState
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItem
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItemsOnDevice
import com.savvasdalkitsis.uhuruphotos.feature.media.common.view.api.ui.state.CelState
import com.savvasdalkitsis.uhuruphotos.feature.media.common.view.api.ui.state.VitrineState
import com.savvasdalkitsis.uhuruphotos.feature.media.common.view.api.ui.state.toCel
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.model.LocalMediaFolder
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.model.LocalMediaItems
import com.savvasdalkitsis.uhuruphotos.foundation.coroutines.api.safelyOnStartIgnoring
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.EffectHandler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.merge

object Load : LibraryAction() {

    context(LibraryActionsContext) override fun handle(
        state: LibraryState,
        effect: EffectHandler<LibraryEffect>
    ) = merge(
        autoAlbumsUseCase.observeAutoAlbums()
            .mapToCover { it.cover.toCel() }
            .map(LibraryMutation::DisplayAutoAlbums),
        userAlbumsUseCase.observeUserAlbums()
            .map { albums ->
                with(remoteMediaUseCase) {
                    albums.map { it.toUserAlbumState() }
                }
            }
            .mapToCover { it.cover.cel1 }
            .map(LibraryMutation::DisplayUserAlbums),
        favouriteMedia().mapToCover { it.toCel() }
            .map(LibraryMutation::DisplayFavouritePhotos),
        loading
            .map(LibraryMutation::Loading),
        mediaUseCase.observeLocalMedia()
            .debounce(200)
            .map { media ->
                when (media) {
                    is MediaItemsOnDevice.Found -> {
                        refreshLocalMedia()
                        val primary = listOfNotNull(media.primaryFolder)
                        val vitrines = (primary + media.mediaFolders).map { it.toVitrine() }
                        LibraryLocalMedia.Found(vitrines)
                    }
                    is MediaItemsOnDevice.RequiresPermissions ->
                        LibraryLocalMedia.RequiresPermissions(media.deniedPermissions)
                    is MediaItemsOnDevice.Error -> LibraryLocalMedia.Found(emptyList())
                }
            }
            .distinctUntilChanged()
            .map(LibraryMutation::DisplayLocalAlbums)
    ).safelyOnStartIgnoring {
        initialRefresh(effect)
    }

    context(LibraryActionsContext)
    private suspend fun initialRefresh(effect: EffectHandler<LibraryEffect>) {
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

    context(LibraryActionsContext)
    private fun favouriteMedia() = mediaUseCase.observeFavouriteMedia()
        .mapNotNull { it.getOrNull() }

    private fun <T> Flow<List<T>>.mapToCover(cover: (T) -> CelState?): Flow<VitrineState> =
        map { albums ->
            albums.take(4).map(cover).let {
                VitrineState(it)
            }
        }

    private fun Pair<LocalMediaFolder, List<MediaItem>>.toVitrine(): Pair<LocalMediaFolder, VitrineState> =
        first to VitrineState(second.map { it.toCel() })
}
