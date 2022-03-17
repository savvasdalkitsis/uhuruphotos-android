package com.savvasdalkitsis.librephotos.server.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.savvasdalkitsis.librephotos.server.db.entities.Server

@Dao
abstract class ServerDao {

    @Query("select url from Server")
    abstract suspend fun getServerUrl(): String?

    @Query("delete from Server")
    abstract suspend fun deleteAll()

    @Insert
    abstract suspend fun setServerUrl(server: Server)
}