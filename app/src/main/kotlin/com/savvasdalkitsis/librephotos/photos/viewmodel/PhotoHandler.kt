package com.savvasdalkitsis.librephotos.photos.viewmodel

import com.savvasdalkitsis.librephotos.photos.mvflow.PhotoAction
import com.savvasdalkitsis.librephotos.photos.mvflow.PhotoAction.*
import com.savvasdalkitsis.librephotos.photos.mvflow.PhotoEffect
import com.savvasdalkitsis.librephotos.photos.mvflow.PhotoEffect.HideSystemBars
import com.savvasdalkitsis.librephotos.photos.mvflow.PhotoEffect.ShowSystemBars
import com.savvasdalkitsis.librephotos.photos.mvflow.PhotoMutation
import com.savvasdalkitsis.librephotos.photos.mvflow.PhotoMutation.*
import com.savvasdalkitsis.librephotos.photos.usecase.PhotosUseCase
import com.savvasdalkitsis.librephotos.photos.view.state.PhotoState
import com.savvasdalkitsis.librephotos.viewmodel.Handler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PhotoHandler @Inject constructor(
    private val photosUseCase: PhotosUseCase,
) : Handler<PhotoState, PhotoEffect, PhotoAction, PhotoMutation> {

    override fun invoke(
        state: PhotoState,
        action: PhotoAction,
        effect: suspend (PhotoEffect) -> Unit
    ): Flow<PhotoMutation> = when(action) {
        is LoadPhoto -> flow {
            emit(with(photosUseCase) { ReceivedUrl(
                lowResUrl = action.id.toThumbnailUrlFromId(),
                fullResUrl = action.id.toFullSizeUrlFromId(),
            )})
            emitAll(photosUseCase.getPhoto(action.id)
                .map(::ReceivedDetails)
            )
        }
        ToggleUI -> flow {
            if (state.isUIShowing) {
                emit(HideUI)
                effect(HideSystemBars)
            } else {
                emit(ShowUI)
                effect(ShowSystemBars)
            }
        }
        NavigateBack -> flow {
            effect(PhotoEffect.NavigateBack)
        }
    }

}
