package com.savvasdalkitsis.librephotos.home.viewmodel

import com.savvasdalkitsis.librephotos.auth.model.AuthStatus.Unauthenticated
import com.savvasdalkitsis.librephotos.auth.usecase.AuthenticationUseCase
import com.savvasdalkitsis.librephotos.home.mvflow.HomeAction
import com.savvasdalkitsis.librephotos.home.mvflow.HomeAction.Load
import com.savvasdalkitsis.librephotos.home.mvflow.HomeEffect
import com.savvasdalkitsis.librephotos.home.mvflow.HomeEffect.LaunchAuthentication
import com.savvasdalkitsis.librephotos.home.mvflow.HomeMutation
import com.savvasdalkitsis.librephotos.home.mvflow.HomeMutation.Loading
import com.savvasdalkitsis.librephotos.home.view.state.HomeState
import com.savvasdalkitsis.librephotos.viewmodel.Handler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class HomeHandler @Inject constructor(
    private val authenticationUseCase: AuthenticationUseCase,
) : Handler<HomeState, HomeEffect, HomeAction, HomeMutation> {

    override fun invoke(
        state: HomeState,
        action: HomeAction,
        effect: suspend (HomeEffect) -> Unit,
    ): Flow<HomeMutation> = when (action) {
        is Load -> flow {
            emit(Loading)
            when (authenticationUseCase.authenticationStatus()) {
                is Unauthenticated -> effect(LaunchAuthentication)
                else -> effect(HomeEffect.LoadFeed)
            }
        }
    }
}
