/*
Copyright 2022 Savvas Dalkitsis

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
package com.savvasdalkitsis.uhuruphotos.server.viewmodel

import com.savvasdalkitsis.uhuruphotos.server.mvflow.ServerMutation
import com.savvasdalkitsis.uhuruphotos.server.mvflow.ServerMutation.AskForServerDetails
import com.savvasdalkitsis.uhuruphotos.server.mvflow.ServerMutation.AskForUserCredentials
import com.savvasdalkitsis.uhuruphotos.server.mvflow.ServerMutation.ChangePasswordTo
import com.savvasdalkitsis.uhuruphotos.server.mvflow.ServerMutation.ChangeUsernameTo
import com.savvasdalkitsis.uhuruphotos.server.mvflow.ServerMutation.HideUnsecureServerConfirmation
import com.savvasdalkitsis.uhuruphotos.server.mvflow.ServerMutation.PerformingBackgroundJob
import com.savvasdalkitsis.uhuruphotos.server.mvflow.ServerMutation.SetPasswordVisibility
import com.savvasdalkitsis.uhuruphotos.server.mvflow.ServerMutation.ShowUnsecureServerConfirmation
import com.savvasdalkitsis.uhuruphotos.server.mvflow.ServerMutation.ShowUrlValidation
import com.savvasdalkitsis.uhuruphotos.server.view.ServerState
import com.savvasdalkitsis.uhuruphotos.server.view.ServerState.Loading
import com.savvasdalkitsis.uhuruphotos.server.view.ServerState.ServerUrl
import com.savvasdalkitsis.uhuruphotos.server.view.ServerState.UserCredentials
import com.savvasdalkitsis.uhuruphotos.viewmodel.Reducer

fun serverReducer() : Reducer<ServerState, ServerMutation> = { state, mutation ->
    when (mutation) {
        is AskForServerDetails -> ServerUrl(
            prefilledUrl = mutation.previousUrl.orEmpty(),
            isUrlValid = mutation.isValid,
            allowSaveUrl = mutation.isValid,
        )
        is AskForUserCredentials -> UserCredentials(
            mutation.userName,
            mutation.password,
            allowLogin = false,
            passwordVisible = false,
        ).shouldAllowLogin()
        is ShowUrlValidation -> ServerUrl(
            prefilledUrl = mutation.prefilledUrl.orEmpty(),
            isUrlValid = mutation.isValid,
            allowSaveUrl = mutation.isValid,
        )
        is ChangePasswordTo -> (state as UserCredentials).copy(password = mutation.password).shouldAllowLogin()
        is ChangeUsernameTo -> (state as UserCredentials).copy(username = mutation.username).shouldAllowLogin()
        PerformingBackgroundJob -> Loading
        ShowUnsecureServerConfirmation -> (state as ServerUrl).copy(showUnsecureServerConfirmation = true)
        HideUnsecureServerConfirmation -> (state as ServerUrl).copy(showUnsecureServerConfirmation = false)
        is SetPasswordVisibility -> (state as UserCredentials).copy(passwordVisible = mutation.visible)
    }
}

private fun UserCredentials.shouldAllowLogin(): UserCredentials =
    copy(allowLogin = username.isNotEmpty() && password.isNotEmpty())
