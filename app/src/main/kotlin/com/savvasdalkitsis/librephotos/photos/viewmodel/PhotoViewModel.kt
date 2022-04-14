package com.savvasdalkitsis.librephotos.photos.viewmodel

import androidx.compose.material.ExperimentalMaterialApi
import androidx.lifecycle.ViewModel
import com.savvasdalkitsis.librephotos.photos.mvflow.PhotoAction
import com.savvasdalkitsis.librephotos.photos.mvflow.PhotoEffect
import com.savvasdalkitsis.librephotos.photos.mvflow.PhotoMutation
import com.savvasdalkitsis.librephotos.photos.view.state.PhotoState
import com.savvasdalkitsis.librephotos.viewmodel.ActionReceiver
import com.savvasdalkitsis.librephotos.viewmodel.ActionReceiverHost
import dagger.hilt.android.lifecycle.HiltViewModel
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@ExperimentalMaterialApi
@HiltViewModel
class PhotoViewModel @Inject constructor(
    handler: PhotoHandler,
    reducer: PhotoReducer,
) : ViewModel(),
    com.savvasdalkitsis.librephotos.viewmodel.ActionReceiverHost<PhotoState, PhotoEffect, PhotoAction, PhotoMutation> {

    override val initialState = PhotoState()

    override val actionReceiver = com.savvasdalkitsis.librephotos.viewmodel.ActionReceiver(
        handler,
        reducer,
        container(initialState)
    )
}
