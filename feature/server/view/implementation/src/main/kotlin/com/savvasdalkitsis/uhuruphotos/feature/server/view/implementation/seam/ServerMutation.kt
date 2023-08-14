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
package com.savvasdalkitsis.uhuruphotos.feature.server.view.implementation.seam

import com.savvasdalkitsis.uhuruphotos.feature.server.view.implementation.ui.ServerState
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.Mutation
import dev.zacsweers.redacted.annotations.Redacted

sealed class ServerMutation(
    mutation: Mutation<ServerState>,
) : Mutation<ServerState> by mutation {

    data class ShowUrlValidation(
        val prefilledUrl: String?,
        val isValid: Boolean,
    ) : ServerMutation({
        it.copy(
            prefilledUrl = prefilledUrl.orEmpty(),
            isUrlValid = isValid,
            isLoggingEnabled = it.isLoggingEnabled,
        ).shouldAllowLogin()
    })

    data class SetLoading(val isLoading: Boolean) : ServerMutation({
        it.copy(isLoading = isLoading)
    })

    data object ShowUnsecureServerConfirmation : ServerMutation({
        it.copy(showUnsecureServerConfirmation = true)
    })

    data object HideUnsecureServerConfirmation : ServerMutation({
        it.copy(showUnsecureServerConfirmation = false)
    })

    data object ShowHelpDialog : ServerMutation({
        it.copy(showHelpDialog = true)
    })

    data object HideHelpDialog : ServerMutation({
        it.copy(showHelpDialog = false)
    })

    data class SetCurrentUrlTo(val url: String) : ServerMutation({
        it.copy(currentUrl = url).shouldAllowLogin()
    })

    data class ChangeUsernameTo(val username: String) : ServerMutation({
        it.copy(username = username).shouldAllowLogin()
    })

    data class ChangePasswordTo(@Redacted val password: String) : ServerMutation({
        it.copy(password = password).shouldAllowLogin()
    })

    data class SetPasswordVisibility(val visible: Boolean) : ServerMutation({
        it.copy(passwordVisible = visible)
    })

    data class SetLoggingEnabled(val enabled: Boolean) : ServerMutation({
        it.copy(isLoggingEnabled = enabled)
    })

    data class ChangeServerUrlTo(val url: String) : ServerMutation({
        it.copy(prefilledUrl = url, currentUrl = url).shouldAllowLogin()
    })
}

private fun ServerState.shouldAllowLogin(): ServerState =
    copy(allowLogin = username.isNotEmpty() && password.isNotEmpty() && currentUrl.isNotEmpty() && isUrlValid)