package com.savvasdalkitsis.uhuruphotos.albums.module

import com.savvasdalkitsis.uhuruphotos.albums.service.AlbumsService
import com.savvasdalkitsis.uhuruphotos.db.albums.AlbumsQueries
import com.savvasdalkitsis.uhuruphotos.db.Database
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit

@InstallIn(SingletonComponent::class)
@Module
class AlbumsModule {

    @Provides
    fun albumsService(
        retrofit: Retrofit,
    ): AlbumsService = retrofit.create(AlbumsService::class.java)
}