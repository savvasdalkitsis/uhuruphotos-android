package com.savvasdalkitsis.uhuruphotos.feature.catalogue.auto.view.implementation.seam.actions

import com.savvasdalkitsis.uhuruphotos.feature.catalogue.auto.view.implementation.seam.AutoAlbumsActionsContext
import com.savvasdalkitsis.uhuruphotos.feature.catalogue.auto.view.implementation.seam.AutoAlbumsEffect
import com.savvasdalkitsis.uhuruphotos.feature.catalogue.auto.view.implementation.seam.AutoAlbumsMutation
import com.savvasdalkitsis.uhuruphotos.feature.catalogue.auto.view.implementation.seam.AutoAlbumsState
import com.savvasdalkitsis.uhuruphotos.feature.catalogue.view.api.ui.state.CatalogueSorting
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.EffectHandler
import kotlinx.coroutines.flow.flow

data class ChangeSorting(val sorting: CatalogueSorting) : AutoAlbumsAction() {
    context(AutoAlbumsActionsContext) override fun handle(
        state: AutoAlbumsState,
        effect: EffectHandler<AutoAlbumsEffect>
    ) = flow<AutoAlbumsMutation> {
        autoAlbumsUseCase.changeAutoAlbumsSorting(sorting)
    }
}