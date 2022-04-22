package com.savvasdalkitsis.librephotos.photos.module

import com.savvasdalkitsis.librephotos.db.Database
import com.savvasdalkitsis.librephotos.photos.service.PhotosService
import com.savvasdalkitsis.librephotos.db.photos.PhotoDetailsQueries
import com.savvasdalkitsis.librephotos.db.photos.PhotoSummaryQueries
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class PhotosModule {

    @Provides
    @Singleton
    fun photosService(
        retrofit: Retrofit,
    ): PhotosService = retrofit.create(PhotosService::class.java)

    @Provides
    fun photoDetailsQueries(database: Database): PhotoDetailsQueries = database.photoDetailsQueries

    @Provides
    fun photoSummaryQueries(database: Database): PhotoSummaryQueries = database.photoSummaryQueries
}