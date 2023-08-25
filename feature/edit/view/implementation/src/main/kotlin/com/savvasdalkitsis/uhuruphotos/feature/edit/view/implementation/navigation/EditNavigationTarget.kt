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
package com.savvasdalkitsis.uhuruphotos.feature.edit.view.implementation.navigation

import androidx.compose.runtime.Composable
import com.savvasdalkitsis.uhuruphotos.feature.edit.view.api.navigation.EditNavigationRoute
import com.savvasdalkitsis.uhuruphotos.feature.edit.view.implementation.ui.Edit
import com.savvasdalkitsis.uhuruphotos.feature.edit.view.implementation.viewmodel.EditViewModel
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.NavigationTarget
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.NavigationTargetBuilder
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.NavigationTargetRegistry
import com.savvasdalkitsis.uhuruphotos.foundation.theme.api.ThemeMode
import kotlinx.coroutines.flow.MutableStateFlow
import se.ansman.dagger.auto.AutoInitialize
import javax.inject.Inject
import javax.inject.Singleton

@AutoInitialize
@Singleton
internal class EditNavigationTarget @Inject constructor(
    private val navigationTargetBuilder: NavigationTargetBuilder,
    registry: NavigationTargetRegistry,
) : NavigationTarget<EditNavigationRoute>(EditNavigationRoute::class, registry) {

    @Composable
    override fun View(route: EditNavigationRoute) = with(navigationTargetBuilder) {
        ViewModelView(
            themeMode = MutableStateFlow(ThemeMode.DARK_MODE),
            route = route,
            viewModel = EditViewModel::class,
        ) { state, actions ->
            Edit(
                state,
                actions,
            )
        }
    }
}
