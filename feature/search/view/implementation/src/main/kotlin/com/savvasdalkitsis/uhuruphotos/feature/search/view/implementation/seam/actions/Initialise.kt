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

import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.people.People
import com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.api.usecase.RemoteMediaUseCase
import com.savvasdalkitsis.uhuruphotos.feature.people.view.api.ui.state.toPerson
import com.savvasdalkitsis.uhuruphotos.feature.search.view.implementation.seam.SearchActionsContext
import com.savvasdalkitsis.uhuruphotos.feature.search.view.implementation.seam.effects.SearchEffect
import com.savvasdalkitsis.uhuruphotos.feature.search.view.implementation.seam.SearchMutation
import com.savvasdalkitsis.uhuruphotos.feature.search.view.implementation.seam.effects.ErrorRefreshingPeople
import com.savvasdalkitsis.uhuruphotos.feature.search.view.implementation.ui.state.SearchState
import com.savvasdalkitsis.uhuruphotos.feature.search.view.implementation.ui.state.SearchSuggestion
import com.savvasdalkitsis.uhuruphotos.foundation.coroutines.api.onErrors
import com.savvasdalkitsis.uhuruphotos.foundation.coroutines.api.onErrorsIgnore
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.EffectHandler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlin.math.max
import kotlin.math.min

object Initialise : SearchAction() {
    context(SearchActionsContext) override fun handle(
        state: SearchState,
        effect: EffectHandler<SearchEffect>
    ) = with(remoteMediaUseCase) {
        merge(
            showLibrary(),
            showFeedDisplay(),
            showServerSearchSuggestion(),
            showPeopleSuggestion(effect),
            showSearchSuggestions()
        )
    }

    context(RemoteMediaUseCase, SearchActionsContext)
    private fun showSearchSuggestions() = combine(
        searchUseCase.getRecentTextSearches()
            .map {
                it.map(SearchSuggestion::RecentSearchSuggestion)
            },
        peopleUseCase.observePeopleByPhotoCount()
            .onErrorsIgnore()
            .toPeople()
            .map {
                it.map(SearchSuggestion::PersonSearchSuggestion)
            },
        searchUseCase.getSearchSuggestions()
            .map {
                it.map(SearchSuggestion::ServerSearchSuggestion)
            },
        queryFilter,
    ) { recentSearches, people, searchSuggestions, query ->
        when {
            query.isEmpty() -> emptyList()
            else -> recentSearches + people + searchSuggestions
        }.filterQuery(query)
    }.map(SearchMutation::ShowSearchSuggestions)

    context(RemoteMediaUseCase, SearchActionsContext)
    private fun showPeopleSuggestion(effect: EffectHandler<SearchEffect>) =
        peopleUseCase.observePeopleByPhotoCount()
            .onErrors {
                effect.handleEffect(ErrorRefreshingPeople)
            }
            .toPeople()
            .map { it.subList(0, max(0, min(10, it.size - 1))) }
            .map(SearchMutation::ShowPeople)

    context(SearchActionsContext)
    private fun showServerSearchSuggestion() =
        settingsUseCase.observeSearchSuggestionsEnabledMode().flatMapLatest { enabled ->
            if (enabled)
                searchUseCase.getRandomSearchSuggestion()
                    .map(SearchMutation::ShowSearchSuggestion)
            else
                flowOf(SearchMutation.HideSuggestions)
        }

    context(SearchActionsContext)
    private fun showLibrary() = settingsUseCase.observeShowLibrary()
        .map(SearchMutation::ShowLibrary)

    context(SearchActionsContext)
    private fun showFeedDisplay() = feedUseCase
        .getFeedDisplay()
        .distinctUntilChanged()
        .map(SearchMutation::ChangeFeedDisplay)

    context(RemoteMediaUseCase)
    private fun Flow<List<People>>.toPeople() = map { people ->
        people.map {
            it.toPerson { url -> url.toRemoteUrl() }
        }
    }

    private fun List<SearchSuggestion>.filterQuery(query: String): List<SearchSuggestion> =
        filter { it.filterable.contains(query, ignoreCase = true) }
}
