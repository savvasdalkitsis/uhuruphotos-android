package com.savvasdalkitsis.uhuruphotos.feature.welcome.view.implementation.seam.actions

import com.savvasdalkitsis.uhuruphotos.feature.welcome.view.implementation.seam.WelcomeActionsContext
import com.savvasdalkitsis.uhuruphotos.feature.welcome.view.implementation.seam.WelcomeMutation
import com.savvasdalkitsis.uhuruphotos.feature.welcome.view.implementation.ui.state.WelcomeState
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.Mutation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

data object ShowNeedsAccess : WelcomeAction() {
    context(WelcomeActionsContext)
    override fun handle(state: WelcomeState): Flow<Mutation<WelcomeState>> = flowOf(
        WelcomeMutation.DisplayNeedAccessDialog(true)
    )
}