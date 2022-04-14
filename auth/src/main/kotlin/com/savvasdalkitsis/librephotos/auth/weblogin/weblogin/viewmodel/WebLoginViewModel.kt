package com.savvasdalkitsis.librephotos.auth.weblogin.weblogin.viewmodel

import androidx.lifecycle.ViewModel
import com.savvasdalkitsis.librephotos.viewmodel.ActionReceiver
import com.savvasdalkitsis.librephotos.viewmodel.ActionReceiverHost
import com.savvasdalkitsis.librephotos.auth.weblogin.weblogin.mvflow.WebLoginAction
import com.savvasdalkitsis.librephotos.auth.weblogin.weblogin.mvflow.WebLoginEffect
import com.savvasdalkitsis.librephotos.auth.weblogin.weblogin.mvflow.WebLoginMutation
import com.savvasdalkitsis.librephotos.auth.weblogin.weblogin.view.WebLoginState
import dagger.hilt.android.lifecycle.HiltViewModel
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class WebLoginViewModel @Inject constructor(
    handler: WebLoginHandler,
) : ViewModel(),
    com.savvasdalkitsis.librephotos.viewmodel.ActionReceiverHost<WebLoginState, WebLoginEffect, WebLoginAction, WebLoginMutation> {

    override val initialState = WebLoginState("")

    override val actionReceiver = com.savvasdalkitsis.librephotos.viewmodel.ActionReceiver(
        handler,
        webLoginReducer(),
        container(initialState)
    )
}