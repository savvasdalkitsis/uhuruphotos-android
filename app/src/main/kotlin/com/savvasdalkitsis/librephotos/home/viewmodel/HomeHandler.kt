package com.savvasdalkitsis.librephotos.home.viewmodel

import com.savvasdalkitsis.librephotos.auth.model.AuthStatus
import com.savvasdalkitsis.librephotos.auth.usecase.AuthenticationUseCase
import com.savvasdalkitsis.librephotos.home.mvflow.HomeAction
import com.savvasdalkitsis.librephotos.home.mvflow.HomeAction.Load
import com.savvasdalkitsis.librephotos.home.mvflow.HomeEffect
import com.savvasdalkitsis.librephotos.home.mvflow.HomeEffect.LaunchAuthentication
import com.savvasdalkitsis.librephotos.home.mvflow.HomeMutation
import com.savvasdalkitsis.librephotos.home.mvflow.HomeMutation.Loading
import com.savvasdalkitsis.librephotos.home.view.state.HomeState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import net.pedroloureiro.mvflow.EffectSender
import net.pedroloureiro.mvflow.HandlerWithEffects
import javax.inject.Inject

class HomeHandler @Inject constructor(
    private val authenticationUseCase: AuthenticationUseCase,
) : HandlerWithEffects<HomeState, HomeAction, HomeMutation, HomeEffect> {

    override fun invoke(
        state: HomeState,
        action: HomeAction,
        effect: EffectSender<HomeEffect>
    ): Flow<HomeMutation> = when (action) {
        is Load -> flow {
            emit(Loading)
            when (authenticationUseCase.authenticationStatus()) {
                is AuthStatus.Unauthenticated -> effect.send(LaunchAuthentication)
                else -> effect.send(HomeEffect.LoadFeed)
            }
        }
    }
}
