package com.savvasdalkitsis.uhuruphotos.feature.catalogue.user.view.implementation.seam

import com.savvasdalkitsis.uhuruphotos.feature.catalogue.view.api.ui.state.CatalogueSorting
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.EffectHandler
import kotlinx.coroutines.flow.flow

data class ChangeSorting(val sorting: CatalogueSorting) : UserAlbumsAction() {
    context(UserAlbumsActionsContext) override fun handle(
        state: UserAlbumsState,
        effect: EffectHandler<UserAlbumsEffect>
    ) = flow<UserAlbumsMutation> {
        userAlbumsUseCase.changeUserAlbumsSorting(sorting)
    }
}