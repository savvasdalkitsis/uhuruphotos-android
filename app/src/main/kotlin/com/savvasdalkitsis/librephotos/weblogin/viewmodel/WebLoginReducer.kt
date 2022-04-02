package com.savvasdalkitsis.librephotos.weblogin.viewmodel

import com.savvasdalkitsis.librephotos.viewmodel.Reducer
import com.savvasdalkitsis.librephotos.weblogin.mvflow.WebLoginMutation
import com.savvasdalkitsis.librephotos.weblogin.view.WebLoginState

fun webLoginReducer() : Reducer<WebLoginState, WebLoginMutation> = { state, mutation ->
    when (mutation) {
        is WebLoginMutation.LoadPage -> state.copy(url = mutation.url)
        WebLoginMutation.Loading -> state.copy(url = null)
    }
}