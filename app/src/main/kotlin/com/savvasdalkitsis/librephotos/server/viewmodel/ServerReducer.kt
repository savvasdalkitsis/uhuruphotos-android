package com.savvasdalkitsis.librephotos.server.viewmodel

import com.savvasdalkitsis.librephotos.server.mvflow.ServerMutation
import com.savvasdalkitsis.librephotos.server.mvflow.ServerMutation.*
import com.savvasdalkitsis.librephotos.server.viewmodel.state.ServerState
import com.savvasdalkitsis.librephotos.server.viewmodel.state.ServerState.*
import net.pedroloureiro.mvflow.Reducer
import javax.inject.Inject

class ServerReducer @Inject constructor() : Reducer<ServerState, ServerMutation> {

    override fun invoke(
        state: ServerState,
        mutation: ServerMutation
    ): ServerState = when (mutation) {
        is AskForServerDetails -> ServerUrl(mutation.previousUrl.orEmpty())
        is AskForUserCredentials -> UserCredentials(mutation.userName, mutation.password)
        is ChangeUrlTo -> ServerUrl(mutation.url)
        is ChangePasswordTo -> (state as UserCredentials).copy(password = mutation.password)
        is ChangeUserEmailTo -> (state as UserCredentials).copy(userEmail = mutation.userEmail)
        PerformingBackgroundJob -> Loading
    }

}
