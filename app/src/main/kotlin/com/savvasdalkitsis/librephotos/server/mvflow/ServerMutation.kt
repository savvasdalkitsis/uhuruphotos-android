package com.savvasdalkitsis.librephotos.server.mvflow

sealed class ServerMutation {

    data class AskForServerDetails(val previousUrl: String?) : ServerMutation()
    object AskForUserCredentials : ServerMutation()
    data class ChangeUrlTo(val url: String) : ServerMutation()
}
