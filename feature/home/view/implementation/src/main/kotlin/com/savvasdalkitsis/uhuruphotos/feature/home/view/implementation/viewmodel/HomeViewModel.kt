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
package com.savvasdalkitsis.uhuruphotos.feature.home.view.implementation.viewmodel

import androidx.lifecycle.viewModelScope
import com.savvasdalkitsis.uhuruphotos.feature.home.view.api.navigation.HomeNavigationRoute
import com.savvasdalkitsis.uhuruphotos.feature.home.view.implementation.seam.HomeActionsContext
import com.savvasdalkitsis.uhuruphotos.feature.home.view.implementation.seam.HomeEffectsContext
import com.savvasdalkitsis.uhuruphotos.feature.home.view.implementation.seam.actions.HomeAction
import com.savvasdalkitsis.uhuruphotos.feature.home.view.implementation.seam.actions.Load
import com.savvasdalkitsis.uhuruphotos.feature.home.view.implementation.seam.effects.HomeEffect
import com.savvasdalkitsis.uhuruphotos.feature.home.view.implementation.ui.state.HomeState
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.viewmodel.NavigationViewModel
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.ActionHandlerWithContext
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.EffectHandlerWithContext
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class HomeViewModel @Inject constructor(
    homeActionsContext: HomeActionsContext,
    homeEffectsContext: HomeEffectsContext,
) : NavigationViewModel<HomeState, HomeEffect, HomeAction, HomeNavigationRoute>(
    ActionHandlerWithContext(homeActionsContext),
    EffectHandlerWithContext(homeEffectsContext),
    HomeState()
) {

    init {
        viewModelScope.launch {
            action(Load)
        }
    }
}