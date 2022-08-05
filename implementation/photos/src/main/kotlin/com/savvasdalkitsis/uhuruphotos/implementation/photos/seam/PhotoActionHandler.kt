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
package com.savvasdalkitsis.uhuruphotos.implementation.photos.seam

import androidx.work.WorkInfo.State.BLOCKED
import androidx.work.WorkInfo.State.CANCELLED
import androidx.work.WorkInfo.State.ENQUEUED
import androidx.work.WorkInfo.State.FAILED
import androidx.work.WorkInfo.State.RUNNING
import androidx.work.WorkInfo.State.SUCCEEDED
import com.savvasdalkitsis.uhuruphotos.api.albums.model.Album
import com.savvasdalkitsis.uhuruphotos.api.albums.usecase.AlbumsUseCase
import com.savvasdalkitsis.uhuruphotos.api.db.photos.PhotoSummary
import com.savvasdalkitsis.uhuruphotos.api.localalbum.usecase.LocalAlbumUseCase
import com.savvasdalkitsis.uhuruphotos.api.log.log
import com.savvasdalkitsis.uhuruphotos.api.person.usecase.PersonUseCase
import com.savvasdalkitsis.uhuruphotos.api.photos.model.Photo
import com.savvasdalkitsis.uhuruphotos.api.photos.model.PhotoImageSource
import com.savvasdalkitsis.uhuruphotos.api.photos.model.PhotoImageSource.LOCAL
import com.savvasdalkitsis.uhuruphotos.api.photos.model.PhotoImageSource.REMOTE
import com.savvasdalkitsis.uhuruphotos.api.photos.model.PhotoSequenceDataSource.AllPhotos
import com.savvasdalkitsis.uhuruphotos.api.photos.model.PhotoSequenceDataSource.AutoAlbum
import com.savvasdalkitsis.uhuruphotos.api.photos.model.PhotoSequenceDataSource.FavouritePhotos
import com.savvasdalkitsis.uhuruphotos.api.photos.model.PhotoSequenceDataSource.HiddenPhotos
import com.savvasdalkitsis.uhuruphotos.api.photos.model.PhotoSequenceDataSource.LocalAlbum
import com.savvasdalkitsis.uhuruphotos.api.photos.model.PhotoSequenceDataSource.PersonResults
import com.savvasdalkitsis.uhuruphotos.api.photos.model.PhotoSequenceDataSource.SearchResults
import com.savvasdalkitsis.uhuruphotos.api.photos.model.PhotoSequenceDataSource.Single
import com.savvasdalkitsis.uhuruphotos.api.photos.model.PhotoSequenceDataSource.Trash
import com.savvasdalkitsis.uhuruphotos.api.photos.model.PhotoSequenceDataSource.UserAlbum
import com.savvasdalkitsis.uhuruphotos.api.photos.usecase.PhotosUseCase
import com.savvasdalkitsis.uhuruphotos.api.seam.ActionHandler
import com.savvasdalkitsis.uhuruphotos.api.search.SearchUseCase
import com.savvasdalkitsis.uhuruphotos.api.strings.R.string
import com.savvasdalkitsis.uhuruphotos.implementation.photos.seam.PhotoAction.AskForPhotoRestoration
import com.savvasdalkitsis.uhuruphotos.implementation.photos.seam.PhotoAction.AskForPhotoTrashing
import com.savvasdalkitsis.uhuruphotos.implementation.photos.seam.PhotoAction.ChangedToPage
import com.savvasdalkitsis.uhuruphotos.implementation.photos.seam.PhotoAction.ClickedOnDetailsEntry
import com.savvasdalkitsis.uhuruphotos.implementation.photos.seam.PhotoAction.ClickedOnGps
import com.savvasdalkitsis.uhuruphotos.implementation.photos.seam.PhotoAction.ClickedOnMap
import com.savvasdalkitsis.uhuruphotos.implementation.photos.seam.PhotoAction.DismissConfirmationDialogs
import com.savvasdalkitsis.uhuruphotos.implementation.photos.seam.PhotoAction.DismissErrorMessage
import com.savvasdalkitsis.uhuruphotos.implementation.photos.seam.PhotoAction.DownloadOriginal
import com.savvasdalkitsis.uhuruphotos.implementation.photos.seam.PhotoAction.FullImageLoaded
import com.savvasdalkitsis.uhuruphotos.implementation.photos.seam.PhotoAction.HideInfo
import com.savvasdalkitsis.uhuruphotos.implementation.photos.seam.PhotoAction.LoadPhoto
import com.savvasdalkitsis.uhuruphotos.implementation.photos.seam.PhotoAction.NavigateBack
import com.savvasdalkitsis.uhuruphotos.implementation.photos.seam.PhotoAction.PersonSelected
import com.savvasdalkitsis.uhuruphotos.implementation.photos.seam.PhotoAction.Refresh
import com.savvasdalkitsis.uhuruphotos.implementation.photos.seam.PhotoAction.RestorePhoto
import com.savvasdalkitsis.uhuruphotos.implementation.photos.seam.PhotoAction.SetFavourite
import com.savvasdalkitsis.uhuruphotos.implementation.photos.seam.PhotoAction.SharePhoto
import com.savvasdalkitsis.uhuruphotos.implementation.photos.seam.PhotoAction.ShowInfo
import com.savvasdalkitsis.uhuruphotos.implementation.photos.seam.PhotoAction.ToggleUI
import com.savvasdalkitsis.uhuruphotos.implementation.photos.seam.PhotoAction.TrashPhoto
import com.savvasdalkitsis.uhuruphotos.implementation.photos.seam.PhotoAction.UsePhotoAs
import com.savvasdalkitsis.uhuruphotos.implementation.photos.seam.PhotoEffect.CopyToClipboard
import com.savvasdalkitsis.uhuruphotos.implementation.photos.seam.PhotoEffect.DownloadingOriginal
import com.savvasdalkitsis.uhuruphotos.implementation.photos.seam.PhotoEffect.HideSystemBars
import com.savvasdalkitsis.uhuruphotos.implementation.photos.seam.PhotoEffect.LaunchMap
import com.savvasdalkitsis.uhuruphotos.implementation.photos.seam.PhotoEffect.NavigateToPerson
import com.savvasdalkitsis.uhuruphotos.implementation.photos.seam.PhotoEffect.ShowSystemBars
import com.savvasdalkitsis.uhuruphotos.implementation.photos.seam.PhotoMutation.ChangeCurrentIndex
import com.savvasdalkitsis.uhuruphotos.implementation.photos.seam.PhotoMutation.FinishedLoading
import com.savvasdalkitsis.uhuruphotos.implementation.photos.seam.PhotoMutation.FinishedLoadingDetails
import com.savvasdalkitsis.uhuruphotos.implementation.photos.seam.PhotoMutation.HideAllConfirmationDialogs
import com.savvasdalkitsis.uhuruphotos.implementation.photos.seam.PhotoMutation.HideUI
import com.savvasdalkitsis.uhuruphotos.implementation.photos.seam.PhotoMutation.Loading
import com.savvasdalkitsis.uhuruphotos.implementation.photos.seam.PhotoMutation.LoadingDetails
import com.savvasdalkitsis.uhuruphotos.implementation.photos.seam.PhotoMutation.ReceivedDetails
import com.savvasdalkitsis.uhuruphotos.implementation.photos.seam.PhotoMutation.RemovePhotoFromSource
import com.savvasdalkitsis.uhuruphotos.implementation.photos.seam.PhotoMutation.SetOriginalFileIconState
import com.savvasdalkitsis.uhuruphotos.implementation.photos.seam.PhotoMutation.ShowDeleteConfirmationDialog
import com.savvasdalkitsis.uhuruphotos.implementation.photos.seam.PhotoMutation.ShowErrorMessage
import com.savvasdalkitsis.uhuruphotos.implementation.photos.seam.PhotoMutation.ShowMetadata
import com.savvasdalkitsis.uhuruphotos.implementation.photos.seam.PhotoMutation.ShowMultiplePhotos
import com.savvasdalkitsis.uhuruphotos.implementation.photos.seam.PhotoMutation.ShowPhotoFavourite
import com.savvasdalkitsis.uhuruphotos.implementation.photos.seam.PhotoMutation.ShowRestorationConfirmationDialog
import com.savvasdalkitsis.uhuruphotos.implementation.photos.seam.PhotoMutation.ShowRestoreButton
import com.savvasdalkitsis.uhuruphotos.implementation.photos.seam.PhotoMutation.ShowShareIcon
import com.savvasdalkitsis.uhuruphotos.implementation.photos.seam.PhotoMutation.ShowSinglePhoto
import com.savvasdalkitsis.uhuruphotos.implementation.photos.seam.PhotoMutation.ShowTrashingConfirmationDialog
import com.savvasdalkitsis.uhuruphotos.implementation.photos.seam.PhotoMutation.ShowUI
import com.savvasdalkitsis.uhuruphotos.implementation.photos.seam.PhotoMutation.ShowUseAsIcon
import com.savvasdalkitsis.uhuruphotos.implementation.photos.usecase.MetadataUseCase
import com.savvasdalkitsis.uhuruphotos.implementation.photos.view.state.OriginalFileIconState.ERROR
import com.savvasdalkitsis.uhuruphotos.implementation.photos.view.state.OriginalFileIconState.HIDDEN
import com.savvasdalkitsis.uhuruphotos.implementation.photos.view.state.OriginalFileIconState.IDLE
import com.savvasdalkitsis.uhuruphotos.implementation.photos.view.state.OriginalFileIconState.IN_PROGRESS
import com.savvasdalkitsis.uhuruphotos.implementation.photos.view.state.PhotoState
import com.savvasdalkitsis.uhuruphotos.implementation.photos.view.state.PhotoType
import com.savvasdalkitsis.uhuruphotos.implementation.photos.view.state.PhotoType.TRASHED
import com.savvasdalkitsis.uhuruphotos.implementation.photos.view.state.SinglePhotoState
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

class PhotoActionHandler @Inject constructor(
    private val photosUseCase: PhotosUseCase,
    private val personUseCase: PersonUseCase,
    private val albumsUseCase: AlbumsUseCase,
    private val searchUseCase: SearchUseCase,
    private val localAlbumUseCase: LocalAlbumUseCase,
    private val metadataUseCase: MetadataUseCase,
) : ActionHandler<PhotoState, PhotoEffect, PhotoAction, PhotoMutation> {

    private var photoType = PhotoType.default
    private var changePageJob: Job? = null

    override fun handleAction(
        state: PhotoState,
        action: PhotoAction,
        effect: suspend (PhotoEffect) -> Unit
    ): Flow<PhotoMutation> = when(action) {
        is LoadPhoto -> flow {
            if (action.sequenceDataSource == Trash) {
                photoType = TRASHED
                emit(ShowRestoreButton)
            }
            with(photosUseCase) {
                emit(ShowSinglePhoto(
                    SinglePhotoState(
                        id = action.id,
                        lowResUrl = action.id.toThumbnailUrlFromId(action.isVideo, action.imageSource),
                        fullResUrl = action.id.toFullSizeUrlFromId(action.isVideo, action.imageSource),
                        showFavouriteIcon = action.imageSource == REMOTE,
                        showDeleteButton =  action.imageSource == REMOTE,
                    )
                ))
                when (action.sequenceDataSource) {
                    Single -> loadPhotoDetails(
                        photoId = action.id,
                        isVideo = action.isVideo,
                        imageSource = action.imageSource
                    )
                    AllPhotos -> loadAlbums(albumsUseCase.getAlbums(), action)
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
                    FavouritePhotos -> loadSummaries(
                        photosUseCase.getFavouritePhotoSummaries(),
                        action,
                    )
                    HiddenPhotos -> loadSummaries(
                        Result.success(photosUseCase.getHiddenPhotoSummaries()),
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
            if (state.photos.isNotEmpty()) {
                val photo = state.photos[action.page]
                changePageJob = launch {
                    val imageSource = PhotoImageSource.fromUrl(photo.fullResUrl)
                    merge<PhotoMutation>(
                        flow {
                            loadPhotoDetails(photo.id, photo.isVideo, imageSource)
                        },
                        when (imageSource) {
                            REMOTE -> photosUseCase.observeOriginalFileDownloadStatus(photo.id)
                                .map {
                                    when (it) {
                                        ENQUEUED, RUNNING, BLOCKED -> IN_PROGRESS
                                        SUCCEEDED -> IDLE
                                        FAILED, CANCELLED -> ERROR
                                    }
                                }
                                .onStart { emit(IDLE) }
                                .map { SetOriginalFileIconState(photo.id, it) }
                            LOCAL -> flowOf(SetOriginalFileIconState(photo.id, HIDDEN))
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
            effect(PhotoEffect.NavigateBack)
        }
        is SetFavourite -> flow {
            photosUseCase.setPhotoFavourite(state.currentPhoto.id, action.favourite)
                .onFailure {
                    emit(ShowErrorMessage(string.error_changing_photo_favourite))
                }
                .onSuccess {
                    emit(ShowPhotoFavourite(state.currentPhoto.id, action.favourite))
                }
        }
        Refresh -> flow {
            val photo = state.currentPhoto
            loadPhotoDetails(
                photoId = photo.id,
                refresh = true,
                isVideo = photo.isVideo,
                imageSource = PhotoImageSource.fromUrl(photo.fullResUrl),
            )
        }
        DismissErrorMessage -> flowOf(PhotoMutation.DismissErrorMessage)
        ShowInfo -> flowOf(PhotoMutation.ShowInfo)
        HideInfo -> flowOf(PhotoMutation.HideInfo)
        is ClickedOnMap -> flow {
            effect(LaunchMap(action.gps))
        }
        is ClickedOnGps -> flow {
            effect(CopyToClipboard(action.gps.toString()))
        }
        AskForPhotoTrashing -> flowOf(when (photoType) {
            TRASHED -> ShowDeleteConfirmationDialog
            else -> ShowTrashingConfirmationDialog
        })
        AskForPhotoRestoration -> flowOf(ShowRestorationConfirmationDialog)
        DismissConfirmationDialogs -> flowOf(HideAllConfirmationDialogs)
        TrashPhoto -> processAndRemovePhoto(state, effect) {
            when (photoType) {
                TRASHED -> photosUseCase.deletePhoto(state.currentPhoto.id)
                else -> photosUseCase.trashPhoto(state.currentPhoto.id)
            }
        }
        RestorePhoto -> processAndRemovePhoto(state, effect) {
            photosUseCase.restorePhoto(state.currentPhoto.id)
        }
        SharePhoto -> flow {
            effect(PhotoEffect.SharePhoto(state.currentPhoto.fullResUrl))
        }
        UsePhotoAs -> flow {
            effect(PhotoEffect.UsePhotoAs(state.currentPhoto.fullResUrl))
        }
        is FullImageLoaded -> flow {
            emit(SetOriginalFileIconState(action.photo.id, HIDDEN))
            emit(ShowShareIcon(action.photo.id))
            emit(ShowUseAsIcon(action.photo.id))
            if (!(PhotoImageSource.fromUrl(action.photo.fullResUrl) == REMOTE
                && action.photo.isVideo)
            ) {
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
            photosUseCase.downloadOriginal(action.photo.id, action.photo.isVideo)
            effect(DownloadingOriginal)
        }
        is ClickedOnDetailsEntry -> flow {
            effect(CopyToClipboard(action.text))
        }
    }

    private fun processAndRemovePhoto(
        state: PhotoState,
        effect: suspend (PhotoEffect) -> Unit,
        process: suspend () -> Unit,
    ) = flow {
        emit(Loading)
        emit(HideAllConfirmationDialogs)
        process()
        emit(FinishedLoading)
        emit(RemovePhotoFromSource(state.currentPhoto.id))
        if (state.photos.size == 1) {
            effect(PhotoEffect.NavigateBack)
        }
    }

    context (PhotosUseCase)
    private suspend fun FlowCollector<PhotoMutation>.loadSummaries(
        photoSummaries: Result<List<PhotoSummary>>,
        action: LoadPhoto,
    ) =
        when (val favourites =
            photoSummaries
                .mapCatching { it.mapToPhotos().getOrThrow() }
                .getOrNull()
        ) {
            null -> loadPhotoDetails(action.id)
            else -> loadPhotos(favourites, action)
        }

    context(PhotosUseCase)
    private suspend fun FlowCollector<PhotoMutation>.loadAlbums(
        albums: List<Album>,
        action: LoadPhoto
    ) = loadPhotos(albums.flatMap { it.photos }, action)

    context(PhotosUseCase)
    private suspend fun FlowCollector<PhotoMutation>.loadPhotos(
        photos: List<Photo>,
        action: LoadPhoto
    ) {
        val photoStates = photos.map { photo ->
            val imageSource = PhotoImageSource.fromUrl(photo.fullResUrl)
            SinglePhotoState(
                id = photo.id,
                lowResUrl = photo.thumbnailUrl ?: photo.id.toThumbnailUrlFromId(action.isVideo),
                fullResUrl = photo.fullResUrl ?: photo.id.toFullSizeUrlFromId(action.isVideo),
                isFavourite = photo.isFavourite,
                isVideo = photo.isVideo,
                showFavouriteIcon = imageSource == REMOTE,
                showDeleteButton = imageSource == REMOTE,
            )
        }
        val index = photoStates.indexOfFirst { it.id == action.id }
        emit(ShowMultiplePhotos(photoStates, index))
        loadPhotoDetails(
            photoId = action.id,
            isVideo = action.isVideo,
            imageSource = action.imageSource
        )
    }

    private suspend fun FlowCollector<PhotoMutation>.loadPhotoDetails(
        photoId: String,
        isVideo: Boolean = false,
        imageSource: PhotoImageSource = REMOTE,
        refresh: Boolean = false,
    ) {
        emit(Loading)
        emit(LoadingDetails(photoId))
        if (refresh) {
            photosUseCase.refreshDetailsNow(photoId, isVideo, imageSource)
        } else {
            photosUseCase.refreshDetailsNowIfMissing(photoId, isVideo, imageSource)
        }.onFailure {
            log(it)
            emit(ShowErrorMessage(string.error_loading_photo_details))
        }
        when (val details = photosUseCase.getPhotoDetails(photoId, isVideo, imageSource)) {
            null -> emit(ShowErrorMessage(string.error_loading_photo_details))
            else -> emit(ReceivedDetails(photoId, details))
        }
        emit(FinishedLoading)
        emit(FinishedLoadingDetails(photoId))
    }
}
