package com.savvasdalkitsis.librephotos.weblogin.viewmodel

import androidx.lifecycle.ViewModel
import com.savvasdalkitsis.librephotos.viewmodel.ActionReceiver
import com.savvasdalkitsis.librephotos.viewmodel.ActionReceiverHost
import com.savvasdalkitsis.librephotos.weblogin.mvflow.WebLoginAction
import com.savvasdalkitsis.librephotos.weblogin.mvflow.WebLoginEffect
import com.savvasdalkitsis.librephotos.weblogin.mvflow.WebLoginMutation
import com.savvasdalkitsis.librephotos.weblogin.view.WebLoginState
import dagger.hilt.android.lifecycle.HiltViewModel
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class WebLoginViewModel @Inject constructor(
    handler: WebLoginHandler,
) : ViewModel(), ActionReceiverHost<WebLoginState, WebLoginEffect, WebLoginAction, WebLoginMutation> {

    override val initialState = WebLoginState("")

    override val actionReceiver = ActionReceiver(
        handler,
        webLoginReducer(),
        container(initialState)
    )
}