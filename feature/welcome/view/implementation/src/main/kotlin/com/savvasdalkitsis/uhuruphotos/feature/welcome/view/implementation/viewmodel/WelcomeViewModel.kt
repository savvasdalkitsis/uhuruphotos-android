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
package com.savvasdalkitsis.uhuruphotos.feature.welcome.view.implementation.viewmodel

import androidx.lifecycle.SavedStateHandle
import com.savvasdalkitsis.uhuruphotos.feature.welcome.view.api.navigation.WelcomeNavigationRoute
import com.savvasdalkitsis.uhuruphotos.feature.welcome.view.implementation.seam.WelcomeActionsContext
import com.savvasdalkitsis.uhuruphotos.feature.welcome.view.implementation.seam.actions.Load
import com.savvasdalkitsis.uhuruphotos.feature.welcome.view.implementation.seam.actions.WelcomeAction
import com.savvasdalkitsis.uhuruphotos.feature.welcome.view.implementation.ui.state.WelcomeState
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.viewmodel.NavigationViewModel
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.ActionHandlerWithContext
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class WelcomeViewModel @Inject constructor(
    actionsContext: WelcomeActionsContext,
    handle: SavedStateHandle,
) : NavigationViewModel<WelcomeState, WelcomeAction, WelcomeNavigationRoute>(
    ActionHandlerWithContext(actionsContext),
    WelcomeState(),
    handle,
) {

    override fun onRouteSet(route: WelcomeNavigationRoute) {
        action(Load)
    }
}