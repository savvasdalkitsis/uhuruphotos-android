package com.savvasdalkitsis.librephotos.server.usecase

import com.savvasdalkitsis.librephotos.extensions.awaitSingleOrNull
import com.savvasdalkitsis.librephotos.extensions.crud
import com.savvasdalkitsis.librephotos.server.db.Server
import com.savvasdalkitsis.librephotos.server.db.ServerQueries
import javax.inject.Inject

class ServerUseCase @Inject constructor(
    private val serverQueries: ServerQueries
) {

    @Volatile
    private var serverUrlCache: String? = null

    suspend fun getServerUrl(): String? = serverUrlCache ?:
        serverQueries.getServerUrl().awaitSingleOrNull()?.trim().also { serverUrlCache = it }

    suspend fun setServerUrl(serverUrl: String) {
        crud {
            serverQueries.transaction {
                serverQueries.delete()
                serverQueries.setServerUrl(Server(serverUrl))
                serverUrlCache = serverUrl
            }
        }
    }
}
