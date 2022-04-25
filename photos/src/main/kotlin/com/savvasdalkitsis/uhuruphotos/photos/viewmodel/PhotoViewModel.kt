package com.savvasdalkitsis.uhuruphotos.photos.viewmodel

import androidx.lifecycle.ViewModel
import com.savvasdalkitsis.uhuruphotos.photos.mvflow.PhotoAction
import com.savvasdalkitsis.uhuruphotos.photos.mvflow.PhotoEffect
import com.savvasdalkitsis.uhuruphotos.photos.mvflow.PhotoMutation
import com.savvasdalkitsis.uhuruphotos.photos.view.state.PhotoState
import com.savvasdalkitsis.uhuruphotos.viewmodel.ActionReceiver
import com.savvasdalkitsis.uhuruphotos.viewmodel.ActionReceiverHost
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PhotoViewModel @Inject constructor(
    handler: PhotoHandler,
    reducer: PhotoReducer,
) : ViewModel(),
    ActionReceiverHost<PhotoState, PhotoEffect, PhotoAction, PhotoMutation> {

    override val actionReceiver = ActionReceiver(
        handler,
        reducer,
        PhotoState(),
    )
}
