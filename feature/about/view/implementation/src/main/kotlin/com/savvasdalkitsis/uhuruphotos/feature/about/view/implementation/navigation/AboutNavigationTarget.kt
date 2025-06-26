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
package com.savvasdalkitsis.uhuruphotos.feature.about.view.implementation.navigation

import androidx.compose.animation.ExperimentalSharedTransitionApi
import com.savvasdalkitsis.uhuruphotos.feature.about.view.api.navigation.AboutNavigationRoute
import com.savvasdalkitsis.uhuruphotos.feature.about.view.implementation.ui.About
import com.savvasdalkitsis.uhuruphotos.feature.about.view.implementation.viewmodel.AboutViewModel
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.NavigationTarget
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.ViewModelNavigationTarget
import se.ansman.dagger.auto.AutoInitialize
import javax.inject.Inject
import javax.inject.Singleton

@OptIn(ExperimentalSharedTransitionApi::class)
@AutoInitialize
@Singleton
class AboutNavigationTarget @Inject constructor(
) : NavigationTarget<AboutNavigationRoute> by ViewModelNavigationTarget(
    AboutViewModel::class,
    AboutNavigationRoute::class,
    view = { state, action ->
        About(state = state, action = action)
    }
)