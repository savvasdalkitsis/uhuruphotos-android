package com.savvasdalkitsis.librephotos.server.usecase

import com.savvasdalkitsis.librephotos.server.db.dao.ServerDao
import com.savvasdalkitsis.librephotos.server.db.entities.Server
import javax.inject.Inject

class ServerUseCase @Inject constructor(
    private val serverDao: ServerDao
) {

    suspend fun getServerUrl(): String? = serverDao.getServerUrl()

    suspend fun setServerUrl(serverUrl: String) {
        serverDao.deleteAll()
        serverDao.setServerUrl(Server(serverUrl))
    }
}
