package com.savvasdalkitsis.uhuruphotos.home.viewmodel

import com.savvasdalkitsis.uhuruphotos.auth.model.AuthStatus.Unauthenticated
import com.savvasdalkitsis.uhuruphotos.auth.usecase.AuthenticationUseCase
import com.savvasdalkitsis.uhuruphotos.home.mvflow.HomeAction
import com.savvasdalkitsis.uhuruphotos.home.mvflow.HomeAction.Load
import com.savvasdalkitsis.uhuruphotos.home.mvflow.HomeEffect
import com.savvasdalkitsis.uhuruphotos.home.mvflow.HomeEffect.LaunchAuthentication
import com.savvasdalkitsis.uhuruphotos.home.mvflow.HomeMutation
import com.savvasdalkitsis.uhuruphotos.home.mvflow.HomeMutation.Loading
import com.savvasdalkitsis.uhuruphotos.home.view.state.HomeState
import com.savvasdalkitsis.uhuruphotos.viewmodel.Handler
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
