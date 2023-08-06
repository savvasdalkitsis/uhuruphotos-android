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
package com.savvasdalkitsis.uhuruphotos.feature.catalogue.auto.view.implementation.viewmodel

import com.savvasdalkitsis.uhuruphotos.feature.catalogue.auto.view.api.navigation.AutoAlbumsNavigationRoute
import com.savvasdalkitsis.uhuruphotos.feature.catalogue.auto.view.implementation.seam.AutoAlbumsActionsContext
import com.savvasdalkitsis.uhuruphotos.feature.catalogue.auto.view.implementation.seam.AutoAlbumsState
import com.savvasdalkitsis.uhuruphotos.feature.catalogue.auto.view.implementation.seam.actions.AutoAlbumsAction
import com.savvasdalkitsis.uhuruphotos.feature.catalogue.auto.view.implementation.seam.actions.Load
import com.savvasdalkitsis.uhuruphotos.foundation.effects.api.seam.effects.CommonEffect
import com.savvasdalkitsis.uhuruphotos.foundation.effects.api.seam.effects.CommonEffectHandler
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.viewmodel.NavigationViewModel
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.ActionHandlerWithContext
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class AutoAlbumsViewModel @Inject constructor(
    autoAlbumsActionsContext: AutoAlbumsActionsContext,
    commonEffectHandler: CommonEffectHandler,
) : NavigationViewModel<AutoAlbumsState, CommonEffect, AutoAlbumsAction, AutoAlbumsNavigationRoute>(
    ActionHandlerWithContext(autoAlbumsActionsContext),
    commonEffectHandler,
    AutoAlbumsState()
) {

    override fun onRouteSet(route: AutoAlbumsNavigationRoute) {
        action(Load)
    }
}