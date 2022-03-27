package com.savvasdalkitsis.librephotos.albums.module

import com.savvasdalkitsis.librephotos.Database
import com.savvasdalkitsis.librephotos.albums.api.AlbumsService
import com.savvasdalkitsis.librephotos.albums.db.AlbumsQueries
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit

@InstallIn(SingletonComponent::class)
@Module
class Module {

    @Provides
    fun albumsService(
        retrofit: Retrofit,
    ): AlbumsService = retrofit.create(AlbumsService::class.java)


    @Provides
    fun albumsQueries(database: Database): AlbumsQueries = database.albumsQueries
}