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
package com.savvasdalkitsis.uhuruphotos.feature.auth.view.implementation.viewmodel

import androidx.lifecycle.ViewModel
import com.savvasdalkitsis.uhuruphotos.feature.auth.view.api.navigation.WebLoginNavigationRoute
import com.savvasdalkitsis.uhuruphotos.feature.auth.view.implementation.seam.WebEffectsContext
import com.savvasdalkitsis.uhuruphotos.feature.auth.view.implementation.seam.WebLoginActionsContext
import com.savvasdalkitsis.uhuruphotos.feature.auth.view.implementation.seam.actions.LoadPage
import com.savvasdalkitsis.uhuruphotos.feature.auth.view.implementation.seam.actions.WebLoginAction
import com.savvasdalkitsis.uhuruphotos.feature.auth.view.implementation.ui.WebLoginState
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.HasInitializer
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.ActionHandlerWithContext
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.EffectHandlerWithContext
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.HasActionableState
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.Seam
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class WebLoginViewModel @Inject constructor(
    webLoginActionsContext: WebLoginActionsContext,
    webEffectsContext: WebEffectsContext,
) : ViewModel(), HasActionableState<WebLoginState, WebLoginAction> by Seam(
    ActionHandlerWithContext(webLoginActionsContext),
    EffectHandlerWithContext(webEffectsContext),
    WebLoginState("")
), HasInitializer<WebLoginAction, WebLoginNavigationRoute> {
    override suspend fun initialize(initializerData: WebLoginNavigationRoute, action: (WebLoginAction) -> Unit) {
        action(LoadPage(initializerData.url))
    }
}