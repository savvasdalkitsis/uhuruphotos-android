package com.savvasdalkitsis.librephotos.server.mvflow

sealed class ServerEffect {
    object Close : ServerEffect()
}
