package com.savvasdalkitsis.librephotos.weblogin.viewmodel

import com.savvasdalkitsis.librephotos.auth.cookies.CookieMonitor
import com.savvasdalkitsis.librephotos.coroutines.onMain
import com.savvasdalkitsis.librephotos.viewmodel.Handler
import com.savvasdalkitsis.librephotos.weblogin.mvflow.WebLoginAction
import com.savvasdalkitsis.librephotos.weblogin.mvflow.WebLoginEffect
import com.savvasdalkitsis.librephotos.weblogin.mvflow.WebLoginEffect.Close
import com.savvasdalkitsis.librephotos.weblogin.mvflow.WebLoginMutation
import com.savvasdalkitsis.librephotos.weblogin.mvflow.WebLoginMutation.LoadPage
import com.savvasdalkitsis.librephotos.weblogin.mvflow.WebLoginMutation.Loading
import com.savvasdalkitsis.librephotos.weblogin.view.WebLoginState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import javax.inject.Inject

class WebLoginHandler @Inject constructor(
    private val cookieMonitor: CookieMonitor,
): Handler<WebLoginState, WebLoginEffect, WebLoginAction,WebLoginMutation>  {

    override fun invoke(
        state: WebLoginState,
        action: WebLoginAction,
        effect: suspend (WebLoginEffect) -> Unit,
    ): Flow<WebLoginMutation> =
        when(action) {
            is WebLoginAction.LoadPage -> flow{
                emit(Loading)
                cookieMonitor.monitor().invokeOnCompletion {
                    onMain {
                        effect(Close)
                    }
                }

                emit(LoadPage(action.url))
            }
        }
}