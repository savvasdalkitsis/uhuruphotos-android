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
package com.savvasdalkitsis.uhuruphotos.implementation.server.seam

import com.savvasdalkitsis.uhuruphotos.api.seam.Mutation
import com.savvasdalkitsis.uhuruphotos.implementation.server.view.ServerState
import com.savvasdalkitsis.uhuruphotos.implementation.server.view.ServerState.Loading
import com.savvasdalkitsis.uhuruphotos.implementation.server.view.ServerState.ServerUrl
import com.savvasdalkitsis.uhuruphotos.implementation.server.view.ServerState.UserCredentials
import dev.zacsweers.redacted.annotations.Redacted

internal sealed class ServerMutation(
    mutation: Mutation<ServerState>,
) : Mutation<ServerState> by mutation {

    data class AskForServerDetails(
        val previousUrl: String?,
        val isValid: Boolean,
    ) : ServerMutation({
        ServerUrl(
            prefilledUrl = previousUrl.orEmpty(),
            isUrlValid = isValid,
            allowSaveUrl = isValid,
        )
    })

    data class AskForUserCredentials(
        val userName: String,
        @Redacted val password: String,
    ) : ServerMutation({
        UserCredentials(
            userName,
            password,
            allowLogin = false,
            passwordVisible = false,
        ).shouldAllowLogin()
    })

    data class ShowUrlValidation(
        val prefilledUrl: String?,
        val isValid: Boolean,
    ) : ServerMutation({
        ServerUrl(
            prefilledUrl = prefilledUrl.orEmpty(),
            isUrlValid = isValid,
            allowSaveUrl = isValid,
        )
    })

    object PerformingBackgroundJob : ServerMutation({
        Loading
    })

    object ShowUnsecureServerConfirmation : ServerMutation({
        (it as ServerUrl).copy(showUnsecureServerConfirmation = true)
    })

    object HideUnsecureServerConfirmation : ServerMutation({
        (it as ServerUrl).copy(showUnsecureServerConfirmation = false)
    })

    data class ChangeUsernameTo(val username: String) : ServerMutation({
        (it as UserCredentials).copy(username = username).shouldAllowLogin()
    })

    data class ChangePasswordTo(@Redacted val password: String) : ServerMutation({
        (it as UserCredentials).copy(password = password).shouldAllowLogin()
    })

    data class SetPasswordVisibility(val visible: Boolean) : ServerMutation({
        (it as UserCredentials).copy(passwordVisible = visible)
    })
}

private fun UserCredentials.shouldAllowLogin(): UserCredentials =
    copy(allowLogin = username.isNotEmpty() && password.isNotEmpty())
