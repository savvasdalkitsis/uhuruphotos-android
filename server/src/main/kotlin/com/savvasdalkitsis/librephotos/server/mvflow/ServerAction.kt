package com.savvasdalkitsis.librephotos.server.mvflow

import dev.zacsweers.redacted.annotations.Redacted

sealed class ServerAction {
    object CheckPersistedServer : ServerAction()
    object RequestServerUrlChange: ServerAction()
    data class ChangeServerUrlTo(val url: String) : ServerAction()
    data class UrlTyped(val url: String) : ServerAction()
    data class UsernameChangedTo(val username: String) : ServerAction()
    data class UserPasswordChangedTo(@Redacted val password: String) : ServerAction()
    object Login : ServerAction()
}
