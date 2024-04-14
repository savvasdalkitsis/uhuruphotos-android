/*
Copyright 2023 Savvas Dalkitsis

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
package com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.actions

import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.LightboxActionsContext
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.LightboxMutation
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.ui.state.LightboxState
import com.savvasdalkitsis.uhuruphotos.feature.people.view.api.ui.state.Person
import com.savvasdalkitsis.uhuruphotos.feature.person.view.api.navigation.PersonNavigationRoute
import kotlinx.coroutines.flow.flow

data class PersonSelected(val person: Person) : LightboxAction() {

    context(LightboxActionsContext) override fun handle(
        state: LightboxState
    ) = flow<LightboxMutation> {
        navigator.navigateTo(PersonNavigationRoute(person.id))
    }
}
