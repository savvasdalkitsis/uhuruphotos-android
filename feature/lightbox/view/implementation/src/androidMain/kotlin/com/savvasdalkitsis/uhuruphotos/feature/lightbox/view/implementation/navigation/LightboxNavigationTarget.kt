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
package com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.navigation

import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.api.navigation.LightboxNavigationRoute
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.ui.Lightbox
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.viewmodel.LightboxViewModel
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.NavigationTarget
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.ViewModelNavigationTarget
import com.savvasdalkitsis.uhuruphotos.foundation.theme.api.ThemeMode

object LightboxNavigationTarget : NavigationTarget<LightboxNavigationRoute> by ViewModelNavigationTarget(
    LightboxViewModel::class,
    LightboxNavigationRoute::class,
    theme = { ThemeMode.DARK_MODE },
    view = { state, action ->
        Lightbox(state, action)
    }
)