package com.savvasdalkitsis.librephotos.server.viewmodel

import androidx.lifecycle.ViewModel
import com.savvasdalkitsis.librephotos.server.mvflow.ServerAction
import com.savvasdalkitsis.librephotos.server.mvflow.ServerEffect
import com.savvasdalkitsis.librephotos.server.mvflow.ServerMutation
import com.savvasdalkitsis.librephotos.server.view.ServerState
import com.savvasdalkitsis.librephotos.viewmodel.ActionReceiver
import com.savvasdalkitsis.librephotos.viewmodel.ActionReceiverHost
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ServerViewModel @Inject constructor(
    handler: ServerHandler,
) : ViewModel(),
    ActionReceiverHost<ServerState, ServerEffect, ServerAction, ServerMutation> {

    override val actionReceiver = ActionReceiver(
        handler,
        serverReducer(),
        ServerState.Loading,
    )
}