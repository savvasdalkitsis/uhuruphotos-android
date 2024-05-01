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
package com.savvasdalkitsis.uhuruphotos.feature.person.domain.api.module

import com.savvasdalkitsis.uhuruphotos.feature.auth.domain.api.module.AuthModule
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.module.DbModule
import com.savvasdalkitsis.uhuruphotos.feature.feed.domain.api.module.FeedModule
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.module.CommonMediaModule
import com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.api.module.RemoteMediaModule
import com.savvasdalkitsis.uhuruphotos.feature.person.domain.api.usecase.PersonUseCase
import com.savvasdalkitsis.uhuruphotos.feature.person.domain.implementation.repository.PersonRepository
import com.savvasdalkitsis.uhuruphotos.feature.person.domain.implementation.service.PersonService
import com.savvasdalkitsis.uhuruphotos.foundation.inject.api.singleInstance

object PersonModule {

    val personUseCase: PersonUseCase
        get() = com.savvasdalkitsis.uhuruphotos.feature.person.domain.implementation.usecase.PersonUseCase(
            personRepository,
            CommonMediaModule.mediaUseCase,
            FeedModule.feedUseCase,
            FeedModule.feedWorkScheduler,
        )

    private val personRepository get() = PersonRepository(
        DbModule.database.personQueries,
        personService,
        DbModule.database.remoteMediaCollectionsQueries,
        RemoteMediaModule.remoteMediaUseCase,
    )

    private val personService: PersonService by singleInstance {
        AuthModule.ktorfit.create()
    }
}