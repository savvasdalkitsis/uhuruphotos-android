package com.savvasdalkitsis.librephotos.server.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.savvasdalkitsis.librephotos.server.mvflow.ServerAction
import com.savvasdalkitsis.librephotos.server.mvflow.ServerEffect
import com.savvasdalkitsis.librephotos.server.mvflow.ServerMutation
import com.savvasdalkitsis.librephotos.server.view.ServerState
import com.savvasdalkitsis.librephotos.viewmodel.ActionReceiver
import com.savvasdalkitsis.librephotos.viewmodel.ActionReceiverHost
import dagger.hilt.android.lifecycle.HiltViewModel
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class ServerViewModel @Inject constructor(
    handler: ServerHandler,
    savedStateHandle: SavedStateHandle,
) : ViewModel(),
    ActionReceiverHost<ServerState, ServerEffect, ServerAction, ServerMutation> {

    override val initialState = ServerState.Loading

    override val actionReceiver = ActionReceiver(
        handler,
        serverReducer(),
        container(initialState, savedStateHandle = savedStateHandle)
    )
}