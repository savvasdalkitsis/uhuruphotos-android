package com.savvasdalkitsis.uhuruphotos.feature.uploads.view.implementation.seam.actions

import com.savvasdalkitsis.uhuruphotos.feature.uploads.view.implementation.seam.UploadsActionsContext
import com.savvasdalkitsis.uhuruphotos.feature.uploads.view.implementation.ui.state.UploadsState
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.Mutation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

data object UpPressed : UploadsAction() {
    context(UploadsActionsContext)
    override fun handle(state: UploadsState): Flow<Mutation<UploadsState>> = flow {
        navigator.navigateBack()
    }
}