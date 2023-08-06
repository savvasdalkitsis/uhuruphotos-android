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
package com.savvasdalkitsis.uhuruphotos.feature.server.view.implementation.viewmodel

import com.savvasdalkitsis.uhuruphotos.feature.server.view.api.navigation.ServerNavigationRoute
import com.savvasdalkitsis.uhuruphotos.feature.server.view.implementation.seam.ServerActionsContext
import com.savvasdalkitsis.uhuruphotos.feature.server.view.implementation.seam.actions.CheckPersistedServer
import com.savvasdalkitsis.uhuruphotos.feature.server.view.implementation.seam.actions.Load
import com.savvasdalkitsis.uhuruphotos.feature.server.view.implementation.seam.actions.RequestServerUrlChange
import com.savvasdalkitsis.uhuruphotos.feature.server.view.implementation.seam.actions.ServerAction
import com.savvasdalkitsis.uhuruphotos.feature.server.view.implementation.ui.ServerState
import com.savvasdalkitsis.uhuruphotos.foundation.effects.api.seam.effects.CommonEffect
import com.savvasdalkitsis.uhuruphotos.foundation.effects.api.seam.effects.CommonEffectHandler
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.viewmodel.NavigationViewModel
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.ActionHandlerWithContext
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class ServerViewModel @Inject constructor(
    serverActionsContext: ServerActionsContext,
    commonEffectHandler: CommonEffectHandler,
) : NavigationViewModel<ServerState, CommonEffect, ServerAction, ServerNavigationRoute>(
    ActionHandlerWithContext(serverActionsContext),
    commonEffectHandler,
    ServerState.Loading(false)
) {

    override fun onRouteSet(route: ServerNavigationRoute) {
        action(Load)
        action(
            when {
                route.auto -> CheckPersistedServer
                else -> RequestServerUrlChange
            }
        )
    }
}