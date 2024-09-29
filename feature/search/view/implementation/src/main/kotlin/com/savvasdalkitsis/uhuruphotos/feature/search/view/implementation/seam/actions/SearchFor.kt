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
package com.savvasdalkitsis.uhuruphotos.feature.search.view.implementation.seam.actions

import com.github.michaelbull.result.getOr
import com.savvasdalkitsis.uhuruphotos.feature.collage.view.api.ui.state.toCluster
import com.savvasdalkitsis.uhuruphotos.feature.search.view.implementation.seam.SearchActionsContext
import com.savvasdalkitsis.uhuruphotos.feature.search.view.implementation.seam.SearchMutation.DisplaySearchResults
import com.savvasdalkitsis.uhuruphotos.feature.search.view.implementation.seam.SearchMutation.Loading
import com.savvasdalkitsis.uhuruphotos.feature.search.view.implementation.seam.SearchMutation.SetQuery
import com.savvasdalkitsis.uhuruphotos.feature.search.view.implementation.seam.SearchMutation.ShowErrorScreen
import com.savvasdalkitsis.uhuruphotos.feature.search.view.implementation.ui.state.SearchState
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.Mutation
import com.savvasdalkitsis.uhuruphotos.foundation.strings.api.R
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.mapNotNull

@OptIn(FlowPreview::class)
data class SearchFor(val query: String) : SearchAction() {
    override fun SearchActionsContext.handle(
        state: SearchState
    ): Flow<Mutation<SearchState>> = flow {
        emit(Loading)
        emit(SetQuery(query))
        searchUseCase.addSearchToRecentSearches(query)
        emitAll(searchUseCase.searchFor(query)
            .debounce(200)
            .cancellable()
            .mapNotNull { result ->
                val clusters = result.getOr(null)?.map { it.toCluster() }
                when {
                    clusters.orEmpty().isNotEmpty() -> DisplaySearchResults(clusters.orEmpty())
                    else -> {
                        if (state.clusterStates.isNotEmpty()) {
                            toaster.show(R.string.error_searching)
                            null
                        } else {
                            ShowErrorScreen
                        }
                    }
                }
            }
        )
    }

}