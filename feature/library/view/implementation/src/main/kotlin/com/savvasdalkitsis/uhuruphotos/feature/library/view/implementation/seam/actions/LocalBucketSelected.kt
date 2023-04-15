package com.savvasdalkitsis.uhuruphotos.feature.library.view.implementation.seam.actions

import com.savvasdalkitsis.uhuruphotos.feature.library.view.implementation.seam.LibraryActionsContext
import com.savvasdalkitsis.uhuruphotos.feature.library.view.implementation.seam.LibraryEffect
import com.savvasdalkitsis.uhuruphotos.feature.library.view.implementation.seam.LibraryEffect.NavigateToLocalBucket
import com.savvasdalkitsis.uhuruphotos.feature.library.view.implementation.seam.LibraryMutation
import com.savvasdalkitsis.uhuruphotos.feature.library.view.implementation.ui.state.LibraryState
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.model.LocalMediaFolder
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.EffectHandler
import kotlinx.coroutines.flow.flow

data class LocalBucketSelected(val bucket: LocalMediaFolder) : LibraryAction() {
    context(LibraryActionsContext) override fun handle(
        state: LibraryState,
        effect: EffectHandler<LibraryEffect>
    ) = flow<LibraryMutation> {
        effect.handleEffect(NavigateToLocalBucket(bucket))
    }
}