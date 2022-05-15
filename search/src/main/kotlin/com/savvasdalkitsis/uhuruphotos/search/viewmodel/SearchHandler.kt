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
package com.savvasdalkitsis.uhuruphotos.search.viewmodel

import com.github.michaelbull.result.Ok
import com.savvasdalkitsis.uhuruphotos.account.usecase.AccountUseCase
import com.savvasdalkitsis.uhuruphotos.db.people.People
import com.savvasdalkitsis.uhuruphotos.feedpage.usecase.FeedPageUseCase
import com.savvasdalkitsis.uhuruphotos.infrastructure.extensions.onErrors
import com.savvasdalkitsis.uhuruphotos.infrastructure.extensions.onErrorsIgnore
import com.savvasdalkitsis.uhuruphotos.log.log
import com.savvasdalkitsis.uhuruphotos.people.api.usecase.PeopleUseCase
import com.savvasdalkitsis.uhuruphotos.people.api.view.state.toPerson
import com.savvasdalkitsis.uhuruphotos.photos.usecase.PhotosUseCase
import com.savvasdalkitsis.uhuruphotos.search.mvflow.SearchAction
import com.savvasdalkitsis.uhuruphotos.search.mvflow.SearchAction.*
import com.savvasdalkitsis.uhuruphotos.search.mvflow.SearchEffect
import com.savvasdalkitsis.uhuruphotos.search.mvflow.SearchEffect.*
import com.savvasdalkitsis.uhuruphotos.search.mvflow.SearchMutation
import com.savvasdalkitsis.uhuruphotos.search.mvflow.SearchMutation.*
import com.savvasdalkitsis.uhuruphotos.search.usecase.SearchUseCase
import com.savvasdalkitsis.uhuruphotos.search.view.state.SearchResults.Found
import com.savvasdalkitsis.uhuruphotos.search.view.state.SearchState
import com.savvasdalkitsis.uhuruphotos.search.view.state.SearchSuggestion
import com.savvasdalkitsis.uhuruphotos.search.view.state.SearchSuggestion.*
import com.savvasdalkitsis.uhuruphotos.settings.usecase.SettingsUseCase
import com.savvasdalkitsis.uhuruphotos.userbadge.api.UserBadgeUseCase
import com.savvasdalkitsis.uhuruphotos.viewmodel.Handler
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.max
import kotlin.math.min

class SearchHandler @Inject constructor(
    private val searchUseCase: SearchUseCase,
    private val userBadgeUseCase: UserBadgeUseCase,
    private val accountUseCase: AccountUseCase,
    private val feedPageUseCase: FeedPageUseCase,
    private val settingsUseCase: SettingsUseCase,
    private val peopleUseCase: PeopleUseCase,
    private val photosUseCase: PhotosUseCase,
) : Handler<SearchState, SearchEffect, SearchAction, SearchMutation> {

    private var lastSearch: Job? = null
    private var lastSuggestions: Job? = null
    private val queryFilter = MutableSharedFlow<String>()

    override fun invoke(
        state: SearchState,
        action: SearchAction,
        effect: suspend (SearchEffect) -> Unit,
    ): Flow<SearchMutation> = when (action) {
        Initialise -> with(photosUseCase) {
            merge(
                showFeedDisplay(),
                showUserBadgeState(),
                showServerSearchSuggestion(),
                showPeopleSuggestion(effect),
                showSearchSuggestions()
            )
        }
        is QueryChanged -> flow {
            queryFilter.emit(action.query)
        }
        is SearchFor -> performSearch(effect, action)
        is ChangeFocus -> flowOf(FocusChanged(action.focused))
        SearchCleared -> flow {
            lastSearch?.cancel()
            lastSuggestions?.cancel()
            emit(SwitchStateToIdle)
        }
        UserBadgePressed -> flowOf(ShowAccountOverview)
        DismissAccountOverview -> flowOf(HideAccountOverview)
        AskToLogOut -> flowOf(ShowLogOutConfirmation)
        DismissLogOutDialog -> flowOf(HideLogOutConfirmation)
        LogOut -> flow {
            accountUseCase.logOut()
            effect(ReloadApp)
        }
        EditServer -> flow {
            emit(HideAccountOverview)
            effect(NavigateToEditServer)
        }
        SettingsClick -> flow {
            emit(HideAccountOverview)
            effect(NavigateToSettings)
        }
        is SelectedPhoto -> flow {
            effect(OpenPhotoDetails(action.photo.id, action.center, action.scale, action.photo.isVideo))
        }
        is ChangeDisplay -> flowOf(ChangeSearchDisplay(action.display))
        ViewAllPeopleSelected -> flow {
            effect(NavigateToAllPeople)
        }
        is PersonSelected -> flow {
            effect(NavigateToPerson(action.person.id))
        }
        LoadHeatMap -> flow {
            effect(NavigateToHeatMap)
        }
        is RemoveFromRecentSearches -> flow {
            searchUseCase.removeFromRecentSearches(action.query)
        }
    }

    private fun performSearch(
        effect: suspend (SearchEffect) -> Unit,
        action: SearchFor
    ) = channelFlow {
        lastSearch?.cancel()
        send(SwitchStateToSearching)
        effect(HideKeyboard)
        lastSearch = launch {
            searchUseCase.addSearchToRecentSearches(action.query)
            searchUseCase.searchFor(action.query)
                .debounce(200)
                .mapNotNull { result ->
                    if (result is Ok) {
                        val albums = result.value
                        when {
                            albums.isEmpty() -> SwitchStateToSearching
                            else -> SwitchStateToFound(Found(albums))
                        }
                    } else {
                        effect(ErrorSearching)
                        null
                    }
                }
                .cancellable()
                .catch {
                    if (it !is CancellationException) {
                        log(it)
                        effect(ErrorSearching)
                    }
                    send(SwitchStateToIdle)
                }
                .collect { send(it) }
        }
    }

    context(PhotosUseCase)
    private fun showSearchSuggestions() = combine(
        searchUseCase.getRecentTextSearches()
            .map {
                it.map(::RecentSearchSuggestion)
            },
        peopleUseCase.observePeopleByPhotoCount()
            .onErrorsIgnore()
            .toPeople()
            .map {
                it.map(::PersonSearchSuggestion)
            },
        searchUseCase.getSearchSuggestions()
            .map {
                it.map(::ServerSearchSuggestion)
            },
        queryFilter,
    ) { recentSearches, people, searchSuggestions, query ->
        when {
            query.isEmpty() -> emptyList()
            else -> recentSearches + people + searchSuggestions
        }.filterQuery(query)
    }.map(::ShowSearchSuggestions)

    context(PhotosUseCase)
    private fun showPeopleSuggestion(effect: suspend (SearchEffect) -> Unit) =
        peopleUseCase.observePeopleByPhotoCount()
            .onErrors {
                effect(ErrorRefreshingPeople)
            }
            .toPeople()
            .map { it.subList(0, max(0, min(10, it.size - 1))) }
            .map(::ShowPeople)

    private fun showServerSearchSuggestion() =
        settingsUseCase.observeSearchSuggestionsEnabledMode().flatMapLatest { enabled ->
            if (enabled)
                searchUseCase.getRandomSearchSuggestion()
                    .map(::ShowSearchSuggestion)
            else
                flowOf(HideSuggestions)
        }

    private fun showUserBadgeState() = userBadgeUseCase.getUserBadgeState()
        .map(::UserBadgeStateChanged)

    private fun showFeedDisplay() = feedPageUseCase
        .getFeedDisplay()
        .distinctUntilChanged()
        .map(::ChangeFeedDisplay)

    context(PhotosUseCase)
    private fun Flow<List<People>>.toPeople() = map { people ->
        people.map {
            it.toPerson { url -> url.toAbsoluteUrl() }
        }
    }

    private fun List<SearchSuggestion>.filterQuery(query: String): List<SearchSuggestion> =
        filter { it.filterable.contains(query, ignoreCase = true) }
}
