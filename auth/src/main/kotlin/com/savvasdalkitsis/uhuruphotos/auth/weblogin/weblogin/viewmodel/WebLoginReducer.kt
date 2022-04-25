package com.savvasdalkitsis.uhuruphotos.auth.weblogin.weblogin.viewmodel

import com.savvasdalkitsis.uhuruphotos.auth.weblogin.weblogin.mvflow.WebLoginMutation
import com.savvasdalkitsis.uhuruphotos.auth.weblogin.weblogin.view.WebLoginState

fun webLoginReducer() : com.savvasdalkitsis.uhuruphotos.viewmodel.Reducer<WebLoginState, WebLoginMutation> = { state, mutation ->
    when (mutation) {
        is WebLoginMutation.LoadPage -> state.copy(url = mutation.url)
        WebLoginMutation.Loading -> state.copy(url = null)
    }
}