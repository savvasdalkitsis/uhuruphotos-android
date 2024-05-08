/*
Copyright 2024 Savvas Dalkitsis

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

import com.savvasdalkitsis.uhuruphotos.feature.processing.domain.api.model.ProcessingItem
import com.savvasdalkitsis.uhuruphotos.feature.processing.view.implementation.seam.ProcessingActionsContext
import com.savvasdalkitsis.uhuruphotos.feature.processing.view.implementation.seam.ProcessingMutation.ShowMessageDialog
import com.savvasdalkitsis.uhuruphotos.feature.processing.view.implementation.ui.state.ProcessingState
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.Mutation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf

data class TappedProcessingItem(val item: ProcessingItem) : ProcessingAction() {

    override fun ProcessingActionsContext.handle(state: ProcessingState): Flow<Mutation<ProcessingState>> =
        (item.error.notBlank ?: item.lastResponse.notBlank)?.let {
            flowOf(ShowMessageDialog(item, it))
        } ?: emptyFlow()

    private val String?.notBlank get() = this?.takeIf { it.isNotBlank() }
}