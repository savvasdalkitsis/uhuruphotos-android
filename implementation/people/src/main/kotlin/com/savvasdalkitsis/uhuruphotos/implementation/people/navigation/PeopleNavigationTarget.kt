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
package com.savvasdalkitsis.uhuruphotos.implementation.people.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import com.savvasdalkitsis.uhuruphotos.api.navigation.NavigationTarget
import com.savvasdalkitsis.uhuruphotos.api.navigation.navigationTarget
import com.savvasdalkitsis.uhuruphotos.api.people.navigation.PeopleNavigationTarget
import com.savvasdalkitsis.uhuruphotos.api.settings.usecase.SettingsUseCase
import com.savvasdalkitsis.uhuruphotos.implementation.people.seam.PeopleAction
import com.savvasdalkitsis.uhuruphotos.implementation.people.seam.PeopleAction.LoadPeople
import com.savvasdalkitsis.uhuruphotos.implementation.people.seam.PeopleEffect
import com.savvasdalkitsis.uhuruphotos.implementation.people.seam.PeopleEffectHandler
import com.savvasdalkitsis.uhuruphotos.implementation.people.view.People
import com.savvasdalkitsis.uhuruphotos.implementation.people.view.state.PeopleState
import com.savvasdalkitsis.uhuruphotos.implementation.people.viewmodel.PeopleViewModel
import javax.inject.Inject

class PeopleNavigationTarget @Inject constructor(
    private val settingsUseCase: SettingsUseCase,
    private val effectHandler: PeopleEffectHandler,
) : NavigationTarget {

    override suspend fun NavGraphBuilder.create(navHostController: NavHostController) {
        navigationTarget<PeopleState, PeopleEffect, PeopleAction, PeopleViewModel>(
            name = PeopleNavigationTarget.name,
            themeMode = settingsUseCase.observeThemeModeState(),
            effects = effectHandler,
            initializer = { _, action -> action(LoadPeople) },
            createModel = { hiltViewModel() },
        ) { state, actions ->
            People(state, actions)
        }
    }
}