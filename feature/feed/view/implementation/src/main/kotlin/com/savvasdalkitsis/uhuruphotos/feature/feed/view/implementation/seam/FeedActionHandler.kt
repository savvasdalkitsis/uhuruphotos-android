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
package com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam

import com.savvasdalkitsis.uhuruphotos.api.albums.model.Album
import com.savvasdalkitsis.uhuruphotos.api.albums.usecase.AlbumsUseCase
import com.savvasdalkitsis.uhuruphotos.api.userbadge.ui.state.SyncState.IN_PROGRESS
import com.savvasdalkitsis.uhuruphotos.api.userbadge.usecase.UserBadgeUseCase
import com.savvasdalkitsis.uhuruphotos.feature.collage.view.api.ui.state.PredefinedCollageDisplay.YEARLY
import com.savvasdalkitsis.uhuruphotos.feature.feed.domain.api.usecase.FeedUseCase
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.SelectionList
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.FeedAction.AlbumRefreshClicked
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.FeedAction.AlbumSelectionClicked
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.FeedAction.AskForSelectedPhotosTrashing
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.FeedAction.ChangeDisplay
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.FeedAction.ClearSelected
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.FeedAction.DismissSelectedPhotosTrashing
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.FeedAction.DownloadSelectedPhotos
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.FeedAction.LoadFeed
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.FeedAction.PhotoLongPressed
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.FeedAction.RefreshAlbums
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.FeedAction.SelectedPhoto
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.FeedAction.ShareSelectedPhotos
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.FeedAction.TrashSelectedPhotos
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.FeedEffect.DownloadingFiles
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.FeedEffect.OpenLightbox
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.FeedEffect.SharePhotos
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.FeedEffect.Vibrate
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.FeedMutation.HideTrashingConfirmationDialog
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.FeedMutation.Loading
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.FeedMutation.ShowAlbums
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.FeedMutation.ShowLibrary
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.FeedMutation.ShowNoPhotosFound
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.FeedMutation.ShowTrashingConfirmationDialog
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.FeedMutation.StartRefreshing
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.FeedMutation.StopRefreshing
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.ui.state.FeedState
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItem
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItemSelectionMode.SELECTED
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItemSelectionMode.UNDEFINED
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItemSelectionMode.UNSELECTED
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.usecase.MediaUseCase
import com.savvasdalkitsis.uhuruphotos.feature.settings.domain.api.usecase.SettingsUseCase
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.ActionHandler
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import javax.inject.Inject

internal class FeedActionHandler @Inject constructor(
    private val albumsUseCase: AlbumsUseCase,
    private val userBadgeUseCase: UserBadgeUseCase,
    private val feedUseCase: FeedUseCase,
    private val mediaUseCase: MediaUseCase,
    private val selectionList: SelectionList,
    private val settingsUseCase: SettingsUseCase,
) : ActionHandler<FeedState, FeedEffect, FeedAction, FeedMutation> {

    override fun handleAction(
        state: FeedState,
        action: FeedAction,
        effect: suspend (FeedEffect) -> Unit,
    ): Flow<FeedMutation> = when (action) {
        is LoadFeed -> merge(
            settingsUseCase.observeShowLibrary()
                .map(::ShowLibrary),
            feedUseCase
                .getFeedDisplay()
                .distinctUntilChanged()
                .map(FeedMutation::ChangeDisplay),
            flowOf(Loading),
            combine(
                albumsUseCase.observeAlbums().debounce(200),
                selectionList.ids,
                userBadgeUseCase.getUserBadgeState(),
                feedUseCase
                    .getFeedDisplay()
                    .distinctUntilChanged()
            ) { albums, ids, userBadge, feedDisplay ->
                val selected = albums.selectPhotos(ids)
                val final = when (feedDisplay) {
                    YEARLY -> selected.groupByYear()
                    else -> selected
                }
                if (userBadge.syncState != IN_PROGRESS && final.photosCount == 0) {
                    ShowNoPhotosFound
                } else {
                    ShowAlbums(final)
                }
            },
        )
        RefreshAlbums -> flow {
            emit(StartRefreshing)
            albumsUseCase.startRefreshAlbumsWork(shallow = true)
            delay(200)
            emit(StopRefreshing)
        }
        is SelectedPhoto -> flow {
            when {
                state.selectedPhotoCount == 0 -> effect(with(action) {
                    OpenLightbox(mediaItem.id, center, scale, mediaItem.isVideo)
                })
                action.mediaItem.selectionMode == SELECTED -> {
                    effect(Vibrate)
                    action.mediaItem.deselect()
                }
                else -> {
                    effect(Vibrate)
                    action.mediaItem.select()
                }
            }
        }
        is ChangeDisplay -> flow {
            feedUseCase.setFeedDisplay(action.display)
        }
        is PhotoLongPressed -> flow {
            if (state.selectedPhotoCount == 0) {
                effect(Vibrate)
                action.mediaItem.select()
            }
        }
        ClearSelected -> flow {
            effect(Vibrate)
            selectionList.clear()
        }
        AskForSelectedPhotosTrashing -> flowOf(ShowTrashingConfirmationDialog)
        is AlbumSelectionClicked -> flow {
            val photos = action.album.photos
            effect(Vibrate)
            if (photos.all { it.selectionMode == SELECTED }) {
                photos.forEach { it.deselect() }
            } else {
                photos.forEach { it.select() }
            }
        }
        is AlbumRefreshClicked -> flow {
            emit(StartRefreshing)
            albumsUseCase.refreshAlbum(action.album.id)
            emit(StopRefreshing)
        }
        DismissSelectedPhotosTrashing -> flowOf(HideTrashingConfirmationDialog)
        TrashSelectedPhotos -> flow {
            emit(HideTrashingConfirmationDialog)
            state.selectedMediaItem.forEach {
                mediaUseCase.trashMediaItem(it.id)
            }
            selectionList.clear()
        }
        ShareSelectedPhotos -> flow {
            effect(SharePhotos(state.selectedMediaItem))
        }
        DownloadSelectedPhotos -> flow {
            effect(DownloadingFiles)
            state.selectedMediaItem.forEach {
                mediaUseCase.downloadOriginal(it.id, it.isVideo)
            }
        }
    }

    private suspend fun MediaItem.deselect() {
        selectionList.deselect(id)
    }

    private suspend fun MediaItem.select() {
        selectionList.select(id)
    }

    private val List<Album>.photosCount get() = sumOf { it.photos.size }

    private fun List<Album>.selectPhotos(ids: Set<String>): List<Album> {
        val empty = ids.isEmpty()
        return map { album ->
            album.copy(photos = album.photos.map { photo ->
                photo.copy(selectionMode = when {
                    empty -> UNDEFINED
                    photo.id.value.toString() in ids -> SELECTED
                    else -> UNSELECTED
                })
            })
        }
    }

    private fun List<Album>.groupByYear() = groupBy {
        it.unformattedDate?.split("-")?.get(0)
    }.map { (year, albums) ->
        Album(
            id = year ?: "-",
            photos = albums.flatMap { it.photos },
            displayTitle = year ?: "-",
            location = null,
        )
    }
}
