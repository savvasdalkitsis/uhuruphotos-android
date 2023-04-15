package com.savvasdalkitsis.uhuruphotos.feature.catalogue.auto.view.implementation.seam.actions

import com.savvasdalkitsis.uhuruphotos.feature.catalogue.auto.view.implementation.seam.AutoAlbumsActionsContext
import com.savvasdalkitsis.uhuruphotos.feature.catalogue.auto.view.implementation.seam.AutoAlbumsEffect
import com.savvasdalkitsis.uhuruphotos.feature.catalogue.auto.view.implementation.seam.AutoAlbumsMutation
import com.savvasdalkitsis.uhuruphotos.feature.catalogue.auto.view.implementation.seam.AutoAlbumsState
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.EffectHandler
import kotlinx.coroutines.flow.flow

object Refresh : AutoAlbumsAction() {
    context(AutoAlbumsActionsContext) override fun handle(
        state: AutoAlbumsState,
        effect: EffectHandler<AutoAlbumsEffect>
    ) = flow<AutoAlbumsMutation> {
        refreshAlbums(effect)
    }
}