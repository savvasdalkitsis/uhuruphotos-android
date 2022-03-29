package com.savvasdalkitsis.librephotos.feed.viewmodel

import com.savvasdalkitsis.librephotos.albums.usecase.AlbumsUseCase
import com.savvasdalkitsis.librephotos.feed.mvflow.FeedAction
import com.savvasdalkitsis.librephotos.feed.mvflow.FeedAction.LoadFeed
import com.savvasdalkitsis.librephotos.feed.mvflow.FeedEffect
import com.savvasdalkitsis.librephotos.feed.mvflow.FeedMutation
import com.savvasdalkitsis.librephotos.feed.mvflow.FeedMutation.*
import com.savvasdalkitsis.librephotos.feed.view.state.FeedPageState
import com.savvasdalkitsis.librephotos.userbadge.usecase.UserBadgeUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import net.pedroloureiro.mvflow.EffectSender
import net.pedroloureiro.mvflow.HandlerWithEffects
import javax.inject.Inject

class FeedHandler @Inject constructor(
    private val albumsUseCase: AlbumsUseCase,
    private val userBadgeUseCase: UserBadgeUseCase,
) : HandlerWithEffects<FeedPageState, FeedAction, FeedMutation, FeedEffect> {

    override fun invoke(
        state: FeedPageState,
        action: FeedAction,
        effect: EffectSender<FeedEffect>
    ): Flow<FeedMutation> = when (action) {
        is LoadFeed -> channelFlow {
            send(Loading)
            albumsUseCase.getAlbums(refresh = false)
                .debounce(200)
                .map(::ShowAlbums)
                .onEach(this::send)
                .launchIn(CoroutineScope(Dispatchers.Default))

            userBadgeUseCase.getUserBadgeState()
                .map(::UserBadgeUpdate)
                .onEach(this::send)
                .launchIn(CoroutineScope(Dispatchers.Default))

            awaitClose()
        }
    }
}
