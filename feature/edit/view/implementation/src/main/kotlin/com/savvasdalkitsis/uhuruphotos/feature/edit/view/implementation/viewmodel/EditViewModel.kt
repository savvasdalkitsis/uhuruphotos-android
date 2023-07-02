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
package com.savvasdalkitsis.uhuruphotos.feature.edit.view.implementation.viewmodel

import android.net.Uri
import com.savvasdalkitsis.uhuruphotos.feature.edit.view.api.navigation.EditNavigationRoute
import com.savvasdalkitsis.uhuruphotos.feature.edit.view.implementation.seam.EditActionsContext
import com.savvasdalkitsis.uhuruphotos.feature.edit.view.implementation.seam.EditEffectsContext
import com.savvasdalkitsis.uhuruphotos.feature.edit.view.implementation.seam.actions.EditAction
import com.savvasdalkitsis.uhuruphotos.feature.edit.view.implementation.seam.actions.Load
import com.savvasdalkitsis.uhuruphotos.feature.edit.view.implementation.seam.effects.EditEffect
import com.savvasdalkitsis.uhuruphotos.feature.edit.view.implementation.ui.state.EditState
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.viewmodel.NavigationViewModel
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.ActionHandlerWithContext
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.EffectHandlerWithContext
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class EditViewModel @Inject constructor(
    actionsContext: EditActionsContext,
    effectsContext: EditEffectsContext,
) : NavigationViewModel<EditState, EditEffect, EditAction, EditNavigationRoute>(
    ActionHandlerWithContext(actionsContext),
    EffectHandlerWithContext(effectsContext),
    EditState(),
) {

    override fun onRouteSet(route: EditNavigationRoute) {
        with(route) {
            action(Load(Uri.parse(uri), fileName, timestamp))
        }
    }
}