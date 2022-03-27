package com.savvasdalkitsis.librephotos.photos.module

import com.savvasdalkitsis.librephotos.photos.api.PhotosService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class Module {

    @Provides
    @Singleton
    fun photosService(
        retrofit: Retrofit,
    ): PhotosService = retrofit.create(PhotosService::class.java)
}