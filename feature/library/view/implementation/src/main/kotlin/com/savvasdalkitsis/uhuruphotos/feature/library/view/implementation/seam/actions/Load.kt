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

import com.github.michaelbull.result.getOr
import com.savvasdalkitsis.uhuruphotos.feature.catalogue.user.view.api.state.toUserAlbumState
import com.savvasdalkitsis.uhuruphotos.feature.library.view.implementation.seam.LibraryActionsContext
import com.savvasdalkitsis.uhuruphotos.feature.library.view.implementation.seam.LibraryMutation
import com.savvasdalkitsis.uhuruphotos.feature.library.view.implementation.seam.LibraryMutation.DisplayAutoAlbums
import com.savvasdalkitsis.uhuruphotos.feature.library.view.implementation.seam.LibraryMutation.DisplayFavouriteMedia
import com.savvasdalkitsis.uhuruphotos.feature.library.view.implementation.seam.LibraryMutation.DisplayHidden
import com.savvasdalkitsis.uhuruphotos.feature.library.view.implementation.seam.LibraryMutation.DisplayLocalAlbums
import com.savvasdalkitsis.uhuruphotos.feature.library.view.implementation.seam.LibraryMutation.DisplayTrash
import com.savvasdalkitsis.uhuruphotos.feature.library.view.implementation.seam.LibraryMutation.DisplayUserAlbums
import com.savvasdalkitsis.uhuruphotos.feature.library.view.implementation.seam.LibraryMutation.Loading
import com.savvasdalkitsis.uhuruphotos.feature.library.view.implementation.seam.LibraryMutation.SetItemOrder
import com.savvasdalkitsis.uhuruphotos.feature.library.view.implementation.ui.state.LibraryItem
import com.savvasdalkitsis.uhuruphotos.feature.library.view.implementation.ui.state.LibraryItem.*
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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.merge

data object Load : LibraryAction() {

    context(LibraryActionsContext) override fun handle(
        state: LibraryState
    ) = merge(
        libraryUseCase.getLibraryItems().map(::SetItemOrder),
        autoAlbums(),
        userAlbums(),
        favouriteMedia(),
        loading.map(::Loading),
        localAlbums(),
        trash(),
        hidden(),
    ).safelyOnStartIgnoring {
        initialRefresh()
    }

    context(LibraryActionsContext)
    private fun trash() = welcomeUseCase.flow(
        withRemoteAccess = flowOf(DisplayTrash(true)),
        withoutRemoteAccess = observeShouldShowUpsellFromSource(TRASH).map(::DisplayTrash)
    )

    context(LibraryActionsContext)
    private fun hidden() = welcomeUseCase.flow(
        withRemoteAccess = flowOf(DisplayHidden(true)),
        withoutRemoteAccess = observeShouldShowUpsellFromSource(HIDDEN).map(::DisplayHidden)
    )

    context(LibraryActionsContext)
    private fun localAlbums() =
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
            .map(::DisplayLocalAlbums)

    context(LibraryActionsContext)
    private fun userAlbums() = welcomeUseCase.flow(
        withRemoteAccess = userAlbumsUseCase.observeUserAlbums()
            .mapNotNull { albums ->
                serverUseCase.getServerUrl()?.let { serverUrl ->
                    albums.map { it.toUserAlbumState(serverUrl) }
                }
            }
            .mapToCover { it.cover.cel1 }
            .map(::DisplayUserAlbums),
        withoutRemoteAccess = observeShouldShowUpsellFromSource(USER).map { show ->
            DisplayUserAlbums(VitrineState().takeIf { show })
        },
    )

    context(LibraryActionsContext)
    private fun autoAlbums() = welcomeUseCase.flow(
        withRemoteAccess = autoAlbumsUseCase.observeAutoAlbums()
            .mapToCover { it.cover.toCel() }
            .map(::DisplayAutoAlbums),
        withoutRemoteAccess = observeShouldShowUpsellFromSource(AUTO).mapNotNull { show ->
            DisplayAutoAlbums(VitrineState().takeIf { show })
        },
    )

    context(LibraryActionsContext)
    private suspend fun initialRefresh() {
        if (welcomeUseCase.getWelcomeStatus().hasRemoteAccess) {
            if (autoAlbumsUseCase.getAutoAlbums().isEmpty()) {
                refreshAutoAlbums()
            }
            if (userAlbumsUseCase.getUserAlbums().isEmpty()) {
                refreshUserAlbums()
            }
            if (mediaUseCase.getFavouriteMediaCount().getOr(0) == 0L) {
                refreshFavouriteMedia()
            }
            if (mediaUseCase.getHiddenMedia().getOr(emptyList()).isEmpty()) {
                refreshHiddenMedia()
            }
        }
        val localItems = localMediaUseCase.getLocalMediaItems()
        if (localItems is LocalMediaItems.Found && localItems.allFolders.isEmpty()) {
            refreshLocalMedia()
        }
    }

    context(LibraryActionsContext)
    private fun favouriteMedia() = welcomeUseCase.flow(
        withRemoteAccess = mediaUseCase.observeFavouriteMedia()
            .mapNotNull { it.getOr(null) }.mapToCover { it.toCel() }
            .map(::DisplayFavouriteMedia),
        withoutRemoteAccess = observeShouldShowUpsellFromSource(FAVOURITE).map { show ->
            DisplayFavouriteMedia(VitrineState().takeIf { show })
        },
    )

    private fun <T> Flow<List<T>>.mapToCover(cover: (T) -> CelState?): Flow<VitrineState> =
        map { albums ->
            albums.take(4).map(cover).let {
                VitrineState(it)
            }
        }

    private fun Pair<LocalMediaFolder, List<MediaItem>>.toVitrine(): Pair<LocalMediaFolder, VitrineState> =
        first to VitrineState(second.map { it.toCel() })
}
