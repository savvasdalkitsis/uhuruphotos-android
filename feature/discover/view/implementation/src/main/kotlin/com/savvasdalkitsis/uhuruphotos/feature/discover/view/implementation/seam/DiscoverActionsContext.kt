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
package com.savvasdalkitsis.uhuruphotos.feature.discover.view.implementation.seam

import com.savvasdalkitsis.uhuruphotos.feature.auth.domain.api.usecase.ServerUseCase
import com.savvasdalkitsis.uhuruphotos.feature.feed.domain.api.usecase.FeedUseCase
import com.savvasdalkitsis.uhuruphotos.feature.heatmap.domain.api.usecase.HeatMapUseCase
import com.savvasdalkitsis.uhuruphotos.feature.people.domain.api.usecase.PeopleUseCase
import com.savvasdalkitsis.uhuruphotos.feature.search.domain.api.usecase.SearchUseCase
import com.savvasdalkitsis.uhuruphotos.feature.settings.domain.api.usecase.SettingsUseCase
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.Navigator
import com.savvasdalkitsis.uhuruphotos.foundation.toaster.api.usecase.ToasterUseCase
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.usecase.UiUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import javax.inject.Inject

class DiscoverActionsContext @Inject constructor(
    val feedUseCase: FeedUseCase,
    val settingsUseCase: SettingsUseCase,
    val peopleUseCase: PeopleUseCase,
    val serverUseCase: ServerUseCase,
    val searchUseCase: SearchUseCase,
    val heatMapUseCase: HeatMapUseCase,
    val uiUseCase: UiUseCase,
    val toaster: ToasterUseCase,
    val navigator: Navigator,
) {
    private val _queryFilter = MutableSharedFlow<String>()
    val queryFilter: Flow<String> get() = _queryFilter

    suspend fun changeQuery(query: String) {
        _queryFilter.emit(query)
    }
}
