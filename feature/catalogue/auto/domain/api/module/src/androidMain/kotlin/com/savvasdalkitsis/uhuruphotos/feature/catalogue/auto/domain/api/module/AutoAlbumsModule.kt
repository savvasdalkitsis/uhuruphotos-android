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
package com.savvasdalkitsis.uhuruphotos.feature.catalogue.auto.domain.api.module

import com.savvasdalkitsis.uhuruphotos.feature.auth.domain.api.module.AuthModule
import com.savvasdalkitsis.uhuruphotos.feature.catalogue.auto.domain.api.usecase.AutoAlbumsUseCase
import com.savvasdalkitsis.uhuruphotos.feature.catalogue.auto.domain.implementation.repository.AutoAlbumsRepository
import com.savvasdalkitsis.uhuruphotos.feature.catalogue.auto.domain.implementation.service.AutoAlbumsService
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.module.DbModule
import com.savvasdalkitsis.uhuruphotos.feature.user.domain.api.module.UserModule
import com.savvasdalkitsis.uhuruphotos.foundation.android.api.module.AndroidModule
import com.savvasdalkitsis.uhuruphotos.foundation.inject.api.singleInstance
import com.savvasdalkitsis.uhuruphotos.foundation.preferences.api.module.PreferencesModule

object AutoAlbumsModule {

    val autoAlbumsUseCase: AutoAlbumsUseCase
        get() = com.savvasdalkitsis.uhuruphotos.feature.catalogue.auto.domain.implementation.usecase.AutoAlbumsUseCase(
            autoAlbumsRepository,
            PreferencesModule.plainTextPreferences,
            UserModule.userUseCase,
            AndroidModule.applicationContext,
        )

    private val autoAlbumsRepository get() = AutoAlbumsRepository(
        DbModule.database.autoAlbumsQueries,
        autoAlbumsService,
    )

    private val autoAlbumsService: AutoAlbumsService by singleInstance {
        AuthModule.ktorfit.create()
    }
}