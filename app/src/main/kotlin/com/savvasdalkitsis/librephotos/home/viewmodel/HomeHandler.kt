package com.savvasdalkitsis.librephotos.home.viewmodel

import com.savvasdalkitsis.librephotos.albums.usecase.AlbumsUseCase
import com.savvasdalkitsis.librephotos.auth.model.AuthStatus
import com.savvasdalkitsis.librephotos.auth.usecase.AuthenticationUseCase
import com.savvasdalkitsis.librephotos.feed.view.FeedState
import com.savvasdalkitsis.librephotos.home.mvflow.HomeAction
import com.savvasdalkitsis.librephotos.home.mvflow.HomeAction.LoadFeed
import com.savvasdalkitsis.librephotos.home.mvflow.HomeEffect
import com.savvasdalkitsis.librephotos.home.mvflow.HomeEffect.LaunchAuthentication
import com.savvasdalkitsis.librephotos.home.mvflow.HomeMutation
import com.savvasdalkitsis.librephotos.home.mvflow.HomeMutation.Loading
import com.savvasdalkitsis.librephotos.home.mvflow.HomeMutation.ShowAlbums
import com.savvasdalkitsis.librephotos.home.state.HomeState
import kotlinx.coroutines.flow.*
import net.pedroloureiro.mvflow.EffectSender
import net.pedroloureiro.mvflow.HandlerWithEffects
import javax.inject.Inject

class HomeHandler @Inject constructor(
    private val authenticationUseCase: AuthenticationUseCase,
    private val albumsUseCase: AlbumsUseCase,
) : HandlerWithEffects<HomeState, HomeAction, HomeMutation, HomeEffect> {

    override fun invoke(
        state: HomeState,
        action: HomeAction,
        effect: EffectSender<HomeEffect>
    ): Flow<HomeMutation> = when (action) {
        is LoadFeed -> flow {
            emit(Loading)
            when (authenticationUseCase.authenticationStatus()) {
                is AuthStatus.Unauthenticated -> effect.send(LaunchAuthentication)
                else -> emitAll(albumsUseCase.getAlbums(refresh = true)
                    .debounce(200)
                    .map { albums ->
                        ShowAlbums(FeedState(albums))
                    }
                )
            }
        }
    }
}
