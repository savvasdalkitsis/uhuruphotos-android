package com.savvasdalkitsis.uhuruphotos.feature.auth.view.implementation.seam.actions

import com.savvasdalkitsis.uhuruphotos.feature.auth.view.implementation.seam.WebLoginActionsContext
import com.savvasdalkitsis.uhuruphotos.feature.auth.view.implementation.seam.WebLoginEffect
import com.savvasdalkitsis.uhuruphotos.feature.auth.view.implementation.seam.WebLoginMutation
import com.savvasdalkitsis.uhuruphotos.feature.auth.view.implementation.ui.WebLoginState
import com.savvasdalkitsis.uhuruphotos.foundation.launchers.api.onMain
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.EffectHandler
import kotlinx.coroutines.flow.flow
import kotlin.coroutines.coroutineContext

data class LoadPage(val url: String): WebLoginAction() {
    context(WebLoginActionsContext) override fun handle(
        state: WebLoginState,
        effect: EffectHandler<WebLoginEffect>
    ) = flow {
        emit(WebLoginMutation.Loading)
        cookieMonitor.monitor(coroutineContext).invokeOnCompletion {
            onMain {
                effect.handleEffect(WebLoginEffect.Close)
            }
        }

        emit(WebLoginMutation.LoadPage(url))
    }
}