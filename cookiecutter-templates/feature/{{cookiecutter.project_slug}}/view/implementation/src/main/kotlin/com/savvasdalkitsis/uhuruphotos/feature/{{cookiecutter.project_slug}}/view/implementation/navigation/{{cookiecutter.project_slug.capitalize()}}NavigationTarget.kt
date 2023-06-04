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
package com.savvasdalkitsis.uhuruphotos.feature.{{cookiecutter.project_slug}}.view.implementation.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import com.savvasdalkitsis.uhuruphotos.feature.{{cookiecutter.project_slug}}.view.api.navigation.{{cookiecutter.project_slug.capitalize()}}NavigationRoute
import com.savvasdalkitsis.uhuruphotos.feature.{{cookiecutter.project_slug}}.view.implementation.ui.{{cookiecutter.project_slug.capitalize()}}
import com.savvasdalkitsis.uhuruphotos.feature.{{cookiecutter.project_slug}}.view.implementation.viewmodel.{{cookiecutter.project_slug.capitalize()}}ViewModel
import com.savvasdalkitsis.uhuruphotos.feature.settings.domain.api.usecase.SettingsUseCase
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.NavigationTarget
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.NavigationTargetBuilder
import se.ansman.dagger.auto.AutoBindIntoSet
import javax.inject.Inject

@AutoBindIntoSet
internal class {{cookiecutter.project_slug.capitalize()}}NavigationTarget @Inject constructor(
    private val settingsUseCase: SettingsUseCase,
    private val navigationTargetBuilder: NavigationTargetBuilder,
) : NavigationTarget {

    override suspend fun NavGraphBuilder.create(navHostController: NavHostController) = with(navigationTargetBuilder) {
        navigationTarget(
            themeMode = settingsUseCase.observeThemeModeState(),
            route = {{cookiecutter.project_slug.capitalize()}}NavigationRoute::class,
            viewModel = {{cookiecutter.project_slug.capitalize()}}ViewModel::class,
        ) { state, actions ->
            {{cookiecutter.project_slug.capitalize()}}(
                state,
                actions,
            )
        }
    }
}
