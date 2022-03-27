package com.savvasdalkitsis.librephotos.server.module

import com.savvasdalkitsis.librephotos.Database
import com.savvasdalkitsis.librephotos.albums.db.ServerQueries
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class Module {

    @Provides
    fun serverQueries(database: Database): ServerQueries = database.serverQueries
}