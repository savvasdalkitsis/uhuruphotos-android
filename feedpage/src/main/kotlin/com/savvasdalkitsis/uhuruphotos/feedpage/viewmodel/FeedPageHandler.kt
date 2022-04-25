package com.savvasdalkitsis.uhuruphotos.feedpage.viewmodel

import com.savvasdalkitsis.uhuruphotos.account.usecase.AccountUseCase
import com.savvasdalkitsis.uhuruphotos.albums.model.Album
import com.savvasdalkitsis.uhuruphotos.albums.usecase.AlbumsUseCase
import com.savvasdalkitsis.uhuruphotos.feedpage.SelectionList
import com.savvasdalkitsis.uhuruphotos.feedpage.mvflow.FeedPageAction
import com.savvasdalkitsis.uhuruphotos.feedpage.mvflow.FeedPageAction.*
import com.savvasdalkitsis.uhuruphotos.feedpage.mvflow.FeedPageAction.ChangeDisplay
import com.savvasdalkitsis.uhuruphotos.feedpage.mvflow.FeedPageAction.HideFeedDisplayChoice
import com.savvasdalkitsis.uhuruphotos.feedpage.mvflow.FeedPageAction.ShowFeedDisplayChoice
import com.savvasdalkitsis.uhuruphotos.feedpage.mvflow.FeedPageEffect
import com.savvasdalkitsis.uhuruphotos.feedpage.mvflow.FeedPageEffect.*
import com.savvasdalkitsis.uhuruphotos.feedpage.mvflow.FeedPageMutation
import com.savvasdalkitsis.uhuruphotos.feedpage.mvflow.FeedPageMutation.*
import com.savvasdalkitsis.uhuruphotos.feedpage.usecase.FeedPageUseCase
import com.savvasdalkitsis.uhuruphotos.feedpage.view.state.FeedPageState
import com.savvasdalkitsis.uhuruphotos.photos.model.Photo
import com.savvasdalkitsis.uhuruphotos.photos.model.SelectionMode
import com.savvasdalkitsis.uhuruphotos.photos.usecase.PhotosUseCase
import com.savvasdalkitsis.uhuruphotos.userbadge.api.UserBadgeUseCase
import com.savvasdalkitsis.uhuruphotos.viewmodel.Handler
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject

class FeedPageHandler @Inject constructor(
    private val albumsUseCase: AlbumsUseCase,
    private val userBadgeUseCase: UserBadgeUseCase,
    private val accountUseCase: AccountUseCase,
    private val feedPageUseCase: FeedPageUseCase,
    private val photosUseCase: PhotosUseCase,
    private val selectionList: SelectionList,
) : Handler<FeedPageState, FeedPageEffect, FeedPageAction, FeedPageMutation> {

    override fun invoke(
        state: FeedPageState,
        action: FeedPageAction,
        effect: suspend (FeedPageEffect) -> Unit,
    ): Flow<FeedPageMutation> = when (action) {
        is LoadFeed -> merge(
            feedPageUseCase
                .getFeedDisplay()
                .distinctUntilChanged()
                .map(FeedPageMutation::ChangeDisplay),
            flowOf(Loading),
            combine(
                albumsUseCase.getAlbums().debounce(200),
                selectionList.ids,
            ) { albums, ids ->
                albums.selectPhotos(ids)
            }.map(::ShowAlbums),
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
                action.photo.selectionMode == SelectionMode.SELECTED -> {
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
            emit(FeedPageMutation.HideFeedDisplayChoice)
        }
        HideFeedDisplayChoice -> flowOf(FeedPageMutation.HideFeedDisplayChoice)
        ShowFeedDisplayChoice -> flowOf(FeedPageMutation.ShowFeedDisplayChoice)
        is PhotoLongPressed -> flow {
            if (state.selectedPhotoCount == 0) {
                effect(Vibrate)
                action.photo.select()
            }
        }
        ClearSelected -> flow<FeedPageMutation> {
            effect(Vibrate)
            selectionList.clear()
        }
        AskForSelectedPhotosDeletion -> flowOf(ShowDeletionConfirmationDialog)
        is AlbumSelectionClicked -> flow<FeedPageMutation> {
            val photos = action.album.photos
            effect(Vibrate)
            if (photos.all { it.selectionMode == SelectionMode.SELECTED }) {
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

    private fun List<Album>.selectPhotos(ids: Set<String>): List<Album> = when {
        ids.isNotEmpty() -> map { album ->
            album.copy(photos = album.photos.map { photo ->
                photo.copy(selectionMode = when (photo.id) {
                    in ids -> SelectionMode.SELECTED
                    else -> SelectionMode.UNSELECTED
                })
            })
        }
        else -> this
    }
}
