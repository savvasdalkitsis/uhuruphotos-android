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
package com.savvasdalkitsis.uhuruphotos.feature.person.view.implementation.viewmodel

import com.savvasdalkitsis.uhuruphotos.feature.person.view.api.navigation.PersonNavigationRoute
import com.savvasdalkitsis.uhuruphotos.feature.person.view.implementation.seam.PersonActionsContext
import com.savvasdalkitsis.uhuruphotos.feature.person.view.implementation.seam.actions.LoadPerson
import com.savvasdalkitsis.uhuruphotos.feature.person.view.implementation.seam.actions.PersonAction
import com.savvasdalkitsis.uhuruphotos.feature.person.view.implementation.ui.state.PersonState
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.viewmodel.NavigationViewModel
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.ActionHandlerWithContext
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PersonViewModel @Inject constructor(
    personActionsContext: PersonActionsContext,
) : NavigationViewModel<PersonState, PersonAction, PersonNavigationRoute>(
    ActionHandlerWithContext(personActionsContext),
    PersonState()
) {

    override fun onRouteSet(route: PersonNavigationRoute) {
        action(LoadPerson(route.personId))
    }
}