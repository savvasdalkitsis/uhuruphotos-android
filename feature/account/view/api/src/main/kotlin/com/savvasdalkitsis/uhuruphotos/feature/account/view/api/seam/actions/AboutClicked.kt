package com.savvasdalkitsis.uhuruphotos.feature.account.view.api.seam.actions

import com.savvasdalkitsis.uhuruphotos.feature.account.view.api.seam.AccountOverviewActionsContext
import com.savvasdalkitsis.uhuruphotos.feature.account.view.api.seam.effects.AccountOverviewEffect
import com.savvasdalkitsis.uhuruphotos.feature.account.view.api.seam.effects.NavigateToAbout
import com.savvasdalkitsis.uhuruphotos.feature.account.view.api.ui.state.AccountOverviewState
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.EffectHandler
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.Mutation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

data object AboutClicked : AccountOverviewAction() {
    context(AccountOverviewActionsContext) override fun handle(
        state: AccountOverviewState,
        effect: EffectHandler<AccountOverviewEffect>
    ): Flow<Mutation<AccountOverviewState>> = flow {
        effect.handleEffect(NavigateToAbout)
    }

}