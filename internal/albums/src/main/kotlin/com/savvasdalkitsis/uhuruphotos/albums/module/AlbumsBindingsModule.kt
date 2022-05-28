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
package com.savvasdalkitsis.uhuruphotos.albums.module

import com.savvasdalkitsis.uhuruphotos.albums.initializer.AlbumsInitializer
import com.savvasdalkitsis.uhuruphotos.albums.repository.AlbumsRepository
import com.savvasdalkitsis.uhuruphotos.albums.usecase.AlbumsUseCase
import com.savvasdalkitsis.uhuruphotos.albums.worker.AlbumWorkScheduler
import com.savvasdalkitsis.uhuruphotos.initializer.ApplicationCreated
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
            com.savvasdalkitsis.uhuruphotos.albums.api.worker.AlbumWorkScheduler

    @Binds
    abstract fun albumsUseCase(albumsUseCase: AlbumsUseCase):
            com.savvasdalkitsis.uhuruphotos.albums.api.usecase.AlbumsUseCase

    @Binds
    abstract fun albumsRepository(albumsRepository: AlbumsRepository):
            com.savvasdalkitsis.uhuruphotos.albums.api.repository.AlbumsRepository
}