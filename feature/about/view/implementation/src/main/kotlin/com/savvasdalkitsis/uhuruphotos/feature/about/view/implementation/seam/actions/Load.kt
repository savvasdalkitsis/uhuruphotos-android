package com.savvasdalkitsis.uhuruphotos.feature.about.view.implementation.seam.actions

import com.savvasdalkitsis.uhuruphotos.feature.about.view.implementation.seam.AboutActionsContext
import com.savvasdalkitsis.uhuruphotos.feature.about.view.implementation.seam.AboutMutation.DisplayAppVersion
import com.savvasdalkitsis.uhuruphotos.feature.about.view.implementation.seam.effects.AboutEffect
import com.savvasdalkitsis.uhuruphotos.feature.about.view.implementation.ui.state.AboutState
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.EffectHandler
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.Mutation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

data object Load : AboutAction() {

    context(AboutActionsContext) override fun handle(
        state: AboutState,
        effect: EffectHandler<AboutEffect>
    ): Flow<Mutation<AboutState>> = flowOf(DisplayAppVersion(applicationUseCase.appVersion()))
}