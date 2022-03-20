package com.savvasdalkitsis.librephotos.server.viewmodel.state

import dev.zacsweers.redacted.annotations.Redacted

sealed class ServerState {
    object Loading: ServerState()
    data class ServerUrl(val url: String): ServerState()
    data class UserCredentials(val username: String, @Redacted val password: String): ServerState()
}
