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

import com.savvasdalkitsis.uhuruphotos.feature.collage.view.api.ui.state.toCluster
import com.savvasdalkitsis.uhuruphotos.feature.search.view.implementation.seam.SearchActionsContext
import com.savvasdalkitsis.uhuruphotos.feature.search.view.implementation.seam.SearchEffect
import com.savvasdalkitsis.uhuruphotos.feature.search.view.implementation.seam.SearchMutation
import com.savvasdalkitsis.uhuruphotos.feature.search.view.implementation.ui.state.SearchResults
import com.savvasdalkitsis.uhuruphotos.feature.search.view.implementation.ui.state.SearchState
import com.savvasdalkitsis.uhuruphotos.foundation.log.api.log
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.EffectHandler
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.launch

data class SearchFor(val query: String) : SearchAction() {
    context(SearchActionsContext) override fun handle(
        state: SearchState,
        effect: EffectHandler<SearchEffect>
    ) = channelFlow {
        lastSearch?.cancel()
        send(SearchMutation.UpdateLatestQuery(query))
        send(SearchMutation.SwitchStateToSearching)
        effect.handleEffect(SearchEffect.HideKeyboard)
        lastSearch = launch {
            searchUseCase.addSearchToRecentSearches(query)
            searchUseCase.searchFor(query)
                .debounce(200)
                .mapNotNull { result ->
                    val clusters = result.getOrNull()?.map { it.toCluster() }
                    if (clusters != null)
                        when {
                            clusters.isEmpty() -> SearchMutation.SwitchStateToSearching
                            else -> SearchMutation.SwitchStateToFound(SearchResults.Found(clusters))
                        }
                    else {
                        effect.handleEffect(SearchEffect.ErrorSearching)
                        null
                    }
                }
                .cancellable()
                .catch {
                    if (it !is CancellationException) {
                        log(it)
                        effect.handleEffect(SearchEffect.ErrorSearching)
                    }
                    send(SearchMutation.SwitchStateToIdle)
                }
                .collect { send(it) }
        }
    }
}
