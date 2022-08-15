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
package com.savvasdalkitsis.uhuruphotos.feature.person.view.implementation.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import com.savvasdalkitsis.uhuruphotos.api.settings.usecase.SettingsUseCase
import com.savvasdalkitsis.uhuruphotos.feature.person.view.api.navigation.PersonNavigationTarget
import com.savvasdalkitsis.uhuruphotos.feature.person.view.api.navigation.PersonNavigationTarget.personId
import com.savvasdalkitsis.uhuruphotos.feature.person.view.implementation.seam.PersonAction
import com.savvasdalkitsis.uhuruphotos.feature.person.view.implementation.seam.PersonAction.LoadPerson
import com.savvasdalkitsis.uhuruphotos.feature.person.view.implementation.seam.PersonEffect
import com.savvasdalkitsis.uhuruphotos.feature.person.view.implementation.seam.PersonEffectHandler
import com.savvasdalkitsis.uhuruphotos.feature.person.view.implementation.ui.Person
import com.savvasdalkitsis.uhuruphotos.feature.person.view.implementation.ui.state.PersonState
import com.savvasdalkitsis.uhuruphotos.feature.person.view.implementation.viewmodel.PersonViewModel
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.NavigationTarget
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.navigationTarget
import javax.inject.Inject

class PersonNavigationTarget @Inject constructor(
    private val settingsUseCase: SettingsUseCase,
    private val effectHandler: PersonEffectHandler,
) : NavigationTarget {

    override suspend fun NavGraphBuilder.create(navHostController: NavHostController) {
        navigationTarget<PersonState, PersonEffect, PersonAction, PersonViewModel>(
            name = PersonNavigationTarget.registrationName,
            themeMode = settingsUseCase.observeThemeModeState(),
            effects = effectHandler,
            initializer = { navBackStackEntry, action -> action(LoadPerson(navBackStackEntry.personId)) },
            createModel = { hiltViewModel() },
        ) { state, actions ->
            Person(state, actions)
        }
    }
}