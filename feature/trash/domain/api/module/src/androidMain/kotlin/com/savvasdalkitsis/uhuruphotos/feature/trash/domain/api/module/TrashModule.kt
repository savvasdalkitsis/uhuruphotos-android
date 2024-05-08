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
package com.savvasdalkitsis.uhuruphotos.feature.trash.domain.api.module

import com.savvasdalkitsis.uhuruphotos.feature.auth.domain.api.module.AuthModule
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.module.DbModule
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.module.CommonMediaModule
import com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.api.module.RemoteMediaModule
import com.savvasdalkitsis.uhuruphotos.feature.trash.domain.api.usecase.TrashUseCase
import com.savvasdalkitsis.uhuruphotos.feature.trash.domain.implementation.repository.TrashRepository
import com.savvasdalkitsis.uhuruphotos.feature.trash.domain.implementation.service.TrashService
import com.savvasdalkitsis.uhuruphotos.foundation.inject.api.singleInstance
import com.savvasdalkitsis.uhuruphotos.foundation.preferences.api.module.PreferencesModule

object TrashModule {

    val trashUseCase: TrashUseCase
        get() = com.savvasdalkitsis.uhuruphotos.feature.trash.domain.implementation.usecase.TrashUseCase(
            CommonMediaModule.mediaUseCase,
            trashRepository,
            PreferencesModule.plainTextPreferences,
        )

    private val trashRepository get() = TrashRepository(
        DbModule.database.remoteMediaTrashQueries,
        RemoteMediaModule.remoteMediaUseCase,
        trashService,
    )

    private val trashService: TrashService by singleInstance {
        AuthModule.ktorfit.create()
    }
}