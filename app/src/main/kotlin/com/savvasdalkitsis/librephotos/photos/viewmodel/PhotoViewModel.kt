package com.savvasdalkitsis.librephotos.photos.viewmodel

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

@HiltViewModel
class PhotoViewModel @Inject constructor(
    handler: PhotoHandler
) : ViewModel(),
    ActionReceiverHost<PhotoState, PhotoEffect, PhotoAction, PhotoMutation> {

    override val initialState = PhotoState()

    override val actionReceiver = ActionReceiver(
        handler,
        photoReducer(),
        container(initialState)
    )
}
