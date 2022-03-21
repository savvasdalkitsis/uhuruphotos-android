package com.savvasdalkitsis.librephotos.weblogin.viewmodel

import com.savvasdalkitsis.librephotos.viewmodel.MVFlowViewModel
import com.savvasdalkitsis.librephotos.weblogin.mvflow.WebLoginAction
import com.savvasdalkitsis.librephotos.weblogin.mvflow.WebLoginEffect
import com.savvasdalkitsis.librephotos.weblogin.mvflow.WebLoginMutation
import com.savvasdalkitsis.librephotos.weblogin.view.WebLoginState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class WebLoginViewModel @Inject constructor(
    handler: WebLoginHandler,
    reducer: WebLoginReducer,
) : MVFlowViewModel<WebLoginState, WebLoginAction, WebLoginMutation, WebLoginEffect>(
    handler,
    reducer,
    WebLoginState(""),
    WebLoginAction.Loading
)