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
package com.savvasdalkitsis.uhuruphotos.server.seam

import dev.zacsweers.redacted.annotations.Redacted

sealed class ServerAction {
    object CheckPersistedServer : ServerAction()
    object RequestServerUrlChange: ServerAction()
    object DismissUnsecuredServerDialog: ServerAction()
    data class ChangeServerUrlTo(val url: String) : ServerAction()
    data class AttemptChangeServerUrlTo(val url: String) : ServerAction()
    data class UrlTyped(val url: String) : ServerAction()
    data class UsernameChangedTo(val username: String) : ServerAction()
    data class UserPasswordChangedTo(@Redacted val password: String) : ServerAction()
    object Login : ServerAction()
    object SendLogsClick : ServerAction()
    object TogglePasswordVisibility : ServerAction()
}
