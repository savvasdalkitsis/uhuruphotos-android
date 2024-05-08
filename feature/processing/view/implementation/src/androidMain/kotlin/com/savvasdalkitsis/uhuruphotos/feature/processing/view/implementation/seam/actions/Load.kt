/*
Copyright 2023 Savvas Dalkitsis

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
package com.savvasdalkitsis.uhuruphotos.feature.processing.view.implementation.seam.actions

import com.savvasdalkitsis.uhuruphotos.feature.processing.view.implementation.seam.ProcessingActionsContext
import com.savvasdalkitsis.uhuruphotos.feature.processing.view.implementation.seam.ProcessingMutation.Loading
import com.savvasdalkitsis.uhuruphotos.feature.processing.view.implementation.seam.ProcessingMutation.ShowProcessing
import com.savvasdalkitsis.uhuruphotos.feature.processing.view.implementation.ui.state.ProcessingState
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.Mutation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge

data object Load : ProcessingAction() {

    override fun ProcessingActionsContext.handle(state: ProcessingState): Flow<Mutation<ProcessingState>> =
        merge(
            flowOf(Loading),
            processingUseCase.observeProcessingMedia().map(::ShowProcessing)
        )
}