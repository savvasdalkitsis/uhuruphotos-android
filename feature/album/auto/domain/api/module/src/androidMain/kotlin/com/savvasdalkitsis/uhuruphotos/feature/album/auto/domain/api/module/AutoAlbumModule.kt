/*
Copyright 2024 Savvas Dalkitsis

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
package com.savvasdalkitsis.uhuruphotos.feature.album.auto.domain.api.module

import com.savvasdalkitsis.uhuruphotos.feature.album.auto.domain.api.usecase.AutoAlbumUseCase
import com.savvasdalkitsis.uhuruphotos.feature.album.auto.domain.implementation.repository.AutoAlbumRepository
import com.savvasdalkitsis.uhuruphotos.feature.album.auto.domain.implementation.service.AutoAlbumService
import com.savvasdalkitsis.uhuruphotos.feature.auth.domain.api.module.AuthModule
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.module.DbModule
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.module.CommonMediaModule
import com.savvasdalkitsis.uhuruphotos.foundation.inject.api.singleInstance

object AutoAlbumModule {

    val autoAlbumUseCase: AutoAlbumUseCase
        get() = com.savvasdalkitsis.uhuruphotos.feature.album.auto.domain.implementation.usecase.AutoAlbumUseCase(
            autoAlbumRepository,
            CommonMediaModule.mediaUseCase,
        )

    private val autoAlbumRepository get() = AutoAlbumRepository(
        DbModule.database,
        DbModule.database.autoAlbumQueries,
        DbModule.database.autoAlbumPeopleQueries,
        DbModule.database.peopleQueries,
        DbModule.database.autoAlbumPhotosQueries,
        DbModule.database.remoteMediaItemDetailsQueries,
        autoAlbumService,
    )

    private val autoAlbumService: AutoAlbumService by singleInstance {
        AuthModule.ktorfit.create()
    }
}