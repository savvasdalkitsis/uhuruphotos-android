package com.savvasdalkitsis.uhuruphotos.albums.module

import com.savvasdalkitsis.uhuruphotos.albums.initializer.AlbumsInitializer
import com.savvasdalkitsis.uhuruphotos.albums.worker.AlbumWorkScheduler
import com.savvasdalkitsis.uhuruphotos.initializer.ApplicationCreated
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

    @Binds
    abstract fun albumWorkScheduler(albumWorkScheduler: AlbumWorkScheduler):
            com.savvasdalkitsis.uhuruphotos.albums.api.worker.AlbumWorkScheduler
}