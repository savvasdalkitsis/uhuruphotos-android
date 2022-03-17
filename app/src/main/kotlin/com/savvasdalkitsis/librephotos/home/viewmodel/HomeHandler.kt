package com.savvasdalkitsis.librephotos.home.viewmodel

import com.savvasdalkitsis.librephotos.auth.model.AuthStatus
import com.savvasdalkitsis.librephotos.auth.usecase.AuthenticationUseCase
import com.savvasdalkitsis.librephotos.feed.view.preview.feedStatePreview
import com.savvasdalkitsis.librephotos.home.actions.HomeAction
import com.savvasdalkitsis.librephotos.home.actions.HomeAction.LoadFeed
import com.savvasdalkitsis.librephotos.home.effect.HomeEffect
import com.savvasdalkitsis.librephotos.home.effect.HomeEffect.LaunchAuthentication
import com.savvasdalkitsis.librephotos.home.mutation.HomeMutation
import com.savvasdalkitsis.librephotos.home.state.HomeState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
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
        is LoadFeed -> channelFlow {
            send(HomeMutation.Loading)
            authenticationUseCase.authenticationStatus().collectLatest {
                when (it) {
                    is AuthStatus.Unauthenticated -> effect.send(LaunchAuthentication)
                    else -> send(HomeMutation.Loaded(feedStatePreview))
                }
            }
        }
    }
}
