package com.savvasdalkitsis.librephotos.auth.weblogin.weblogin.viewmodel

import com.savvasdalkitsis.librephotos.auth.weblogin.weblogin.mvflow.WebLoginMutation
import com.savvasdalkitsis.librephotos.auth.weblogin.weblogin.view.WebLoginState

fun webLoginReducer() : com.savvasdalkitsis.librephotos.viewmodel.Reducer<WebLoginState, WebLoginMutation> = { state, mutation ->
    when (mutation) {
        is WebLoginMutation.LoadPage -> state.copy(url = mutation.url)
        WebLoginMutation.Loading -> state.copy(url = null)
    }
}