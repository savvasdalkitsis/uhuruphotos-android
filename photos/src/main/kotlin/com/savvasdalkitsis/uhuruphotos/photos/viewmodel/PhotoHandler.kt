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
package com.savvasdalkitsis.uhuruphotos.photos.viewmodel

import com.savvasdalkitsis.uhuruphotos.albums.api.model.Album
import com.savvasdalkitsis.uhuruphotos.albums.api.usecase.AlbumsUseCase
import com.savvasdalkitsis.uhuruphotos.db.people.People
import com.savvasdalkitsis.uhuruphotos.people.api.usecase.PeopleUseCase
import com.savvasdalkitsis.uhuruphotos.people.api.view.state.toPerson
import com.savvasdalkitsis.uhuruphotos.person.api.usecase.PersonUseCase
import com.savvasdalkitsis.uhuruphotos.photos.model.PhotoSequenceDataSource.*
import com.savvasdalkitsis.uhuruphotos.photos.mvflow.PhotoAction
import com.savvasdalkitsis.uhuruphotos.photos.mvflow.PhotoAction.*
import com.savvasdalkitsis.uhuruphotos.photos.mvflow.PhotoAction.DismissErrorMessage
import com.savvasdalkitsis.uhuruphotos.photos.mvflow.PhotoAction.HideInfo
import com.savvasdalkitsis.uhuruphotos.photos.mvflow.PhotoAction.NavigateBack
import com.savvasdalkitsis.uhuruphotos.photos.mvflow.PhotoAction.SharePhoto
import com.savvasdalkitsis.uhuruphotos.photos.mvflow.PhotoAction.ShowInfo
import com.savvasdalkitsis.uhuruphotos.photos.mvflow.PhotoEffect
import com.savvasdalkitsis.uhuruphotos.photos.mvflow.PhotoEffect.*
import com.savvasdalkitsis.uhuruphotos.photos.mvflow.PhotoMutation
import com.savvasdalkitsis.uhuruphotos.photos.mvflow.PhotoMutation.*
import com.savvasdalkitsis.uhuruphotos.photos.service.model.deserializePeopleNames
import com.savvasdalkitsis.uhuruphotos.photos.usecase.PhotosUseCase
import com.savvasdalkitsis.uhuruphotos.photos.view.state.PhotoState
import com.savvasdalkitsis.uhuruphotos.photos.view.state.SinglePhotoState
import com.savvasdalkitsis.uhuruphotos.search.api.SearchUseCase
import com.savvasdalkitsis.uhuruphotos.strings.R
import com.savvasdalkitsis.uhuruphotos.user.usecase.UserUseCase
import com.savvasdalkitsis.uhuruphotos.viewmodel.Handler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class PhotoHandler @Inject constructor(
    private val photosUseCase: PhotosUseCase,
    private val peopleUseCase: PeopleUseCase,
    private val personUseCase: PersonUseCase,
    private val userUseCase: UserUseCase,
    private val albumsUseCase: AlbumsUseCase,
    private val searchUseCase: SearchUseCase,
) : Handler<PhotoState, PhotoEffect, PhotoAction, PhotoMutation> {

    override fun invoke(
        state: PhotoState,
        action: PhotoAction,
        effect: suspend (PhotoEffect) -> Unit
    ): Flow<PhotoMutation> = when(action) {
        is LoadPhoto -> flow {
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
                }
            }
        }
        is ChangedToPage -> flow {
            emit(ChangeCurrentIndex(action.page))
            if (state.photos.isNotEmpty()) {
                val photo = state.photos[action.page]
                loadPhotoDetails(photo.id)
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
            emit(ShowPhotoFavourite(state.currentPhoto.id, action.favourite))
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
        AskForPhotoDeletion -> flowOf(ShowDeletionConfirmationDialog)
        DismissPhotoDeletionDialog -> flowOf(HideDeletionConfirmationDialog)
        DeletePhoto -> flow {
            emit(Loading)
            emit(HideDeletionConfirmationDialog)
            photosUseCase.deletePhoto(state.currentPhoto.id)
            emit(FinishedLoading)
            emit(RemovePhotoFromSource(state.currentPhoto.id))
            if (state.photos.size == 1) {
                effect(PhotoEffect.NavigateBack)
            }
        }
        SharePhoto -> flow {
            effect(PhotoEffect.SharePhoto(state.currentPhoto.fullResUrl))
        }
        is FullImageLoaded -> flowOf(ShowShareIcon(action.id))
        is PersonSelected -> flow {
            effect(NavigateToPerson(action.person.id))
        }
    }

    context(PhotosUseCase)
    private suspend fun FlowCollector<PhotoMutation>.loadAlbums(
        albums: List<Album>,
        action: LoadPhoto
    ) {
        val photoStates = albums.flatMap { album ->
            album.photos
        }.map { photo ->
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

                val favouriteThreshold = userUseCase.getUserOrRefresh()?.favoriteMinRating
                emit(ReceivedDetails(details, peopleInPhoto, favouriteThreshold))
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
