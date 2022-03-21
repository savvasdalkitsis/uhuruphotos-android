package com.savvasdalkitsis.librephotos.weblogin.viewmodel

import com.savvasdalkitsis.librephotos.weblogin.mvflow.WebLoginAction
import com.savvasdalkitsis.librephotos.weblogin.mvflow.WebLoginEffect
import com.savvasdalkitsis.librephotos.weblogin.mvflow.WebLoginMutation
import com.savvasdalkitsis.librephotos.weblogin.mvflow.WebLoginMutation.LoadPage
import com.savvasdalkitsis.librephotos.weblogin.mvflow.WebLoginMutation.Loading
import com.savvasdalkitsis.librephotos.weblogin.view.WebLoginState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import net.pedroloureiro.mvflow.EffectSender
import net.pedroloureiro.mvflow.HandlerWithEffects
import javax.inject.Inject

class WebLoginHandler @Inject constructor(
) : HandlerWithEffects<WebLoginState, WebLoginAction, WebLoginMutation, WebLoginEffect> {

    override fun invoke(
        state: WebLoginState,
        action: WebLoginAction,
        effect: EffectSender<WebLoginEffect>
    ): Flow<WebLoginMutation> = when(action) {
        is WebLoginAction.LoadPage -> flowOf(LoadPage(action.url))
        WebLoginAction.Loading -> flowOf(Loading)
    }
}