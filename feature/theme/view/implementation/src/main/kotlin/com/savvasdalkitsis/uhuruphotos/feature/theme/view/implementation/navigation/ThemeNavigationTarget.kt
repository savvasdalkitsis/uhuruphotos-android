/*
Copyright 2024 Savvas Dalkitsis

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
package com.savvasdalkitsis.uhuruphotos.feature.theme.view.implementation.navigation

import androidx.compose.animation.ExperimentalSharedTransitionApi
import com.savvasdalkitsis.uhuruphotos.feature.theme.view.api.navigation.ThemeNavigationRoute
import com.savvasdalkitsis.uhuruphotos.feature.theme.view.implementation.ui.Theme
import com.savvasdalkitsis.uhuruphotos.feature.theme.view.implementation.viewmodel.ThemeViewModel
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.NavigationTarget
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.ViewModelNavigationTarget
import se.ansman.dagger.auto.AutoInitialize
import javax.inject.Inject
import javax.inject.Singleton

@OptIn(ExperimentalSharedTransitionApi::class)
@AutoInitialize
@Singleton
class ThemeNavigationTarget @Inject constructor(
) : NavigationTarget<ThemeNavigationRoute> by ViewModelNavigationTarget(
    ThemeViewModel::class,
    ThemeNavigationRoute::class,
    view = { state, action ->
        Theme(state, action)
    }
)