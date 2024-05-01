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
import com.savvasdalkitsis.uhuruphotos.feature.server.view.implementation.module.ServerModule
import com.savvasdalkitsis.uhuruphotos.feature.server.view.implementation.seam.ServerActionsContext
import com.savvasdalkitsis.uhuruphotos.feature.server.view.implementation.seam.actions.Load
import com.savvasdalkitsis.uhuruphotos.feature.server.view.implementation.seam.actions.ServerAction
import com.savvasdalkitsis.uhuruphotos.feature.server.view.implementation.ui.ServerState
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.viewmodel.NavigationViewModel
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.ActionHandlerWithContext

internal class ServerViewModel(
    serverActionsContext: ServerActionsContext = ServerModule.serverActionsContext,
) : NavigationViewModel<ServerState, ServerAction, ServerNavigationRoute>(
    ActionHandlerWithContext(serverActionsContext),
    ServerState()
) {

    override fun onRouteSet(route: ServerNavigationRoute) {
        action(Load)
    }
}