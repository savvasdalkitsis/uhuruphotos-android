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
package com.savvasdalkitsis.uhuruphotos.feature.{{cookiecutter.project_slug}}.view.implementation.viewmodel

import androidx.lifecycle.viewModelScope
import com.savvasdalkitsis.uhuruphotos.feature.{{cookiecutter.project_slug}}.view.api.navigation.{{cookiecutter.project_slug.capitalize()}}NavigationRoute
import com.savvasdalkitsis.uhuruphotos.feature.{{cookiecutter.project_slug}}.view.implementation.seam.{{cookiecutter.project_slug.capitalize()}}ActionsContext
import com.savvasdalkitsis.uhuruphotos.feature.{{cookiecutter.project_slug}}.view.implementation.seam.actions.{{cookiecutter.project_slug.capitalize()}}Action
import com.savvasdalkitsis.uhuruphotos.feature.{{cookiecutter.project_slug}}.view.implementation.ui.state.{{cookiecutter.project_slug.capitalize()}}State
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.viewmodel.NavigationViewModel
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.ActionHandlerWithContext
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.EffectHandlerWithContext
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class {{cookiecutter.project_slug.capitalize()}}ViewModel @Inject constructor(
    actionsContext: {{cookiecutter.project_slug.capitalize()}}ActionsContext,
) : NavigationViewModel<{{cookiecutter.project_slug.capitalize()}}State, {{cookiecutter.project_slug.capitalize()}}Action, {{cookiecutter.project_slug.capitalize()}}NavigationRoute>(
    ActionHandlerWithContext(actionsContext),
    {{cookiecutter.project_slug.capitalize()}}State(),
) {

    override fun onRouteSet(route: {{cookiecutter.project_slug.capitalize()}}NavigationRoute) {
    }
}