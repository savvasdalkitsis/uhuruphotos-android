package com.savvasdalkitsis.librephotos.albums.module

import com.savvasdalkitsis.librephotos.albums.initializer.AlbumsInitializer
import com.savvasdalkitsis.librephotos.initializer.ApplicationCreated
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet

@Module
@InstallIn(SingletonComponent::class)
abstract class AlbumsBindingsModule {

    @Binds @IntoSet
    abstract fun albumInitializer(albumsInitializer: AlbumsInitializer): ApplicationCreated
}