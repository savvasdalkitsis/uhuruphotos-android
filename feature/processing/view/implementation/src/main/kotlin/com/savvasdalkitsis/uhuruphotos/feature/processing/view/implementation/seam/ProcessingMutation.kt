/*
Copyright 2022 Savvas Dalkitsis

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
package com.savvasdalkitsis.uhuruphotos.feature.processing.view.implementation.seam

import com.savvasdalkitsis.uhuruphotos.feature.processing.domain.api.model.Processing
import com.savvasdalkitsis.uhuruphotos.feature.processing.domain.api.model.ProcessingItem
import com.savvasdalkitsis.uhuruphotos.feature.processing.view.implementation.ui.state.ProcessingState
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.Mutation
import kotlinx.collections.immutable.toImmutableList

sealed class ProcessingMutation(
    mutation: Mutation<ProcessingState>,
) : Mutation<ProcessingState> by mutation {

    data object Loading : ProcessingMutation({
        it.copy(isLoading = true)
    })

    data object DismissMessageDialog : ProcessingMutation({
        it.copy(itemMessageToDisplay = null)
    })

    data class ShowProcessing(val processing: Processing) : ProcessingMutation({
        it.copy(isLoading = false, items = processing.jobs.toImmutableList())
    })

    data class ShowMessageDialog(val item: ProcessingItem, val message: String) : ProcessingMutation({
        it.copy(itemMessageToDisplay = item to message)
    })

    data class ToggleItemSelected(val item: ProcessingItem) : ProcessingMutation({
        it.copy(items = it.items.map { processingItem ->
            if (processingItem == item) {
                processingItem.copy(selected = !processingItem.selected)
            } else {
                processingItem
            }
        }.toImmutableList())
    })
}