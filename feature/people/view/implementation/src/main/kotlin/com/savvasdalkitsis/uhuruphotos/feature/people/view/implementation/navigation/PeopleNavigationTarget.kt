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
package com.savvasdalkitsis.uhuruphotos.feature.people.view.implementation.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import com.savvasdalkitsis.uhuruphotos.feature.people.view.api.navigation.PeopleNavigationTarget
import com.savvasdalkitsis.uhuruphotos.feature.people.view.implementation.seam.actions.PeopleAction
import com.savvasdalkitsis.uhuruphotos.feature.people.view.implementation.seam.actions.LoadPeople
import com.savvasdalkitsis.uhuruphotos.feature.people.view.implementation.ui.People
import com.savvasdalkitsis.uhuruphotos.feature.people.view.implementation.ui.state.PeopleState
import com.savvasdalkitsis.uhuruphotos.feature.people.view.implementation.viewmodel.PeopleViewModel
import com.savvasdalkitsis.uhuruphotos.feature.settings.domain.api.usecase.SettingsUseCase
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.NavigationTarget
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.navigationTarget
import javax.inject.Inject

class PeopleNavigationTarget @Inject constructor(
    private val settingsUseCase: SettingsUseCase,
) : NavigationTarget {

    override suspend fun NavGraphBuilder.create(navHostController: NavHostController) {
        navigationTarget<PeopleState, PeopleAction, PeopleViewModel>(
            route = PeopleNavigationTarget.name,
            themeMode = settingsUseCase.observeThemeModeState(),
            initializer = { _, action -> action(LoadPeople) },
        ) { state, actions ->
            People(state, actions)
        }
    }
}