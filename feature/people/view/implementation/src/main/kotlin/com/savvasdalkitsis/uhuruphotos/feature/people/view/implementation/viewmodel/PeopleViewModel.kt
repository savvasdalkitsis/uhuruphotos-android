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

import com.savvasdalkitsis.uhuruphotos.feature.people.view.api.navigation.PeopleNavigationRoute
import com.savvasdalkitsis.uhuruphotos.feature.people.view.implementation.seam.PeopleActionsContext
import com.savvasdalkitsis.uhuruphotos.feature.people.view.implementation.seam.actions.LoadPeople
import com.savvasdalkitsis.uhuruphotos.feature.people.view.implementation.seam.actions.PeopleAction
import com.savvasdalkitsis.uhuruphotos.feature.people.view.implementation.ui.state.PeopleState
import com.savvasdalkitsis.uhuruphotos.foundation.effects.api.seam.effects.CommonEffect
import com.savvasdalkitsis.uhuruphotos.foundation.effects.api.seam.effects.CommonEffectHandler
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.viewmodel.NavigationViewModel
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.ActionHandlerWithContext
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PeopleViewModel @Inject constructor(
    peopleActionsContext: PeopleActionsContext,
    commonEffectHandler: CommonEffectHandler,
) : NavigationViewModel<PeopleState, CommonEffect, PeopleAction, PeopleNavigationRoute>(
    ActionHandlerWithContext(peopleActionsContext),
    commonEffectHandler,
    PeopleState()
) {

    override fun onRouteSet(route: PeopleNavigationRoute) {
        action(LoadPeople)
    }
}