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

import com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.api.usecase.RemoteMediaUseCase
import com.savvasdalkitsis.uhuruphotos.feature.people.domain.api.usecase.PeopleUseCase
import com.savvasdalkitsis.uhuruphotos.feature.people.view.implementation.seam.PeopleEffect.ErrorLoadingPeople
import com.savvasdalkitsis.uhuruphotos.feature.people.view.implementation.ui.state.SortOrder
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.EffectHandler
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

class PeopleActionsContext @Inject constructor(
    val peopleUseCase: PeopleUseCase,
    val remoteMediaUseCase: RemoteMediaUseCase,
) {

    val sort: MutableSharedFlow<SortOrder> = MutableStateFlow(SortOrder.default)
    val loading: MutableSharedFlow<Boolean> = MutableStateFlow(false)

    suspend fun refresh(effect: EffectHandler<PeopleEffect>) {
        loading.emit(true)
        val result = peopleUseCase.refreshPeople()
        if (result.isFailure) {
            effect.handleEffect(ErrorLoadingPeople)
        }
        loading.emit(false)
    }

}
