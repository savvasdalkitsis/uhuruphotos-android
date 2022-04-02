package com.savvasdalkitsis.librephotos.server.viewmodel

import com.savvasdalkitsis.librephotos.server.mvflow.ServerMutation
import com.savvasdalkitsis.librephotos.server.mvflow.ServerMutation.*
import com.savvasdalkitsis.librephotos.server.view.ServerState
import com.savvasdalkitsis.librephotos.server.view.ServerState.*
import com.savvasdalkitsis.librephotos.viewmodel.Reducer

fun serverReducer() : Reducer<ServerState, ServerMutation> = { state, mutation ->
    when (mutation) {
        is AskForServerDetails -> ServerUrl(mutation.previousUrl.orEmpty())
        is AskForUserCredentials -> UserCredentials(mutation.userName, mutation.password)
        is ChangeUrlTo -> ServerUrl(mutation.url)
        is ChangePasswordTo -> (state as UserCredentials).copy(password = mutation.password)
        is ChangeUsernameTo -> (state as UserCredentials).copy(username = mutation.username)
        PerformingBackgroundJob -> Loading
    }
}
