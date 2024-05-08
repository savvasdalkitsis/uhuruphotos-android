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
package com.savvasdalkitsis.uhuruphotos.feature.search.domain.api.module

import com.savvasdalkitsis.uhuruphotos.feature.auth.domain.api.module.AuthModule
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.module.DbModule
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.module.CommonMediaModule
import com.savvasdalkitsis.uhuruphotos.feature.search.domain.api.usecase.SearchUseCase
import com.savvasdalkitsis.uhuruphotos.feature.search.domain.implementation.repository.SearchRepository
import com.savvasdalkitsis.uhuruphotos.feature.search.domain.implementation.service.SearchService
import com.savvasdalkitsis.uhuruphotos.foundation.date.api.module.DateModule
import com.savvasdalkitsis.uhuruphotos.foundation.inject.api.singleInstance
import com.savvasdalkitsis.uhuruphotos.foundation.preferences.api.module.PreferencesModule

object SearchModule {

    val searchUseCase: SearchUseCase
        get() = com.savvasdalkitsis.uhuruphotos.feature.search.domain.implementation.usecase.SearchUseCase(
            searchRepository,
            DateModule.dateDisplayer,
            CommonMediaModule.mediaUseCase,
        )

    private val searchRepository get() = SearchRepository(
        searchService,
        DbModule.database.searchQueries,
        DbModule.database.remoteMediaItemSummaryQueries,
        PreferencesModule.plainTextPreferences,
    )

    private val searchService: SearchService by singleInstance {
        AuthModule.ktorfit.create()
    }


}