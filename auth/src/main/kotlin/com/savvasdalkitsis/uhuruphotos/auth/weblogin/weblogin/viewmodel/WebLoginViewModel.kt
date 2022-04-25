package com.savvasdalkitsis.uhuruphotos.auth.weblogin.weblogin.viewmodel

import androidx.lifecycle.ViewModel
import com.savvasdalkitsis.uhuruphotos.auth.weblogin.weblogin.mvflow.WebLoginAction
import com.savvasdalkitsis.uhuruphotos.auth.weblogin.weblogin.mvflow.WebLoginEffect
import com.savvasdalkitsis.uhuruphotos.auth.weblogin.weblogin.mvflow.WebLoginMutation
import com.savvasdalkitsis.uhuruphotos.auth.weblogin.weblogin.view.WebLoginState
import com.savvasdalkitsis.uhuruphotos.viewmodel.ActionReceiver
import com.savvasdalkitsis.uhuruphotos.viewmodel.ActionReceiverHost
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class WebLoginViewModel @Inject constructor(
    handler: WebLoginHandler,
) : ViewModel(),
    ActionReceiverHost<WebLoginState, WebLoginEffect, WebLoginAction, WebLoginMutation> {

    override val actionReceiver = ActionReceiver(
        handler,
        webLoginReducer(),
        WebLoginState(""),
    )
}