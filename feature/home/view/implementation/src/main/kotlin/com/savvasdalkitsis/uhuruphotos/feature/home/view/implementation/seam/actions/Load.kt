package com.savvasdalkitsis.uhuruphotos.feature.home.view.implementation.seam.actions

import com.savvasdalkitsis.uhuruphotos.feature.auth.domain.api.model.AuthStatus
import com.savvasdalkitsis.uhuruphotos.feature.home.view.implementation.seam.HomeActionsContext
import com.savvasdalkitsis.uhuruphotos.feature.home.view.implementation.seam.HomeEffect
import com.savvasdalkitsis.uhuruphotos.feature.home.view.implementation.seam.HomeMutation
import com.savvasdalkitsis.uhuruphotos.feature.home.view.implementation.ui.state.HomeState
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.EffectHandler
import com.savvasdalkitsis.uhuruphotos.foundation.strings.api.R
import kotlinx.coroutines.flow.flow

object Load : HomeAction() {

    context(HomeActionsContext) override fun handle(
        state: HomeState,
        effect: EffectHandler<HomeEffect>
    ) = flow {
        emit(HomeMutation.Loading)
        val proceed = when {
            settingsUseCase.getBiometricsRequiredForAppAccess() -> biometricsUseCase.authenticate(
                R.string.authenticate,
                R.string.authenticate_for_access,
                R.string.authenticate_for_access_description,
                true,
            )
            else -> Result.success(Unit)
        }
        when {
            proceed.isFailure -> emit(HomeMutation.NeedsBiometricAuthentication)
            else -> when (authenticationUseCase.authenticationStatus()) {
                is AuthStatus.Unauthenticated -> effect.handleEffect(HomeEffect.LaunchAuthentication)
                else -> effect.handleEffect(HomeEffect.LoadFeed)
            }
        }
    }
}