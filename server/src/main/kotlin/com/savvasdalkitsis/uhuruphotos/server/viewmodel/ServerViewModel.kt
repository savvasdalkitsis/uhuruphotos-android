package com.savvasdalkitsis.uhuruphotos.server.viewmodel

import androidx.lifecycle.ViewModel
import com.savvasdalkitsis.uhuruphotos.server.mvflow.ServerAction
import com.savvasdalkitsis.uhuruphotos.server.mvflow.ServerEffect
import com.savvasdalkitsis.uhuruphotos.server.mvflow.ServerMutation
import com.savvasdalkitsis.uhuruphotos.server.view.ServerState
import com.savvasdalkitsis.uhuruphotos.viewmodel.ActionReceiver
import com.savvasdalkitsis.uhuruphotos.viewmodel.ActionReceiverHost
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