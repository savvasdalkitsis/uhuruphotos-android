package com.savvasdalkitsis.librephotos.server.viewmodel.state

data class ServerState(
    val isLoading: Boolean = true,
    val serverUrl: String? = null,
    val showUserCredentialsInput: Boolean = false,
)
