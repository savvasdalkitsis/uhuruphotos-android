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
package com.savvasdalkitsis.uhuruphotos.feature.sync.view.implementation.viewmodel

import com.savvasdalkitsis.uhuruphotos.feature.sync.view.api.navigation.SyncNavigationRoute
import com.savvasdalkitsis.uhuruphotos.feature.sync.view.implementation.seam.SyncActionsContext
import com.savvasdalkitsis.uhuruphotos.feature.sync.view.implementation.seam.actions.SyncAction
import com.savvasdalkitsis.uhuruphotos.feature.sync.view.implementation.ui.state.SyncState
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.viewmodel.NavigationViewModel
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.ActionHandlerWithContext
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class SyncViewModel @Inject constructor(
    actionsContext: SyncActionsContext,
) : NavigationViewModel<SyncState, SyncAction, SyncNavigationRoute>(
    ActionHandlerWithContext(actionsContext),
    SyncState(),
) {

    override fun onRouteSet(route: SyncNavigationRoute) {
    }
}