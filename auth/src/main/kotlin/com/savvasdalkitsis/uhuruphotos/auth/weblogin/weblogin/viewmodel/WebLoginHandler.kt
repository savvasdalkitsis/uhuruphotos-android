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
package com.savvasdalkitsis.uhuruphotos.auth.weblogin.weblogin.viewmodel

import com.savvasdalkitsis.uhuruphotos.auth.cookies.CookieMonitor
import com.savvasdalkitsis.uhuruphotos.auth.weblogin.weblogin.mvflow.WebLoginAction
import com.savvasdalkitsis.uhuruphotos.auth.weblogin.weblogin.mvflow.WebLoginEffect
import com.savvasdalkitsis.uhuruphotos.auth.weblogin.weblogin.mvflow.WebLoginEffect.Close
import com.savvasdalkitsis.uhuruphotos.auth.weblogin.weblogin.mvflow.WebLoginMutation
import com.savvasdalkitsis.uhuruphotos.auth.weblogin.weblogin.mvflow.WebLoginMutation.LoadPage
import com.savvasdalkitsis.uhuruphotos.auth.weblogin.weblogin.mvflow.WebLoginMutation.Loading
import com.savvasdalkitsis.uhuruphotos.auth.weblogin.weblogin.view.WebLoginState
import com.savvasdalkitsis.uhuruphotos.infrastructure.coroutines.onMain
import com.savvasdalkitsis.uhuruphotos.viewmodel.ActionHandler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import kotlin.coroutines.coroutineContext

class WebLoginHandler @Inject constructor(
    private val cookieMonitor: CookieMonitor,
) : ActionHandler<WebLoginState, WebLoginEffect, WebLoginAction, WebLoginMutation> {

    override fun handleAction(
        state: WebLoginState,
        action: WebLoginAction,
        effect: suspend (WebLoginEffect) -> Unit,
    ): Flow<WebLoginMutation> =
        when(action) {
            is WebLoginAction.LoadPage -> flow {
                emit(Loading)
                cookieMonitor.monitor(coroutineContext).invokeOnCompletion {
                    onMain {
                        effect(Close)
                    }
                }

                emit(LoadPage(action.url))
            }
        }
}