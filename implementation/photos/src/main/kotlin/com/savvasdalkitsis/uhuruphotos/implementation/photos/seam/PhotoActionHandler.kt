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

import androidx.work.WorkInfo
import androidx.work.WorkInfo.State.*
import com.savvasdalkitsis.uhuruphotos.api.albums.model.Album
import com.savvasdalkitsis.uhuruphotos.api.albums.usecase.AlbumsUseCase
import com.savvasdalkitsis.uhuruphotos.api.date.DateDisplayer
import com.savvasdalkitsis.uhuruphotos.api.db.people.People
import com.savvasdalkitsis.uhuruphotos.api.db.photos.PhotoSummary
import com.savvasdalkitsis.uhuruphotos.api.people.usecase.PeopleUseCase
import com.savvasdalkitsis.uhuruphotos.api.people.view.state.toPerson
import com.savvasdalkitsis.uhuruphotos.api.person.usecase.PersonUseCase
import com.savvasdalkitsis.uhuruphotos.api.photos.model.Photo
import com.savvasdalkitsis.uhuruphotos.api.photos.model.PhotoSequenceDataSource.AllPhotos
import com.savvasdalkitsis.uhuruphotos.api.photos.model.PhotoSequenceDataSource.AutoAlbum
import com.savvasdalkitsis.uhuruphotos.api.photos.model.PhotoSequenceDataSource.FavouritePhotos
import com.savvasdalkitsis.uhuruphotos.api.photos.model.PhotoSequenceDataSource.HiddenPhotos
import com.savvasdalkitsis.uhuruphotos.api.photos.model.PhotoSequenceDataSource.PersonResults
import com.savvasdalkitsis.uhuruphotos.api.photos.model.PhotoSequenceDataSource.SearchResults
import com.savvasdalkitsis.uhuruphotos.api.photos.model.PhotoSequenceDataSource.Single
import com.savvasdalkitsis.uhuruphotos.api.photos.model.PhotoSequenceDataSource.Trash
import com.savvasdalkitsis.uhuruphotos.api.photos.model.PhotoSequenceDataSource.UserAlbum
import com.savvasdalkitsis.uhuruphotos.api.photos.model.deserializePeopleNames
import com.savvasdalkitsis.uhuruphotos.api.photos.usecase.PhotosUseCase
import com.savvasdalkitsis.uhuruphotos.api.seam.ActionHandler
import com.savvasdalkitsis.uhuruphotos.api.search.SearchUseCase
import com.savvasdalkitsis.uhuruphotos.api.strings.R
import com.savvasdalkitsis.uhuruphotos.api.user.usecase.UserUseCase
import com.savvasdalkitsis.uhuruphotos.implementation.photos.seam.PhotoAction.*
import com.savvasdalkitsis.uhuruphotos.implementation.photos.seam.PhotoEffect.CopyToClipboard
import com.savvasdalkitsis.uhuruphotos.implementation.photos.seam.PhotoEffect.DownloadingOriginal
import com.savvasdalkitsis.uhuruphotos.implementation.photos.seam.PhotoEffect.HideSystemBars
import com.savvasdalkitsis.uhuruphotos.implementation.photos.seam.PhotoEffect.LaunchMap
import com.savvasdalkitsis.uhuruphotos.implementation.photos.seam.PhotoEffect.NavigateToPerson
import com.savvasdalkitsis.uhuruphotos.implementation.photos.seam.PhotoEffect.ShowSystemBars
import com.savvasdalkitsis.uhuruphotos.implementation.photos.seam.PhotoMutation.ChangeCurrentIndex
import com.savvasdalkitsis.uhuruphotos.implementation.photos.seam.PhotoMutation.FinishedLoading
import com.savvasdalkitsis.uhuruphotos.implementation.photos.seam.PhotoMutation.HideAllConfirmationDialogs
import com.savvasdalkitsis.uhuruphotos.implementation.photos.seam.PhotoMutation.SetOriginalFileIconState
import com.savvasdalkitsis.uhuruphotos.implementation.photos.seam.PhotoMutation.HideUI
import com.savvasdalkitsis.uhuruphotos.implementation.photos.seam.PhotoMutation.Loading
import com.savvasdalkitsis.uhuruphotos.implementation.photos.seam.PhotoMutation.ReceivedDetails
import com.savvasdalkitsis.uhuruphotos.implementation.photos.seam.PhotoMutation.RemovePhotoFromSource
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
import com.savvasdalkitsis.uhuruphotos.implementation.photos.usecase.MetadataUseCase
import com.savvasdalkitsis.uhuruphotos.implementation.photos.view.state.OriginalFileIconState
import com.savvasdalkitsis.uhuruphotos.implementation.photos.view.state.OriginalFileIconState.*
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
    private val peopleUseCase: PeopleUseCase,
    private val personUseCase: PersonUseCase,
    private val userUseCase: UserUseCase,
    private val albumsUseCase: AlbumsUseCase,
    private val searchUseCase: SearchUseCase,
    private val dateDisplayer: DateDisplayer,
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
            if (action.datasource == Trash) {
                photoType = TRASHED
                emit(ShowRestoreButton)
            }
            with(photosUseCase) {
                emit(ShowSinglePhoto(
                    SinglePhotoState(
                        id = action.id,
                        lowResUrl = action.id.toThumbnailUrlFromId(),
                        fullResUrl = action.id.toFullSizeUrlFromId(action.isVideo),
                    )
                ))
                when (action.datasource) {
                    Single -> loadPhotoDetails(action.id)
                    AllPhotos -> loadAlbums(albumsUseCase.getAlbums(), action)
                    is SearchResults -> loadAlbums(
                        searchUseCase.searchResultsFor(action.datasource.query),
                        action,
                    )
                    is PersonResults -> loadAlbums(
                        personUseCase.getPersonAlbums(action.datasource.personId),
                        action,
                    )
                    is AutoAlbum -> loadAlbums(
                        albumsUseCase.getAutoAlbum(action.datasource.albumId),
                        action,
                    )
                    is UserAlbum -> loadAlbums(
                        albumsUseCase.getUserAlbum(action.datasource.albumId),
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
                    merge<PhotoMutation>(
                        flow { loadPhotoDetails(photo.id) },
                        photosUseCase.observeOriginalFileDownloadStatus(photo.id)
                            .map { when(it) {
                                ENQUEUED, RUNNING, BLOCKED -> IN_PROGRESS
                                SUCCEEDED -> IDLE
                                FAILED, CANCELLED -> ERROR
                            } }
                            .onStart { emit(IDLE) }
                            .map { SetOriginalFileIconState(photo.id, it) }
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
                    emit(ShowErrorMessage(R.string.error_changing_photo_favourite))
                }
                .onSuccess {
                    emit(ShowPhotoFavourite(state.currentPhoto.id, action.favourite))
                }
        }
        Refresh -> flow {
            loadPhotoDetails(state.currentPhoto.id, refresh = true)
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
        is FullImageLoaded -> flow {
            emit(SetOriginalFileIconState(action.photo.id, HIDDEN))
            if (!action.photo.isVideo) {
                emit(ShowShareIcon(action.photo.id))
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
        val photoStates = photos.map {  photo ->
            SinglePhotoState(
                id = photo.id,
                lowResUrl = photo.id.toThumbnailUrlFromId(),
                fullResUrl = photo.id.toFullSizeUrlFromId(action.isVideo),
                isFavourite = photo.isFavourite,
                isVideo = photo.isVideo,
            )
        }
        val index = photoStates.indexOfFirst { it.id == action.id }
        emit(ShowMultiplePhotos(photoStates, index))
        loadPhotoDetails(action.id)
    }

    private suspend fun FlowCollector<PhotoMutation>.loadPhotoDetails(
        photoId: String,
        refresh: Boolean = false,
    ) {
        emit(Loading)
        if (refresh) {
            photosUseCase.refreshDetailsNow(photoId)
        } else {
            photosUseCase.refreshDetailsNowIfMissing(photoId)
        }.onFailure {
            emit(ShowErrorMessage(R.string.error_loading_photo_details))
        }
        when (val details = photosUseCase.getPhotoDetails(photoId)) {
            null -> emit(ShowErrorMessage(R.string.error_loading_photo_details))
            else -> {
                val people = maybeRefreshPeople()
                val names = details.peopleNames.orEmpty().deserializePeopleNames
                val peopleInPhoto = when {
                    names.isEmpty() -> emptyList()
                    else -> people.filter { it.name in names }
                }.map {
                    it.toPerson {
                        with(photosUseCase) {
                            it.toAbsoluteUrl()
                        }
                    }
                }

                val favouriteThreshold = userUseCase.getUserOrRefresh()
                emit(ReceivedDetails(
                    details = details,
                    peopleInPhoto = peopleInPhoto,
                    favouriteThreshold = favouriteThreshold.getOrNull()?.favoriteMinRating,
                    formattedDateAndTime = dateDisplayer.dateTimeString(details.timestamp)
                ))
            }
        }
        emit(FinishedLoading)
    }

    private suspend fun maybeRefreshPeople(): List<People> =
        peopleUseCase.getPeopleByName().ifEmpty {
            peopleUseCase.refreshPeople()
            peopleUseCase.getPeopleByName()
        }
}
