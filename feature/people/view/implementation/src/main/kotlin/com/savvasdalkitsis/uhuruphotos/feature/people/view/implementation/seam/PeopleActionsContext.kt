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
import com.savvasdalkitsis.uhuruphotos.feature.people.view.implementation.ui.state.SortOrderState
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.Navigator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import uhuruphotos_android.foundation.strings.api.generated.resources.Res.string
import uhuruphotos_android.foundation.strings.api.generated.resources.error_refreshing_people
import usecase.ToasterUseCase
import javax.inject.Inject

class PeopleActionsContext @Inject constructor(
    val peopleUseCase: PeopleUseCase,
    val serverUseCase: ServerUseCase,
    val toaster: ToasterUseCase,
    val navigator: Navigator,
) {

    private val _sort: MutableSharedFlow<SortOrderState> = MutableStateFlow(SortOrderState.default)
    val sort: Flow<SortOrderState> get() = _sort
    private val _loading: MutableSharedFlow<Boolean> = MutableStateFlow(false)
    val loading: Flow<Boolean> get() = _loading

    suspend fun refresh() {
        _loading.emit(true)
        val result = peopleUseCase.refreshPeople()
        if (result.isErr) {
            toaster.show(string.error_refreshing_people)
        }
        _loading.emit(false)
    }

    suspend fun changeSort(sortOrderState: SortOrderState) {
        _sort.emit(sortOrderState)
    }
}
