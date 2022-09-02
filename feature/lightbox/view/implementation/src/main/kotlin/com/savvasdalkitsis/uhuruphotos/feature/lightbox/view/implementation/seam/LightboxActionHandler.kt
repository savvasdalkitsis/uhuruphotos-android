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
package com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam

import androidx.work.WorkInfo.State.BLOCKED
import androidx.work.WorkInfo.State.CANCELLED
import androidx.work.WorkInfo.State.ENQUEUED
import androidx.work.WorkInfo.State.FAILED
import androidx.work.WorkInfo.State.RUNNING
import androidx.work.WorkInfo.State.SUCCEEDED
import com.savvasdalkitsis.uhuruphotos.api.albums.model.Album
import com.savvasdalkitsis.uhuruphotos.api.albums.usecase.AlbumsUseCase
import com.savvasdalkitsis.uhuruphotos.feature.feed.domain.api.usecase.FeedUseCase
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.api.model.LightboxSequenceDataSource.AutoAlbum
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.api.model.LightboxSequenceDataSource.FavouriteMedia
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.api.model.LightboxSequenceDataSource.Feed
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.api.model.LightboxSequenceDataSource.HiddenMedia
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.api.model.LightboxSequenceDataSource.LocalAlbum
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.api.model.LightboxSequenceDataSource.PersonResults
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.api.model.LightboxSequenceDataSource.SearchResults
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.api.model.LightboxSequenceDataSource.Single
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.api.model.LightboxSequenceDataSource.Trash
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.api.model.LightboxSequenceDataSource.UserAlbum
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.LightboxAction.AskForMediaItemRestoration
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.LightboxAction.AskForMediaItemTrashing
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.LightboxAction.ChangedToPage
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.LightboxAction.ClickedOnDetailsEntry
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.LightboxAction.ClickedOnGps
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.LightboxAction.ClickedOnMap
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.LightboxAction.DismissConfirmationDialogs
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.LightboxAction.DismissErrorMessage
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.LightboxAction.DownloadOriginal
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.LightboxAction.FullMediaDataLoaded
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.LightboxAction.HideInfo
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.LightboxAction.LoadMediaItem
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.LightboxAction.NavigateBack
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.LightboxAction.PersonSelected
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.LightboxAction.Refresh
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.LightboxAction.RestoreMediaItem
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.LightboxAction.SetFavourite
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.LightboxAction.ShareMediaItem
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.LightboxAction.ShowInfo
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.LightboxAction.ToggleUI
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.LightboxAction.TrashMediaItem
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.LightboxAction.UseMediaItemAs
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.LightboxEffect.CopyToClipboard
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.LightboxEffect.DownloadingOriginal
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.LightboxEffect.HideSystemBars
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.LightboxEffect.LaunchMap
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.LightboxEffect.NavigateToPerson
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.LightboxEffect.ShowSystemBars
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.LightboxMutation.ChangeCurrentIndex
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.LightboxMutation.FinishedLoading
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.LightboxMutation.FinishedLoadingDetails
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.LightboxMutation.HideAllConfirmationDialogs
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.LightboxMutation.HideUI
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.LightboxMutation.Loading
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.LightboxMutation.LoadingDetails
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.LightboxMutation.ReceivedDetails
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.LightboxMutation.RemoveMediaItemFromSource
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.LightboxMutation.SetOriginalFileIconState
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.LightboxMutation.ShowDeleteConfirmationDialog
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.LightboxMutation.ShowErrorMessage
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.LightboxMutation.ShowMediaItemFavourite
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.LightboxMutation.ShowMetadata
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.LightboxMutation.ShowMultipleMedia
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.LightboxMutation.ShowRestorationConfirmationDialog
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.LightboxMutation.ShowRestoreButton
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.LightboxMutation.ShowShareIcon
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.LightboxMutation.ShowSingleMediaItem
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.LightboxMutation.ShowTrashingConfirmationDialog
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.LightboxMutation.ShowUI
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.LightboxMutation.ShowUseAsIcon
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.ui.state.LightboxState
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.ui.state.MediaItemType
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.ui.state.MediaItemType.TRASHED
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.ui.state.OriginalFileIconState.ERROR
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.ui.state.OriginalFileIconState.HIDDEN
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.ui.state.OriginalFileIconState.IDLE
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.ui.state.OriginalFileIconState.IN_PROGRESS
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.ui.state.SingleMediaItemState
import com.savvasdalkitsis.uhuruphotos.feature.local.domain.api.usecase.LocalAlbumUseCase
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaCollection
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaId
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItem
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.usecase.MediaUseCase
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.usecase.MetadataUseCase
import com.savvasdalkitsis.uhuruphotos.feature.person.domain.api.usecase.PersonUseCase
import com.savvasdalkitsis.uhuruphotos.feature.search.domain.api.usecase.SearchUseCase
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.ActionHandler
import com.savvasdalkitsis.uhuruphotos.foundation.strings.api.R.string
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

class LightboxActionHandler @Inject constructor(
    private val mediaUseCase: MediaUseCase,
    private val personUseCase: PersonUseCase,
    private val albumsUseCase: AlbumsUseCase,
    private val feedUseCase: FeedUseCase,
    private val searchUseCase: SearchUseCase,
    private val localAlbumUseCase: LocalAlbumUseCase,
    private val metadataUseCase: MetadataUseCase,
) : ActionHandler<LightboxState, LightboxEffect, LightboxAction, LightboxMutation> {

    private var mediaItemType = MediaItemType.default
    private var changePageJob: Job? = null

    override fun handleAction(
        state: LightboxState,
        action: LightboxAction,
        effect: suspend (LightboxEffect) -> Unit
    ): Flow<LightboxMutation> = when(action) {
        is LoadMediaItem -> flow {
            if (action.sequenceDataSource == Trash) {
                mediaItemType = TRASHED
                emit(ShowRestoreButton)
            }
            with(mediaUseCase) {
                emit(ShowSingleMediaItem(
                    SingleMediaItemState(
                        id = action.id,
                        lowResUrl = action.id.toThumbnailUriFromId(action.isVideo),
                        fullResUrl = action.id.toFullSizeUriFromId(action.isVideo),
                        showFavouriteIcon = action.id is MediaId.Remote,
                        showDeleteButton =  action.id is MediaId.Remote,
                    )
                ))
                when (action.sequenceDataSource) {
                    Single -> loadPhotoDetails(
                        photoId = action.id,
                        isVideo = action.isVideo
                    )
                    Feed -> loadCollections(feedUseCase.getFeed(), action)
                    is SearchResults -> loadAlbums(
                        searchUseCase.searchResultsFor(action.sequenceDataSource.query),
                        action,
                    )
                    is PersonResults -> loadAlbums(
                        personUseCase.getPersonAlbums(action.sequenceDataSource.personId),
                        action,
                    )
                    is AutoAlbum -> loadAlbums(
                        albumsUseCase.getAutoAlbum(action.sequenceDataSource.albumId),
                        action,
                    )
                    is UserAlbum -> loadAlbums(
                        albumsUseCase.getUserAlbum(action.sequenceDataSource.albumId),
                        action,
                    )
                    is LocalAlbum -> loadAlbums(
                        localAlbumUseCase.getLocalAlbum(action.sequenceDataSource.albumId),
                        action,
                    )
                    FavouriteMedia -> loadResult(
                        mediaUseCase.getFavouriteMedia(),
                        action,
                    )
                    HiddenMedia -> loadResult(
                        mediaUseCase.getHiddenMedia(),
                        action,
                    )
                    Trash -> loadAlbums(
                        albumsUseCase.getTrash(),
                        action,
                    )
                }
            }
        }
        is ChangedToPage -> channelFlow {
            changePageJob?.cancel()
            changePageJob = null
            send(ChangeCurrentIndex(action.page))
            if (state.media.isNotEmpty()) {
                val photo = state.media[action.page]
                changePageJob = launch {
                    merge<LightboxMutation>(
                        flow {
                            loadPhotoDetails(photo.id, photo.isVideo)
                        },
                        when (photo.id) {
                            is MediaId.Remote -> mediaUseCase.observeOriginalFileDownloadStatus(photo.id)
                                .map {
                                    when (it) {
                                        ENQUEUED, RUNNING, BLOCKED -> IN_PROGRESS
                                        SUCCEEDED -> IDLE
                                        FAILED, CANCELLED -> ERROR
                                    }
                                }
                                .onStart { emit(IDLE) }
                                .map { SetOriginalFileIconState(photo.id, it) }
                            is MediaId.Local -> flowOf(SetOriginalFileIconState(photo.id, HIDDEN))
                        }
                    ).collect { send(it) }
                }
            }
        }
        ToggleUI -> flow {
            if (state.showUI) {
                emit(HideUI)
                effect(HideSystemBars)
            } else {
                emit(ShowUI)
                effect(ShowSystemBars)
            }
        }
        NavigateBack -> flow {
            emit(HideUI)
            effect(LightboxEffect.NavigateBack)
        }
        is SetFavourite -> flow {
            mediaUseCase.setMediaItemFavourite(state.currentMediaItem.id, action.favourite)
                .onFailure {
                    emit(ShowErrorMessage(string.error_changing_photo_favourite))
                }
                .onSuccess {
                    emit(ShowMediaItemFavourite(state.currentMediaItem.id, action.favourite))
                }
        }
        Refresh -> flow {
            val photo = state.currentMediaItem
            loadPhotoDetails(
                photoId = photo.id,
                isVideo = photo.isVideo,
                refresh = true,
            )
        }
        DismissErrorMessage -> flowOf(LightboxMutation.DismissErrorMessage)
        ShowInfo -> flowOf(LightboxMutation.ShowInfo)
        HideInfo -> flowOf(LightboxMutation.HideInfo)
        is ClickedOnMap -> flow {
            effect(LaunchMap(action.gps))
        }
        is ClickedOnGps -> flow {
            effect(CopyToClipboard(action.gps.toString()))
        }
        AskForMediaItemTrashing -> flowOf(when (mediaItemType) {
            TRASHED -> ShowDeleteConfirmationDialog
            else -> ShowTrashingConfirmationDialog
        })
        AskForMediaItemRestoration -> flowOf(ShowRestorationConfirmationDialog)
        DismissConfirmationDialogs -> flowOf(HideAllConfirmationDialogs)
        TrashMediaItem -> processAndRemovePhoto(state, effect) {
            when (mediaItemType) {
                TRASHED -> mediaUseCase.deleteMediaItem(state.currentMediaItem.id)
                else -> mediaUseCase.trashMediaItem(state.currentMediaItem.id)
            }
        }
        RestoreMediaItem -> processAndRemovePhoto(state, effect) {
            mediaUseCase.restoreMediaItem(state.currentMediaItem.id)
        }
        ShareMediaItem -> flow {
            effect(LightboxEffect.ShareMedia(state.currentMediaItem.fullResUrl))
        }
        UseMediaItemAs -> flow {
            effect(LightboxEffect.UseMediaAs(state.currentMediaItem.fullResUrl))
        }
        is FullMediaDataLoaded -> flow {
            emit(SetOriginalFileIconState(action.photo.id, HIDDEN))
            if (!(action.photo.id is MediaId.Remote && action.photo.isVideo)) {
                emit(ShowShareIcon(action.photo.id))
                emit(ShowUseAsIcon(action.photo.id))
                val metadata = metadataUseCase.extractMetadata(action.photo.fullResUrl)
                if (metadata != null) {
                    emit(ShowMetadata(action.photo.id, metadata))
                }
            }
        }
        is PersonSelected -> flow {
            effect(NavigateToPerson(action.person.id))
        }
        is DownloadOriginal -> flow {
            mediaUseCase.downloadOriginal(action.photo.id, action.photo.isVideo)
            effect(DownloadingOriginal)
        }
        is ClickedOnDetailsEntry -> flow {
            effect(CopyToClipboard(action.text))
        }
    }

    private fun processAndRemovePhoto(
        state: LightboxState,
        effect: suspend (LightboxEffect) -> Unit,
        process: suspend () -> Unit,
    ) = flow {
        emit(Loading)
        emit(HideAllConfirmationDialogs)
        process()
        emit(FinishedLoading)
        emit(RemoveMediaItemFromSource(state.currentMediaItem.id))
        if (state.media.size == 1) {
            effect(LightboxEffect.NavigateBack)
        }
    }

    context (MediaUseCase)
    private suspend fun FlowCollector<LightboxMutation>.loadResult(
        mediaItem: Result<List<MediaItem>>,
        action: LoadMediaItem,
    ) =
        when (val items = mediaItem.getOrNull()) {
            null -> loadPhotoDetails(action.id)
            else -> loadPhotos(items, action)
        }

    context(MediaUseCase)
    private suspend fun FlowCollector<LightboxMutation>.loadAlbums(
        albums: List<Album>,
        action: LoadMediaItem
    ) = loadPhotos(albums.flatMap { it.photos }, action)

    context(MediaUseCase)
    private suspend fun FlowCollector<LightboxMutation>.loadCollections(
        collections: List<MediaCollection>,
        action: LoadMediaItem
    ) = loadPhotos(collections.flatMap { it.mediaItems }, action)

    context(MediaUseCase)
    private suspend fun FlowCollector<LightboxMutation>.loadPhotos(
        mediaItems: List<MediaItem>,
        action: LoadMediaItem
    ) {
        val photoStates = mediaItems.map { photo ->
            SingleMediaItemState(
                id = photo.id,
                lowResUrl = photo.thumbnailUri ?: photo.id.toThumbnailUriFromId(action.isVideo),
                fullResUrl = photo.fullResUri ?: photo.id.toFullSizeUriFromId(action.isVideo),
                isFavourite = photo.isFavourite,
                isVideo = photo.isVideo,
                showFavouriteIcon = photo.id is MediaId.Remote,
                showDeleteButton = photo.id is MediaId.Remote,
            )
        }
        val index = photoStates.indexOfFirst { it.id == action.id }
        emit(ShowMultipleMedia(photoStates, index))
        loadPhotoDetails(
            photoId = action.id,
            isVideo = action.isVideo
        )
    }

    private suspend fun FlowCollector<LightboxMutation>.loadPhotoDetails(
        photoId: MediaId<*>,
        isVideo: Boolean = false,
        refresh: Boolean = false,
    ) {
        emit(Loading)
        emit(LoadingDetails(photoId))
        if (refresh) {
            mediaUseCase.refreshDetailsNow(photoId, isVideo)
        } else {
            mediaUseCase.refreshDetailsNowIfMissing(photoId, isVideo)
        }.onFailure {
            emit(ShowErrorMessage(string.error_loading_photo_details))
        }
        when (val details = mediaUseCase.getMediaItemDetails(photoId)) {
            null -> emit(ShowErrorMessage(string.error_loading_photo_details))
            else -> emit(ReceivedDetails(photoId, details))
        }
        emit(FinishedLoading)
        emit(FinishedLoadingDetails(photoId))
    }
}
