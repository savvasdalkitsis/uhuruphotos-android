package com.savvasdalkitsis.uhuruphotos.feature.catalogue.auto.view.implementation.seam.actions

import com.savvasdalkitsis.uhuruphotos.feature.catalogue.auto.view.api.state.AutoAlbum
import com.savvasdalkitsis.uhuruphotos.feature.catalogue.auto.view.implementation.seam.AutoAlbumsActionsContext
import com.savvasdalkitsis.uhuruphotos.feature.catalogue.auto.view.implementation.seam.AutoAlbumsEffect
import com.savvasdalkitsis.uhuruphotos.feature.catalogue.auto.view.implementation.seam.AutoAlbumsEffect.*
import com.savvasdalkitsis.uhuruphotos.feature.catalogue.auto.view.implementation.seam.AutoAlbumsMutation
import com.savvasdalkitsis.uhuruphotos.feature.catalogue.auto.view.implementation.seam.AutoAlbumsState
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.EffectHandler
import kotlinx.coroutines.flow.flow

data class AutoAlbumSelected(val album: AutoAlbum) : AutoAlbumsAction() {
    context(AutoAlbumsActionsContext) override fun handle(
        state: AutoAlbumsState,
        effect: EffectHandler<AutoAlbumsEffect>
    ) = flow<AutoAlbumsMutation> {
        effect.handleEffect(NavigateToAutoAlbum(album))
    }
}