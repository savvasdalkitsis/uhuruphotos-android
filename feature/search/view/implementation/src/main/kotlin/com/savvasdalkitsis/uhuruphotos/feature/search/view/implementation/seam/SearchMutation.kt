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
package com.savvasdalkitsis.uhuruphotos.feature.search.view.implementation.seam

import com.savvasdalkitsis.uhuruphotos.feature.collage.view.api.ui.state.ClusterState
import com.savvasdalkitsis.uhuruphotos.feature.collage.view.api.ui.state.CollageDisplayState
import com.savvasdalkitsis.uhuruphotos.feature.search.view.implementation.ui.state.SearchState
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.Mutation
import kotlinx.collections.immutable.toImmutableList

sealed class SearchMutation(
    mutation: Mutation<SearchState>,
) : Mutation<SearchState> by mutation {

    data object Loading : SearchMutation({
        it.copy(
            isError = false,
            isLoading = true,
        )
    })
    data class SetQuery(val query: String) : SearchMutation({
        it.copy(query = query)
    })

    data class ChangeSearchDisplay(val display: CollageDisplayState) : SearchMutation({
        it.copy(searchDisplay = display)
    })

    data class DisplaySearchResults(val clusterStates: List<ClusterState>) : SearchMutation({
        it.copy(
            isLoading = false,
            clusterStates = clusterStates.toImmutableList(),
        )
    })

    data object ShowErrorScreen : SearchMutation({
        it.copy(
            isError = true,
        )
    })
}