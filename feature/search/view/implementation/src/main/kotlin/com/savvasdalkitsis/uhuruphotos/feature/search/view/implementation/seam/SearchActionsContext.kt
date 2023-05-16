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
package com.savvasdalkitsis.uhuruphotos.feature.search.view.implementation.seam

import com.savvasdalkitsis.uhuruphotos.feature.auth.domain.api.usecase.ServerUseCase
import com.savvasdalkitsis.uhuruphotos.feature.feed.domain.api.usecase.FeedUseCase
import com.savvasdalkitsis.uhuruphotos.feature.people.domain.api.usecase.PeopleUseCase
import com.savvasdalkitsis.uhuruphotos.feature.search.domain.api.usecase.SearchUseCase
import com.savvasdalkitsis.uhuruphotos.feature.settings.domain.api.usecase.SettingsUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import javax.inject.Inject

class SearchActionsContext @Inject constructor(
    val searchUseCase: SearchUseCase,
    val feedUseCase: FeedUseCase,
    val settingsUseCase: SettingsUseCase,
    val peopleUseCase: PeopleUseCase,
    val serverUseCase: ServerUseCase,
) {
    var lastSearch: Job? = null
    val queryFilter = MutableSharedFlow<String>()
}
