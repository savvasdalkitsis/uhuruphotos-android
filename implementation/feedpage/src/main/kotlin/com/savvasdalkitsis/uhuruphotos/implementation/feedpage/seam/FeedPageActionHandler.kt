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
package com.savvasdalkitsis.uhuruphotos.implementation.feedpage.seam

import com.savvasdalkitsis.uhuruphotos.api.albums.model.Album
import com.savvasdalkitsis.uhuruphotos.api.albums.usecase.AlbumsUseCase
import com.savvasdalkitsis.uhuruphotos.api.photos.model.Photo
import com.savvasdalkitsis.uhuruphotos.api.photos.model.SelectionMode.SELECTED
import com.savvasdalkitsis.uhuruphotos.api.photos.model.SelectionMode.UNDEFINED
import com.savvasdalkitsis.uhuruphotos.api.photos.model.SelectionMode.UNSELECTED
import com.savvasdalkitsis.uhuruphotos.api.photos.usecase.PhotosUseCase
import com.savvasdalkitsis.uhuruphotos.api.seam.ActionHandler
import com.savvasdalkitsis.uhuruphotos.api.settings.usecase.SettingsUseCase
import com.savvasdalkitsis.uhuruphotos.api.userbadge.usecase.UserBadgeUseCase
import com.savvasdalkitsis.uhuruphotos.api.userbadge.view.state.SyncState.IN_PROGRESS
import com.savvasdalkitsis.uhuruphotos.implementation.feedpage.SelectionList
import com.savvasdalkitsis.uhuruphotos.implementation.feedpage.seam.FeedPageAction.*
import com.savvasdalkitsis.uhuruphotos.implementation.feedpage.seam.FeedPageEffect.DownloadingFiles
import com.savvasdalkitsis.uhuruphotos.implementation.feedpage.seam.FeedPageEffect.OpenPhotoDetails
import com.savvasdalkitsis.uhuruphotos.implementation.feedpage.seam.FeedPageEffect.SharePhotos
import com.savvasdalkitsis.uhuruphotos.implementation.feedpage.seam.FeedPageEffect.Vibrate
import com.savvasdalkitsis.uhuruphotos.implementation.feedpage.seam.FeedPageMutation.HideTrashingConfirmationDialog
import com.savvasdalkitsis.uhuruphotos.implementation.feedpage.seam.FeedPageMutation.Loading
import com.savvasdalkitsis.uhuruphotos.implementation.feedpage.seam.FeedPageMutation.ShowAlbums
import com.savvasdalkitsis.uhuruphotos.implementation.feedpage.seam.FeedPageMutation.ShowTrashingConfirmationDialog
import com.savvasdalkitsis.uhuruphotos.implementation.feedpage.seam.FeedPageMutation.ShowLibrary
import com.savvasdalkitsis.uhuruphotos.implementation.feedpage.seam.FeedPageMutation.ShowNoPhotosFound
import com.savvasdalkitsis.uhuruphotos.implementation.feedpage.seam.FeedPageMutation.StartRefreshing
import com.savvasdalkitsis.uhuruphotos.implementation.feedpage.seam.FeedPageMutation.StopRefreshing
import com.savvasdalkitsis.uhuruphotos.implementation.feedpage.usecase.FeedPageUseCase
import com.savvasdalkitsis.uhuruphotos.implementation.feedpage.view.state.FeedPageState
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

internal class FeedPageActionHandler @Inject constructor(
    private val albumsUseCase: AlbumsUseCase,
    private val userBadgeUseCase: UserBadgeUseCase,
    private val feedPageUseCase: FeedPageUseCase,
    private val photosUseCase: PhotosUseCase,
    private val selectionList: SelectionList,
    private val settingsUseCase: SettingsUseCase,
) : ActionHandler<FeedPageState, FeedPageEffect, FeedPageAction, FeedPageMutation> {

    override fun handleAction(
        state: FeedPageState,
        action: FeedPageAction,
        effect: suspend (FeedPageEffect) -> Unit,
    ): Flow<FeedPageMutation> = when (action) {
        is LoadFeed -> merge(
            settingsUseCase.observeShowLibrary()
                .map(::ShowLibrary),
            feedPageUseCase
                .getFeedDisplay()
                .distinctUntilChanged()
                .map(FeedPageMutation::ChangeDisplay),
            flowOf(Loading),
            combine(
                albumsUseCase.observeAlbums().debounce(200),
                selectionList.ids,
                userBadgeUseCase.getUserBadgeState()
            ) { albums, ids, userBadge ->
                val selected = albums.selectPhotos(ids)
                if (userBadge.syncState != IN_PROGRESS && selected.photosCount == 0) {
                    ShowNoPhotosFound
                } else {
                    ShowAlbums(selected)
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
                    OpenPhotoDetails(photo.id, center, scale, photo.isVideo)
                })
                action.photo.selectionMode == SELECTED -> {
                    effect(Vibrate)
                    action.photo.deselect()
                }
                else -> {
                    effect(Vibrate)
                    action.photo.select()
                }
            }
        }
        is ChangeDisplay -> flow {
            feedPageUseCase.setFeedDisplay(action.display)
        }
        is PhotoLongPressed -> flow {
            if (state.selectedPhotoCount == 0) {
                effect(Vibrate)
                action.photo.select()
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
            state.selectedPhotos.forEach {
                photosUseCase.trashPhoto(it.id)
            }
            selectionList.clear()
        }
        ShareSelectedPhotos -> flow {
            effect(SharePhotos(state.selectedPhotos))
        }
        DownloadSelectedPhotos -> flow {
            effect(DownloadingFiles)
            state.selectedPhotos.forEach {
                photosUseCase.downloadOriginal(it.id, it.isVideo)
            }
        }
    }

    private suspend fun Photo.deselect() {
        selectionList.deselect(id)
    }

    private suspend fun Photo.select() {
        selectionList.select(id)
    }

    private val List<Album>.photosCount get() = sumOf { it.photos.size }

    private fun List<Album>.selectPhotos(ids: Set<String>): List<Album> {
        val empty = ids.isEmpty()
        return map { album ->
            album.copy(photos = album.photos.map { photo ->
                photo.copy(selectionMode = when {
                    empty -> UNDEFINED
                    photo.id in ids -> SELECTED
                    else -> UNSELECTED
                })
            })
        }
    }
}
