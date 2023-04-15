package com.savvasdalkitsis.uhuruphotos.feature.auth.view.implementation.seam.actions

import com.savvasdalkitsis.uhuruphotos.feature.auth.view.implementation.seam.WebLoginActionsContext
import com.savvasdalkitsis.uhuruphotos.feature.auth.view.implementation.seam.WebLoginEffect
import com.savvasdalkitsis.uhuruphotos.feature.auth.view.implementation.seam.WebLoginMutation
import com.savvasdalkitsis.uhuruphotos.feature.auth.view.implementation.ui.WebLoginState
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.Action

sealed class WebLoginAction :
    Action<WebLoginState, WebLoginEffect, WebLoginMutation, WebLoginActionsContext>