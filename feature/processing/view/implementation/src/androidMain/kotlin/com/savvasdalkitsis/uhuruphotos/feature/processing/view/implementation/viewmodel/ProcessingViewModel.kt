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
package com.savvasdalkitsis.uhuruphotos.feature.processing.view.implementation.viewmodel

import com.savvasdalkitsis.uhuruphotos.feature.processing.view.api.navigation.ProcessingNavigationRoute
import com.savvasdalkitsis.uhuruphotos.feature.processing.view.implementation.module.ProcessingModule
import com.savvasdalkitsis.uhuruphotos.feature.processing.view.implementation.seam.ProcessingActionsContext
import com.savvasdalkitsis.uhuruphotos.feature.processing.view.implementation.seam.actions.Load
import com.savvasdalkitsis.uhuruphotos.feature.processing.view.implementation.seam.actions.ProcessingAction
import com.savvasdalkitsis.uhuruphotos.feature.processing.view.implementation.ui.state.ProcessingState
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.viewmodel.NavigationViewModel
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.ActionHandlerWithContext

internal class ProcessingViewModel(
    actionsContext: ProcessingActionsContext = ProcessingModule.processingActionsContext,
) : NavigationViewModel<ProcessingState, ProcessingAction, ProcessingNavigationRoute>(
    ActionHandlerWithContext(actionsContext),
    ProcessingState(),
) {

    override fun onRouteSet(route: ProcessingNavigationRoute) {
        action(Load)
    }
}