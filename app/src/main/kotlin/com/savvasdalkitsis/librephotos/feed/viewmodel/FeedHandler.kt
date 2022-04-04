package com.savvasdalkitsis.librephotos.feed.viewmodel

import com.savvasdalkitsis.librephotos.account.usecase.AccountUseCase
import com.savvasdalkitsis.librephotos.albums.usecase.AlbumsUseCase
import com.savvasdalkitsis.librephotos.feed.mvflow.FeedAction
import com.savvasdalkitsis.librephotos.feed.mvflow.FeedAction.*
import com.savvasdalkitsis.librephotos.feed.mvflow.FeedEffect
import com.savvasdalkitsis.librephotos.feed.mvflow.FeedEffect.ReloadApp
import com.savvasdalkitsis.librephotos.feed.mvflow.FeedMutation
import com.savvasdalkitsis.librephotos.feed.mvflow.FeedMutation.*
import com.savvasdalkitsis.librephotos.feed.view.state.FeedPageState
import com.savvasdalkitsis.librephotos.userbadge.usecase.UserBadgeUseCase
import com.savvasdalkitsis.librephotos.viewmodel.Handler
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@FlowPreview
class FeedHandler @Inject constructor(
    private val albumsUseCase: AlbumsUseCase,
    private val userBadgeUseCase: UserBadgeUseCase,
    private val accountUseCase: AccountUseCase,
) : Handler<FeedPageState, FeedEffect, FeedAction, FeedMutation> {

    override fun invoke(
        state: FeedPageState,
        action: FeedAction,
        effect: suspend (FeedEffect) -> Unit,
    ): Flow<FeedMutation> = when (action) {
        is LoadFeed -> merge(
            flowOf(Loading),
            albumsUseCase.getAlbums(refresh = false)
                .debounce(200)
                .map(::ShowAlbums),
            userBadgeUseCase.getUserBadgeState()
                .map(::UserBadgeUpdate),
        )
        UserBadgePressed -> flowOf(ShowAccountOverview)
        DismissAccountOverview -> flowOf(HideAccountOverview)
        LogOut -> flow {
            accountUseCase.logOut()
            effect(ReloadApp)
        }
    }
}
