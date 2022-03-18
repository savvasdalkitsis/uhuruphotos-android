package com.savvasdalkitsis.librephotos.server.viewmodel

import com.savvasdalkitsis.librephotos.server.mvflow.ServerAction
import com.savvasdalkitsis.librephotos.server.mvflow.ServerEffect
import com.savvasdalkitsis.librephotos.server.mvflow.ServerMutation
import com.savvasdalkitsis.librephotos.server.viewmodel.state.ServerState
import com.savvasdalkitsis.librephotos.viewmodel.MVFlowViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ServerViewModel @Inject constructor(
    handler: ServerHandler,
    reducer: ServerReducer,
) : MVFlowViewModel<ServerState, ServerAction, ServerMutation, ServerEffect>(
    handler = handler,
    reducer = reducer,
    ServerState.Loading,
    ServerAction.CheckPersistedServer,
)