package com.savvasdalkitsis.uhuruphotos.feature.person.view.implementation.seam.actions

import com.savvasdalkitsis.uhuruphotos.feature.media.common.view.api.ui.state.CelState
import com.savvasdalkitsis.uhuruphotos.feature.person.view.implementation.seam.PersonActionsContext
import com.savvasdalkitsis.uhuruphotos.feature.person.view.implementation.seam.PersonEffect
import com.savvasdalkitsis.uhuruphotos.feature.person.view.implementation.seam.PersonEffect.OpenLightbox
import com.savvasdalkitsis.uhuruphotos.feature.person.view.implementation.seam.PersonMutation
import com.savvasdalkitsis.uhuruphotos.feature.person.view.implementation.ui.state.PersonState
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.EffectHandler
import kotlinx.coroutines.flow.flow

data class SelectedCel(val cel: CelState) : PersonAction() {
    context(PersonActionsContext) override fun handle(
        state: PersonState,
        effect: EffectHandler<PersonEffect>
    ) = flow<PersonMutation> {
        effect.handleEffect(
            OpenLightbox(cel.mediaItem.id, cel.mediaItem.isVideo, state.person!!)
        )
    }
}