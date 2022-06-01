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
package com.savvasdalkitsis.uhuruphotos.feedpage.viewmodel

import com.savvasdalkitsis.uhuruphotos.account.usecase.AccountUseCase
import com.savvasdalkitsis.uhuruphotos.albums.api.model.Album
import com.savvasdalkitsis.uhuruphotos.albums.api.usecase.AlbumsUseCase
import com.savvasdalkitsis.uhuruphotos.feedpage.SelectionList
import com.savvasdalkitsis.uhuruphotos.feedpage.mvflow.FeedPageAction
import com.savvasdalkitsis.uhuruphotos.feedpage.mvflow.FeedPageAction.AlbumSelectionClicked
import com.savvasdalkitsis.uhuruphotos.feedpage.mvflow.FeedPageAction.AskForSelectedPhotosDeletion
import com.savvasdalkitsis.uhuruphotos.feedpage.mvflow.FeedPageAction.AskToLogOut
import com.savvasdalkitsis.uhuruphotos.feedpage.mvflow.FeedPageAction.ChangeDisplay
import com.savvasdalkitsis.uhuruphotos.feedpage.mvflow.FeedPageAction.ClearSelected
import com.savvasdalkitsis.uhuruphotos.feedpage.mvflow.FeedPageAction.DeleteSelectedPhotos
import com.savvasdalkitsis.uhuruphotos.feedpage.mvflow.FeedPageAction.DismissAccountOverview
import com.savvasdalkitsis.uhuruphotos.feedpage.mvflow.FeedPageAction.DismissLogOutDialog
import com.savvasdalkitsis.uhuruphotos.feedpage.mvflow.FeedPageAction.DismissSelectedPhotosDeletion
import com.savvasdalkitsis.uhuruphotos.feedpage.mvflow.FeedPageAction.EditServer
import com.savvasdalkitsis.uhuruphotos.feedpage.mvflow.FeedPageAction.LoadFeed
import com.savvasdalkitsis.uhuruphotos.feedpage.mvflow.FeedPageAction.LogOut
import com.savvasdalkitsis.uhuruphotos.feedpage.mvflow.FeedPageAction.PhotoLongPressed
import com.savvasdalkitsis.uhuruphotos.feedpage.mvflow.FeedPageAction.RefreshAlbums
import com.savvasdalkitsis.uhuruphotos.feedpage.mvflow.FeedPageAction.SelectedPhoto
import com.savvasdalkitsis.uhuruphotos.feedpage.mvflow.FeedPageAction.SettingsClick
import com.savvasdalkitsis.uhuruphotos.feedpage.mvflow.FeedPageAction.ShareSelectedPhotos
import com.savvasdalkitsis.uhuruphotos.feedpage.mvflow.FeedPageAction.UserBadgePressed
import com.savvasdalkitsis.uhuruphotos.feedpage.mvflow.FeedPageEffect
import com.savvasdalkitsis.uhuruphotos.feedpage.mvflow.FeedPageEffect.NavigateToServerEdit
import com.savvasdalkitsis.uhuruphotos.feedpage.mvflow.FeedPageEffect.NavigateToSettings
import com.savvasdalkitsis.uhuruphotos.feedpage.mvflow.FeedPageEffect.OpenPhotoDetails
import com.savvasdalkitsis.uhuruphotos.feedpage.mvflow.FeedPageEffect.ReloadApp
import com.savvasdalkitsis.uhuruphotos.feedpage.mvflow.FeedPageEffect.SharePhotos
import com.savvasdalkitsis.uhuruphotos.feedpage.mvflow.FeedPageEffect.Vibrate
import com.savvasdalkitsis.uhuruphotos.feedpage.mvflow.FeedPageMutation
import com.savvasdalkitsis.uhuruphotos.feedpage.mvflow.FeedPageMutation.HideAccountOverview
import com.savvasdalkitsis.uhuruphotos.feedpage.mvflow.FeedPageMutation.HideDeletionConfirmationDialog
import com.savvasdalkitsis.uhuruphotos.feedpage.mvflow.FeedPageMutation.HideLogOutConfirmation
import com.savvasdalkitsis.uhuruphotos.feedpage.mvflow.FeedPageMutation.Loading
import com.savvasdalkitsis.uhuruphotos.feedpage.mvflow.FeedPageMutation.ShowAccountOverview
import com.savvasdalkitsis.uhuruphotos.feedpage.mvflow.FeedPageMutation.ShowAlbums
import com.savvasdalkitsis.uhuruphotos.feedpage.mvflow.FeedPageMutation.ShowDeletionConfirmationDialog
import com.savvasdalkitsis.uhuruphotos.feedpage.mvflow.FeedPageMutation.ShowLibrary
import com.savvasdalkitsis.uhuruphotos.feedpage.mvflow.FeedPageMutation.ShowLogOutConfirmation
import com.savvasdalkitsis.uhuruphotos.feedpage.mvflow.FeedPageMutation.StartRefreshing
import com.savvasdalkitsis.uhuruphotos.feedpage.mvflow.FeedPageMutation.StopRefreshing
import com.savvasdalkitsis.uhuruphotos.feedpage.mvflow.FeedPageMutation.UserBadgeUpdate
import com.savvasdalkitsis.uhuruphotos.feedpage.usecase.FeedPageUseCase
import com.savvasdalkitsis.uhuruphotos.feedpage.view.state.FeedPageState
import com.savvasdalkitsis.uhuruphotos.photos.api.model.Photo
import com.savvasdalkitsis.uhuruphotos.photos.api.model.SelectionMode.SELECTED
import com.savvasdalkitsis.uhuruphotos.photos.api.model.SelectionMode.UNDEFINED
import com.savvasdalkitsis.uhuruphotos.photos.api.model.SelectionMode.UNSELECTED
import com.savvasdalkitsis.uhuruphotos.photos.usecase.PhotosUseCase
import com.savvasdalkitsis.uhuruphotos.api.settings.usecase.SettingsUseCase
import com.savvasdalkitsis.uhuruphotos.feedpage.mvflow.FeedPageMutation.ShowNoPhotosFound
import com.savvasdalkitsis.uhuruphotos.userbadge.api.UserBadgeUseCase
import com.savvasdalkitsis.uhuruphotos.userbadge.api.view.state.SyncState.IN_PROGRESS
import com.savvasdalkitsis.uhuruphotos.viewmodel.ActionHandler
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

class FeedPageHandler @Inject constructor(
    private val albumsUseCase: AlbumsUseCase,
    private val userBadgeUseCase: UserBadgeUseCase,
    private val accountUseCase: AccountUseCase,
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
                albums.selectPhotos(ids)
                if (userBadge.syncState != IN_PROGRESS && albums.photosCount == 0) {
                    ShowNoPhotosFound
                } else {
                    ShowAlbums(albums)
                }
            },
            userBadgeUseCase.getUserBadgeState()
                .map(::UserBadgeUpdate),
        )
        UserBadgePressed -> flowOf(ShowAccountOverview)
        DismissAccountOverview -> flowOf(HideAccountOverview)
        AskToLogOut -> flowOf(ShowLogOutConfirmation)
        DismissLogOutDialog -> flowOf(HideLogOutConfirmation)
        LogOut -> flow {
            accountUseCase.logOut()
            effect(ReloadApp)
        }
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
        AskForSelectedPhotosDeletion -> flowOf(ShowDeletionConfirmationDialog)
        is AlbumSelectionClicked -> flow {
            val photos = action.album.photos
            effect(Vibrate)
            if (photos.all { it.selectionMode == SELECTED }) {
                photos.forEach { it.deselect() }
            } else {
                photos.forEach { it.select() }
            }
        }
        DismissSelectedPhotosDeletion -> flowOf(HideDeletionConfirmationDialog)
        DeleteSelectedPhotos -> flow {
            emit(HideDeletionConfirmationDialog)
            state.selectedPhotos.forEach {
                photosUseCase.deletePhoto(it.id)
            }
            selectionList.clear()
        }
        ShareSelectedPhotos -> flow {
            effect(SharePhotos(state.selectedPhotos))
        }
        EditServer -> flow {
            emit(HideAccountOverview)
            effect(NavigateToServerEdit)
        }
        SettingsClick -> flow {
            emit(HideAccountOverview)
            effect(NavigateToSettings)
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
