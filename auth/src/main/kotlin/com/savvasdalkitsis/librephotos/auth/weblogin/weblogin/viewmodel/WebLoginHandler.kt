package com.savvasdalkitsis.librephotos.auth.weblogin.weblogin.viewmodel

import com.savvasdalkitsis.librephotos.auth.cookies.CookieMonitor
import com.savvasdalkitsis.librephotos.auth.weblogin.weblogin.mvflow.WebLoginAction
import com.savvasdalkitsis.librephotos.auth.weblogin.weblogin.mvflow.WebLoginEffect
import com.savvasdalkitsis.librephotos.auth.weblogin.weblogin.mvflow.WebLoginEffect.Close
import com.savvasdalkitsis.librephotos.auth.weblogin.weblogin.mvflow.WebLoginMutation
import com.savvasdalkitsis.librephotos.auth.weblogin.weblogin.mvflow.WebLoginMutation.LoadPage
import com.savvasdalkitsis.librephotos.auth.weblogin.weblogin.mvflow.WebLoginMutation.Loading
import com.savvasdalkitsis.librephotos.auth.weblogin.weblogin.view.WebLoginState
import com.savvasdalkitsis.librephotos.infrastructure.coroutines.onMain
import com.savvasdalkitsis.librephotos.viewmodel.Handler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import kotlin.coroutines.coroutineContext

class WebLoginHandler @Inject constructor(
    private val cookieMonitor: CookieMonitor,
): Handler<WebLoginState, WebLoginEffect, WebLoginAction, WebLoginMutation> {

    override fun invoke(
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