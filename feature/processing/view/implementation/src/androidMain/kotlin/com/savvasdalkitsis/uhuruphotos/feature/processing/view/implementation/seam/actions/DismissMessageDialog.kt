package com.savvasdalkitsis.uhuruphotos.feature.processing.view.implementation.seam.actions

import com.savvasdalkitsis.uhuruphotos.feature.processing.view.implementation.seam.ProcessingActionsContext
import com.savvasdalkitsis.uhuruphotos.feature.processing.view.implementation.seam.ProcessingMutation
import com.savvasdalkitsis.uhuruphotos.feature.processing.view.implementation.ui.state.ProcessingState
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.Mutation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

data object DismissMessageDialog : ProcessingAction() {

    override fun ProcessingActionsContext.handle(state: ProcessingState): Flow<Mutation<ProcessingState>> =
        flowOf(ProcessingMutation.DismissMessageDialog)

}