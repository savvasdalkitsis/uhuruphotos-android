package com.savvasdalkitsis.uhuruphotos.feature.catalogue.auto.view.implementation.seam.actions

import com.savvasdalkitsis.uhuruphotos.feature.catalogue.auto.view.implementation.seam.AutoAlbumsActionsContext
import com.savvasdalkitsis.uhuruphotos.feature.catalogue.auto.view.implementation.seam.AutoAlbumsEffect
import com.savvasdalkitsis.uhuruphotos.feature.catalogue.auto.view.implementation.seam.AutoAlbumsMutation
import com.savvasdalkitsis.uhuruphotos.feature.catalogue.auto.view.implementation.seam.AutoAlbumsState
import com.savvasdalkitsis.uhuruphotos.foundation.coroutines.api.safelyOnStartIgnoring
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.EffectHandler
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge

object Load : AutoAlbumsAction() {
    context(AutoAlbumsActionsContext) override fun handle(
        state: AutoAlbumsState,
        effect: EffectHandler<AutoAlbumsEffect>
    ) = merge(
        autoAlbumsUseCase.observeAutoAlbums()
            .map(AutoAlbumsMutation::DisplayAlbums),
        loading
            .map(AutoAlbumsMutation::Loading)
    ).safelyOnStartIgnoring {
        if (autoAlbumsUseCase.getAutoAlbums().isEmpty()) {
            refreshAlbums(effect)
        }
    }
}