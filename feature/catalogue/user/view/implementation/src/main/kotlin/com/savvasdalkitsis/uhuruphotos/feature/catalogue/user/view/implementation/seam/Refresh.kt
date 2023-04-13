package com.savvasdalkitsis.uhuruphotos.feature.catalogue.user.view.implementation.seam

import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.EffectHandler
import kotlinx.coroutines.flow.flow

object Refresh : UserAlbumsAction() {
    context(UserAlbumsActionsContext) override fun handle(
        state: UserAlbumsState,
        effect: EffectHandler<UserAlbumsEffect>
    ) = flow< UserAlbumsMutation> {
        refreshAlbums(effect)
    }
}