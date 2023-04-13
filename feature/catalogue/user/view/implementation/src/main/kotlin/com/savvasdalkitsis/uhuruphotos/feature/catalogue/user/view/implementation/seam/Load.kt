package com.savvasdalkitsis.uhuruphotos.feature.catalogue.user.view.implementation.seam

import com.savvasdalkitsis.uhuruphotos.feature.catalogue.user.view.api.state.toUserAlbumState
import com.savvasdalkitsis.uhuruphotos.foundation.coroutines.api.safelyOnStartIgnoring
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.EffectHandler
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge

object Load : UserAlbumsAction() {
    context(UserAlbumsActionsContext) override fun handle(
        state: UserAlbumsState,
        effect: EffectHandler<UserAlbumsEffect>
    ) = merge(
        userAlbumsUseCase.observeUserAlbums()
            .map { albums ->
                with(remoteMediaUseCase) {
                    albums.map { it.toUserAlbumState() }
                }
            }
            .map(UserAlbumsMutation::DisplayAlbums),
        loading
            .map(UserAlbumsMutation::Loading)
    ).safelyOnStartIgnoring {
        if (userAlbumsUseCase.getUserAlbums().isEmpty()) {
            refreshAlbums(effect)
        }
    }

}