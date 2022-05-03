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

import androidx.work.WorkInfo.State.*
import com.savvasdalkitsis.uhuruphotos.infrastructure.extensions.onErrorsEmit
import com.savvasdalkitsis.uhuruphotos.people.api.usecase.PeopleUseCase
import com.savvasdalkitsis.uhuruphotos.people.api.view.state.toPerson
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
import com.savvasdalkitsis.uhuruphotos.photos.worker.PhotoDetailsRetrieveWorker
import com.savvasdalkitsis.uhuruphotos.viewmodel.Handler
import com.savvasdalkitsis.uhuruphotos.worker.usecase.WorkerStatusUseCase
import kotlinx.coroutines.flow.*
import javax.inject.Inject

class PhotoHandler @Inject constructor(
    private val photosUseCase: PhotosUseCase,
    private val workerStatusUseCase: WorkerStatusUseCase,
    private val peopleUseCase: PeopleUseCase,
) : Handler<PhotoState, PhotoEffect, PhotoAction, PhotoMutation> {

    override fun invoke(
        state: PhotoState,
        action: PhotoAction,
        effect: suspend (PhotoEffect) -> Unit
    ): Flow<PhotoMutation> = when(action) {
        is LoadPhoto -> flow {
            emit(with(photosUseCase) { ReceivedUrl(
                id = action.id,
                lowResUrl = action.id.toThumbnailUrlFromId(),
                fullResUrl = action.id.toFullSizeUrlFromId(action.isVideo),
            )})
            emitAll(merge(
                combine(
                    photosUseCase.getPhoto(action.id),
                    peopleUseCase.getPeopleByName()
                        .onErrorsEmit {
                            effect(ErrorRefreshingPeople)
                            emptyList()
                        }
                ) { details, people ->
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
                    ReceivedDetails(details, peopleInPhoto)
                },
                workerStatusUseCase.monitorUniqueJobStatus(
                    PhotoDetailsRetrieveWorker.workName(action.id)
                ).map {
                    when (it) {
                        BLOCKED, CANCELLED, FAILED -> ShowErrorMessage("Error loading photo details")
                        SUCCEEDED -> FinishedLoading
                        ENQUEUED, RUNNING -> Loading
                    }
                }
            ))
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
            photosUseCase.setPhotoFavourite(state.id, action.favourite)
        }
        Refresh -> flow {
            photosUseCase.refreshDetails(state.id)
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
            photosUseCase.deletePhoto(state.id)
            effect(PhotoEffect.NavigateBack)
        }
        SharePhoto -> flow {
            effect(PhotoEffect.SharePhoto(state.fullResUrl))
        }
        FullImageLoaded -> flowOf(ShowShareIcon)
        is PersonSelected -> flow {
            effect(NavigateToPerson(action.person.id))
        }
    }
}
