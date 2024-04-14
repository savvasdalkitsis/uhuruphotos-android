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
package com.savvasdalkitsis.uhuruphotos.feature.people.view.implementation.seam

import com.savvasdalkitsis.uhuruphotos.feature.auth.domain.api.usecase.ServerUseCase
import com.savvasdalkitsis.uhuruphotos.feature.people.domain.api.usecase.PeopleUseCase
import com.savvasdalkitsis.uhuruphotos.feature.people.view.implementation.ui.state.SortOrder
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.Navigator
import com.savvasdalkitsis.uhuruphotos.foundation.strings.api.R
import com.savvasdalkitsis.uhuruphotos.foundation.toaster.api.usecase.ToasterUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

class PeopleActionsContext @Inject constructor(
    val peopleUseCase: PeopleUseCase,
    val serverUseCase: ServerUseCase,
    val toaster: ToasterUseCase,
    val navigator: Navigator,
) {

    private val _sort: MutableSharedFlow<SortOrder> = MutableStateFlow(SortOrder.default)
    val sort: Flow<SortOrder> get() = _sort
    private val _loading: MutableSharedFlow<Boolean> = MutableStateFlow(false)
    val loading: Flow<Boolean> get() = _loading

    suspend fun refresh() {
        _loading.emit(true)
        val result = peopleUseCase.refreshPeople()
        if (result.isErr) {
            toaster.show(R.string.error_refreshing_people)
        }
        _loading.emit(false)
    }

    suspend fun changeSort(sortOrder: SortOrder) {
        _sort.emit(sortOrder)
    }
}
