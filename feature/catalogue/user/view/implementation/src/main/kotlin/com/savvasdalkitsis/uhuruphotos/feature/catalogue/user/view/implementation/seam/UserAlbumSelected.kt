package com.savvasdalkitsis.uhuruphotos.feature.catalogue.user.view.implementation.seam

import com.savvasdalkitsis.uhuruphotos.feature.catalogue.user.view.api.state.UserAlbumState
import com.savvasdalkitsis.uhuruphotos.feature.catalogue.user.view.implementation.seam.UserAlbumsEffect.*
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.EffectHandler
import kotlinx.coroutines.flow.flow

data class UserAlbumSelected(val album: UserAlbumState) : UserAlbumsAction() {
    context(UserAlbumsActionsContext) override fun handle(
        state: UserAlbumsState,
        effect: EffectHandler<UserAlbumsEffect>
    ) = flow<UserAlbumsMutation> {
        effect.handleEffect(NavigateToUserAlbum(album))
    }
}