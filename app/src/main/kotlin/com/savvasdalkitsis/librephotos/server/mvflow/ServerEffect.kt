package com.savvasdalkitsis.librephotos.server.mvflow

sealed class ServerEffect {
    data class ErrorLoggingIn(val e: Exception) : ServerEffect()
    object Close : ServerEffect()
}
