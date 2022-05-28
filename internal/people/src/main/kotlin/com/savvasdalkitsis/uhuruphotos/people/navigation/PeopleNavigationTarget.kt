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
package com.savvasdalkitsis.uhuruphotos.people.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import com.savvasdalkitsis.uhuruphotos.navigation.NavigationTarget
import com.savvasdalkitsis.uhuruphotos.navigation.navigationTarget
import com.savvasdalkitsis.uhuruphotos.people.api.navigation.PeopleNavigationTarget
import com.savvasdalkitsis.uhuruphotos.people.view.People
import com.savvasdalkitsis.uhuruphotos.people.view.state.PeopleState
import com.savvasdalkitsis.uhuruphotos.people.viewmodel.PeopleAction
import com.savvasdalkitsis.uhuruphotos.people.viewmodel.PeopleAction.LoadPeople
import com.savvasdalkitsis.uhuruphotos.people.viewmodel.PeopleEffect
import com.savvasdalkitsis.uhuruphotos.people.viewmodel.PeopleEffectHandler
import com.savvasdalkitsis.uhuruphotos.people.viewmodel.PeopleViewModel
import com.savvasdalkitsis.uhuruphotos.api.settings.usecase.SettingsUseCase
import javax.inject.Inject

class PeopleNavigationTarget @Inject constructor(
    private val settingsUseCase: SettingsUseCase,
    private val effectHandler: PeopleEffectHandler,
) : NavigationTarget {

    override suspend fun NavGraphBuilder.create() {
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