/*
Copyright 2022 Savvas Dalkitsis

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
package com.savvasdalkitsis.uhuruphotos.implementation.albums.module

import com.savvasdalkitsis.uhuruphotos.foundation.initializer.api.ApplicationCreated
import com.savvasdalkitsis.uhuruphotos.implementation.albums.TempRemoteMediaUseCase
import com.savvasdalkitsis.uhuruphotos.implementation.albums.initializer.AlbumsInitializer
import com.savvasdalkitsis.uhuruphotos.implementation.albums.repository.AlbumsRepository
import com.savvasdalkitsis.uhuruphotos.implementation.albums.worker.AlbumWorkScheduler
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet

@Module
@InstallIn(SingletonComponent::class)
internal abstract class AlbumsBindingsModule {

    @Binds @IntoSet
    abstract fun albumInitializer(albumsInitializer: AlbumsInitializer): ApplicationCreated

    @Binds
    abstract fun albumWorkScheduler(albumWorkScheduler: AlbumWorkScheduler):
            com.savvasdalkitsis.uhuruphotos.api.albums.worker.AlbumWorkScheduler

    @Binds
    abstract fun albumsRepository(albumsRepository: AlbumsRepository):
            com.savvasdalkitsis.uhuruphotos.api.albums.repository.AlbumsRepository

    @Binds
    abstract fun tempRemoteMediaCollectionsUseCase(tempRemoteMediaUseCase: TempRemoteMediaUseCase):
            com.savvasdalkitsis.uhuruphotos.api.albums.TempRemoteMediaUseCase
}