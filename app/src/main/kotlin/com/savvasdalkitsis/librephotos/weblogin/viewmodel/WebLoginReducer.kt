package com.savvasdalkitsis.librephotos.weblogin.viewmodel

import com.savvasdalkitsis.librephotos.weblogin.mvflow.WebLoginMutation
import com.savvasdalkitsis.librephotos.weblogin.view.WebLoginState
import net.pedroloureiro.mvflow.Reducer
import javax.inject.Inject

class WebLoginReducer @Inject constructor(
) : Reducer<WebLoginState, WebLoginMutation> {

    override fun invoke(
        state: WebLoginState,
        mutation: WebLoginMutation
    ): WebLoginState = when (mutation) {
        is WebLoginMutation.LoadPage -> state.copy(url = mutation.url)
        WebLoginMutation.Loading -> state.copy(url = null)
    }
}