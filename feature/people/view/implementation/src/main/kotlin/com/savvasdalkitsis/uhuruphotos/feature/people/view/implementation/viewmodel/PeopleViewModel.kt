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
package com.savvasdalkitsis.uhuruphotos.feature.people.view.implementation.viewmodel

import androidx.lifecycle.ViewModel
import com.savvasdalkitsis.uhuruphotos.feature.people.view.api.navigation.PeopleNavigationRoute
import com.savvasdalkitsis.uhuruphotos.feature.people.view.implementation.seam.PeopleActionsContext
import com.savvasdalkitsis.uhuruphotos.feature.people.view.implementation.seam.PeopleEffectHandler
import com.savvasdalkitsis.uhuruphotos.feature.people.view.implementation.seam.actions.LoadPeople
import com.savvasdalkitsis.uhuruphotos.feature.people.view.implementation.seam.actions.PeopleAction
import com.savvasdalkitsis.uhuruphotos.feature.people.view.implementation.ui.state.PeopleState
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.ActionHandlerWithContext
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.HasActionableState
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.HasInitializer
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.Seam
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PeopleViewModel @Inject constructor(
    peopleActionsContext: PeopleActionsContext,
    effectHandler: PeopleEffectHandler,
) : ViewModel(), HasActionableState<PeopleState, PeopleAction> by Seam(
    ActionHandlerWithContext(peopleActionsContext),
    effectHandler,
    PeopleState()
), HasInitializer<PeopleNavigationRoute> {
    override suspend fun initialize(initializerData: PeopleNavigationRoute) {
        action(LoadPeople)
    }
}