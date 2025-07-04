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
package com.savvasdalkitsis.uhuruphotos.feature.theme.view.implementation.viewmodel

import androidx.lifecycle.SavedStateHandle
import com.savvasdalkitsis.uhuruphotos.feature.theme.view.api.navigation.ThemeNavigationRoute
import com.savvasdalkitsis.uhuruphotos.feature.theme.view.implementation.seam.ThemeActionsContext
import com.savvasdalkitsis.uhuruphotos.feature.theme.view.implementation.seam.actions.Load
import com.savvasdalkitsis.uhuruphotos.feature.theme.view.implementation.seam.actions.ThemeAction
import com.savvasdalkitsis.uhuruphotos.feature.theme.view.implementation.ui.state.ThemeState
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.viewmodel.NavigationViewModel
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.ActionHandlerWithContext
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class ThemeViewModel @Inject constructor(
    actionsContext: ThemeActionsContext,
    handle: SavedStateHandle,
) : NavigationViewModel<ThemeState, ThemeAction, ThemeNavigationRoute>(
    ActionHandlerWithContext(actionsContext),
    ThemeState(),
    handle,
) {

    override fun onRouteSet(route: ThemeNavigationRoute) {
        action(Load)
    }
}