package com.savvasdalkitsis.librephotos.server.mvflow

sealed class ServerAction {
    object CheckPersistedServer : ServerAction()
    object ServerUrlChange : ServerAction()
    data class UrlChangedTo(val url: String) : ServerAction()
}
