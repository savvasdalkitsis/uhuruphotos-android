package com.savvasdalkitsis.uhuruphotos.photos.module

import com.savvasdalkitsis.uhuruphotos.db.Database
import com.savvasdalkitsis.uhuruphotos.photos.service.PhotosService
import com.savvasdalkitsis.uhuruphotos.db.photos.PhotoDetailsQueries
import com.savvasdalkitsis.uhuruphotos.db.photos.PhotoSummaryQueries
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