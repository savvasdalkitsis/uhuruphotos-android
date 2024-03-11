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
package com.savvasdalkitsis.uhuruphotos.feature.processing.view.implementation.navigation

import androidx.compose.runtime.Composable
import com.savvasdalkitsis.uhuruphotos.feature.processing.view.api.navigation.ProcessingNavigationRoute
import com.savvasdalkitsis.uhuruphotos.feature.processing.view.implementation.ui.Processing
import com.savvasdalkitsis.uhuruphotos.feature.processing.view.implementation.viewmodel.ProcessingViewModel
import com.savvasdalkitsis.uhuruphotos.feature.settings.domain.api.usecase.SettingsUIUseCase
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.NavigationTarget
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.NavigationTargetBuilder
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.NavigationTargetRegistry
import se.ansman.dagger.auto.AutoInitialize
import javax.inject.Inject
import javax.inject.Singleton

@AutoInitialize
@Singleton
internal class ProcessingNavigationTarget @Inject constructor(
    private val settingsUIUseCase: SettingsUIUseCase,
    private val navigationTargetBuilder: NavigationTargetBuilder,
    registry: NavigationTargetRegistry,
) : NavigationTarget<ProcessingNavigationRoute>(ProcessingNavigationRoute::class, registry) {

    @Composable
    override fun View(route: ProcessingNavigationRoute) = with(navigationTargetBuilder) {
        ViewModelView(
            settingsUIUseCase.observeThemeModeState(),
            route,
            ProcessingViewModel::class,
            scoped = true,
        ) { state, action ->
            Processing(
                state,
                action,
            )
        }
    }
}