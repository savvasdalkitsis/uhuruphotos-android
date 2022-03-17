package com.savvasdalkitsis.librephotos.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.savvasdalkitsis.librephotos.auth.db.dao.AuthDao
import com.savvasdalkitsis.librephotos.auth.db.entities.Token
import com.savvasdalkitsis.librephotos.server.db.dao.ServerDao
import com.savvasdalkitsis.librephotos.server.db.entities.Server

@Database(entities = [Token::class, Server::class], version = 1)
abstract class LibrePhotosDatabase : RoomDatabase() {

    abstract fun authDao(): AuthDao
    abstract fun serverDao(): ServerDao
}