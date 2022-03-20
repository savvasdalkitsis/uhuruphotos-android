package com.savvasdalkitsis.librephotos.home.viewmodel

import com.savvasdalkitsis.librephotos.auth.model.AuthStatus
import com.savvasdalkitsis.librephotos.auth.usecase.AuthenticationUseCase
import com.savvasdalkitsis.librephotos.feed.state.FeedState
import com.savvasdalkitsis.librephotos.feed.view.preview.feedStatePreview
import com.savvasdalkitsis.librephotos.home.mvflow.HomeAction
import com.savvasdalkitsis.librephotos.home.mvflow.HomeAction.LoadFeed
import com.savvasdalkitsis.librephotos.home.mvflow.HomeEffect
import com.savvasdalkitsis.librephotos.home.mvflow.HomeEffect.LaunchAuthentication
import com.savvasdalkitsis.librephotos.home.mvflow.HomeMutation
import com.savvasdalkitsis.librephotos.home.state.HomeState
import com.savvasdalkitsis.librephotos.photos.usecase.PhotosUseCase
import com.savvasdalkitsis.librephotos.server.usecase.ServerUseCase
import kotlinx.coroutines.flow.*
import net.pedroloureiro.mvflow.EffectSender
import net.pedroloureiro.mvflow.HandlerWithEffects
import javax.inject.Inject

class HomeHandler @Inject constructor(
    private val authenticationUseCase: AuthenticationUseCase,
    private val photosUseCase: PhotosUseCase,
    private val serverUseCase: ServerUseCase,
) : HandlerWithEffects<HomeState, HomeAction, HomeMutation, HomeEffect> {

    override fun invoke(
        state: HomeState,
        action: HomeAction,
        effect: EffectSender<HomeEffect>
    ): Flow<HomeMutation> = when (action) {
        is LoadFeed -> flow {
            emit(HomeMutation.Loading)
            val serverUrl = serverUseCase.getServerUrl()
            when (authenticationUseCase.authenticationStatus()) {
                is AuthStatus.Unauthenticated -> effect.send(LaunchAuthentication)
                else -> emit(HomeMutation.Loaded(FeedState(photosUseCase.getPhotos().paths.map {
                    serverUrl + it
                })))
            }
        }
    }
}
