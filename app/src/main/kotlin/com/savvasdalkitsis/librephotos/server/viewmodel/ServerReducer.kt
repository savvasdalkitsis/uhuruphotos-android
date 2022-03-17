package com.savvasdalkitsis.librephotos.server.viewmodel

import com.savvasdalkitsis.librephotos.server.mvflow.ServerMutation
import com.savvasdalkitsis.librephotos.server.viewmodel.state.ServerState
import net.pedroloureiro.mvflow.Reducer
import javax.inject.Inject

class ServerReducer @Inject constructor() : Reducer<ServerState, ServerMutation> {

    override fun invoke(
        state: ServerState,
        mutation: ServerMutation
    ): ServerState = when(mutation) {
        is ServerMutation.AskForServerDetails -> state.copy(
            isLoading = false,
            serverUrl = mutation.previousUrl.orEmpty(),
            showUserCredentialsInput = false,
        )
        ServerMutation.AskForUserCredentials -> state.copy(
            isLoading = false,
            serverUrl = null,
            showUserCredentialsInput = true,
        )
        is ServerMutation.ChangeUrlTo -> state.copy(
            isLoading = false,
            serverUrl = mutation.url,
            showUserCredentialsInput = false,
        )
    }

}
