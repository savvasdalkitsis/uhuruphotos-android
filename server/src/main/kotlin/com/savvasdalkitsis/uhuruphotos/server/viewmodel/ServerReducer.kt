package com.savvasdalkitsis.uhuruphotos.server.viewmodel

import com.savvasdalkitsis.uhuruphotos.server.mvflow.ServerMutation
import com.savvasdalkitsis.uhuruphotos.server.mvflow.ServerMutation.*
import com.savvasdalkitsis.uhuruphotos.server.view.ServerState
import com.savvasdalkitsis.uhuruphotos.server.view.ServerState.*
import com.savvasdalkitsis.uhuruphotos.viewmodel.Reducer

fun serverReducer() : Reducer<ServerState, ServerMutation> = { state, mutation ->
    when (mutation) {
        is AskForServerDetails -> ServerUrl(
            url = mutation.previousUrl.orEmpty(),
            isUrlValid = mutation.isValid,
            allowSaveUrl = mutation.isValid,
        )
        is AskForUserCredentials -> UserCredentials(
            mutation.userName,
            mutation.password,
            allowLogin = false
        ).shouldAllowLogin()
        is ChangeUrlTo -> ServerUrl(
            url = mutation.url,
            isUrlValid = mutation.isValid,
            allowSaveUrl = mutation.isValid,
        )
        is ChangePasswordTo -> (state as UserCredentials).copy(password = mutation.password).shouldAllowLogin()
        is ChangeUsernameTo -> (state as UserCredentials).copy(username = mutation.username).shouldAllowLogin()
        PerformingBackgroundJob -> Loading
    }
}

private fun UserCredentials.shouldAllowLogin(): UserCredentials =
    copy(allowLogin = username.isNotEmpty() && password.isNotEmpty())
