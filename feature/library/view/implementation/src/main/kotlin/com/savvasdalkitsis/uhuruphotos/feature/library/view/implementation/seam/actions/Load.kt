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
import com.savvasdalkitsis.uhuruphotos.feature.library.view.implementation.seam.LibraryMutation.DisplayAutoAlbums
import com.savvasdalkitsis.uhuruphotos.feature.library.view.implementation.seam.LibraryMutation.DisplayFavouriteMedia
import com.savvasdalkitsis.uhuruphotos.feature.library.view.implementation.seam.LibraryMutation.DisplayHidden
import com.savvasdalkitsis.uhuruphotos.feature.library.view.implementation.seam.LibraryMutation.DisplayLocalAlbums
import com.savvasdalkitsis.uhuruphotos.feature.library.view.implementation.seam.LibraryMutation.DisplayTrash
import com.savvasdalkitsis.uhuruphotos.feature.library.view.implementation.seam.LibraryMutation.DisplayUserAlbums
import com.savvasdalkitsis.uhuruphotos.feature.library.view.implementation.seam.LibraryMutation.Loading
import com.savvasdalkitsis.uhuruphotos.feature.library.view.implementation.seam.LibraryMutation.SetItemOrder
import com.savvasdalkitsis.uhuruphotos.feature.library.view.implementation.ui.state.LibraryItemState.AUTO
import com.savvasdalkitsis.uhuruphotos.feature.library.view.implementation.ui.state.LibraryItemState.FAVOURITE
import com.savvasdalkitsis.uhuruphotos.feature.library.view.implementation.ui.state.LibraryItemState.HIDDEN
import com.savvasdalkitsis.uhuruphotos.feature.library.view.implementation.ui.state.LibraryItemState.TRASH
import com.savvasdalkitsis.uhuruphotos.feature.library.view.implementation.ui.state.LibraryItemState.USER
import com.savvasdalkitsis.uhuruphotos.feature.library.view.implementation.ui.state.LibraryLocalMediaState
import com.savvasdalkitsis.uhuruphotos.feature.library.view.implementation.ui.state.LibraryState
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItemModel
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItemsOnDeviceModel.ErrorModel
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItemsOnDeviceModel.FoundModel
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItemsOnDeviceModel.RequiresPermissionsModel
import com.savvasdalkitsis.uhuruphotos.feature.media.common.view.api.ui.state.CelState
import com.savvasdalkitsis.uhuruphotos.feature.media.common.view.api.ui.state.VitrineState
import com.savvasdalkitsis.uhuruphotos.feature.media.common.view.api.ui.state.toCel
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.model.LocalMediaFolder
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.model.LocalMediaItems
import com.savvasdalkitsis.uhuruphotos.feature.welcome.domain.api.usecase.flow
import com.savvasdalkitsis.uhuruphotos.foundation.coroutines.api.safelyOnStartIgnoring
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.merge

@OptIn(FlowPreview::class)
data object Load : LibraryAction() {

    override fun LibraryActionsContext.handle(
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

    private fun LibraryActionsContext.trash() = welcomeUseCase.flow(
        withRemoteAccess = flowOf(DisplayTrash(true)),
        withoutRemoteAccess = observeShouldShowUpsellFromSource(TRASH).map(::DisplayTrash)
    )

    private fun LibraryActionsContext.hidden() = welcomeUseCase.flow(
        withRemoteAccess = flowOf(DisplayHidden(true)),
        withoutRemoteAccess = observeShouldShowUpsellFromSource(HIDDEN).map(::DisplayHidden)
    )

    private fun LibraryActionsContext.localAlbums() =
        combine(
            mediaUseCase.observeLocalMedia()
                .debounce(200),
            localMediaUseCase.areOtherFoldersBeingScanned(),
        ) { media, scanningOtherFolders ->
            when (media) {
                is FoundModel -> {
                    refreshLocalMedia()
                    val primary = listOfNotNull(media.primaryFolder)
                    val vitrines = (primary + media.mediaFolders).map { it.toVitrine() }
                    LibraryLocalMediaState.FoundState(vitrines.toImmutableList(), scanningOtherFolders)
                }

                is RequiresPermissionsModel ->
                    LibraryLocalMediaState.RequiresPermissionsState(media.deniedPermissions.toImmutableList())
                is ErrorModel -> LibraryLocalMediaState.FoundState(persistentListOf(), scanningOtherFolders)
            }
        }.distinctUntilChanged().map(::DisplayLocalAlbums)

    private fun LibraryActionsContext.userAlbums() = welcomeUseCase.flow(
        withRemoteAccess = userAlbumsUseCase.observeUserAlbums()
            .mapNotNull { albums ->
                albums.map { it.toUserAlbumState() }
            }
            .mapToCover { it.cover.cel1 }
            .map(::DisplayUserAlbums),
        withoutRemoteAccess = observeShouldShowUpsellFromSource(USER).map { show ->
            DisplayUserAlbums(VitrineState().takeIf { show })
        },
    )

    private fun LibraryActionsContext.autoAlbums() = welcomeUseCase.flow(
        withRemoteAccess = autoAlbumsUseCase.observeAutoAlbums()
            .mapToCover { it.cover.toCel() }
            .map(::DisplayAutoAlbums),
        withoutRemoteAccess = observeShouldShowUpsellFromSource(AUTO).mapNotNull { show ->
            DisplayAutoAlbums(VitrineState().takeIf { show })
        },
    )

    private suspend fun LibraryActionsContext.initialRefresh() {
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

    private fun LibraryActionsContext.favouriteMedia() = welcomeUseCase.flow(
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

    private fun Pair<LocalMediaFolder, List<MediaItemModel>>.toVitrine(): Pair<LocalMediaFolder, VitrineState> =
        first to VitrineState(second.map { it.toCel() })
}
