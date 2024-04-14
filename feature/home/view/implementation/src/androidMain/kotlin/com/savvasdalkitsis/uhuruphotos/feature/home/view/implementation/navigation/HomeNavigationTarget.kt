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
package com.savvasdalkitsis.uhuruphotos.feature.home.view.implementation.navigation

import com.savvasdalkitsis.uhuruphotos.feature.home.view.api.navigation.HomeNavigationRoute
import com.savvasdalkitsis.uhuruphotos.feature.home.view.implementation.ui.Home
import com.savvasdalkitsis.uhuruphotos.feature.home.view.implementation.viewmodel.HomeViewModel
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.NavigationTarget
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.ViewModelNavigationTarget
import se.ansman.dagger.auto.AutoInitialize
import javax.inject.Inject
import javax.inject.Singleton

@AutoInitialize
@Singleton
class HomeNavigationTarget @Inject constructor(
) : NavigationTarget<HomeNavigationRoute> by ViewModelNavigationTarget(
    HomeViewModel::class,
    HomeNavigationRoute::class,
    view = { state, action ->
        Home(state, action)
    }
)