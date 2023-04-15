package com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.seam.action

import com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.seam.GalleryActionsContext
import com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.seam.GalleryEffect
import com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.seam.GalleryEffect.NavigateToPerson
import com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.seam.GalleryMutation
import com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.ui.state.GalleryState
import com.savvasdalkitsis.uhuruphotos.feature.people.view.api.ui.state.Person
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.EffectHandler
import kotlinx.coroutines.flow.flow

data class PersonSelected(val person: Person) : GalleryAction() {
    context(GalleryActionsContext) override fun handle(
        state: GalleryState,
        effect: EffectHandler<GalleryEffect>
    ) = flow<GalleryMutation> {
        effect.handleEffect(NavigateToPerson(person.id))
    }
}