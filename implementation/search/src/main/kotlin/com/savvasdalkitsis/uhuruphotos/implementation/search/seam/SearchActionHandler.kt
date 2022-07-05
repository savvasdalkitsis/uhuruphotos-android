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
package com.savvasdalkitsis.uhuruphotos.implementation.search.seam

import com.savvasdalkitsis.uhuruphotos.api.account.usecase.AccountUseCase
import com.savvasdalkitsis.uhuruphotos.api.coroutines.onErrors
import com.savvasdalkitsis.uhuruphotos.api.coroutines.onErrorsIgnore
import com.savvasdalkitsis.uhuruphotos.api.db.people.People
import com.savvasdalkitsis.uhuruphotos.api.feedpage.usecase.FeedPageUseCase
import com.savvasdalkitsis.uhuruphotos.api.log.log
import com.savvasdalkitsis.uhuruphotos.api.people.usecase.PeopleUseCase
import com.savvasdalkitsis.uhuruphotos.api.people.view.state.toPerson
import com.savvasdalkitsis.uhuruphotos.api.photos.usecase.PhotosUseCase
import com.savvasdalkitsis.uhuruphotos.api.seam.ActionHandler
import com.savvasdalkitsis.uhuruphotos.api.settings.usecase.SettingsUseCase
import com.savvasdalkitsis.uhuruphotos.api.userbadge.usecase.UserBadgeUseCase
import com.savvasdalkitsis.uhuruphotos.implementation.search.seam.SearchAction.AskToLogOut
import com.savvasdalkitsis.uhuruphotos.implementation.search.seam.SearchAction.ChangeDisplay
import com.savvasdalkitsis.uhuruphotos.implementation.search.seam.SearchAction.ChangeFocus
import com.savvasdalkitsis.uhuruphotos.implementation.search.seam.SearchAction.DismissAccountOverview
import com.savvasdalkitsis.uhuruphotos.implementation.search.seam.SearchAction.DismissLogOutDialog
import com.savvasdalkitsis.uhuruphotos.implementation.search.seam.SearchAction.EditServer
import com.savvasdalkitsis.uhuruphotos.implementation.search.seam.SearchAction.Initialise
import com.savvasdalkitsis.uhuruphotos.implementation.search.seam.SearchAction.LoadHeatMap
import com.savvasdalkitsis.uhuruphotos.implementation.search.seam.SearchAction.LogOut
import com.savvasdalkitsis.uhuruphotos.implementation.search.seam.SearchAction.PersonSelected
import com.savvasdalkitsis.uhuruphotos.implementation.search.seam.SearchAction.QueryChanged
import com.savvasdalkitsis.uhuruphotos.implementation.search.seam.SearchAction.RemoveFromRecentSearches
import com.savvasdalkitsis.uhuruphotos.implementation.search.seam.SearchAction.SearchCleared
import com.savvasdalkitsis.uhuruphotos.implementation.search.seam.SearchAction.SearchFor
import com.savvasdalkitsis.uhuruphotos.implementation.search.seam.SearchAction.SelectedPhoto
import com.savvasdalkitsis.uhuruphotos.implementation.search.seam.SearchAction.SettingsClick
import com.savvasdalkitsis.uhuruphotos.implementation.search.seam.SearchAction.UserBadgePressed
import com.savvasdalkitsis.uhuruphotos.implementation.search.seam.SearchAction.ViewAllPeopleSelected
import com.savvasdalkitsis.uhuruphotos.implementation.search.seam.SearchEffect.ErrorRefreshingPeople
import com.savvasdalkitsis.uhuruphotos.implementation.search.seam.SearchEffect.ErrorSearching
import com.savvasdalkitsis.uhuruphotos.implementation.search.seam.SearchEffect.HideKeyboard
import com.savvasdalkitsis.uhuruphotos.implementation.search.seam.SearchEffect.NavigateToAllPeople
import com.savvasdalkitsis.uhuruphotos.implementation.search.seam.SearchEffect.NavigateToEditServer
import com.savvasdalkitsis.uhuruphotos.implementation.search.seam.SearchEffect.NavigateToHeatMap
import com.savvasdalkitsis.uhuruphotos.implementation.search.seam.SearchEffect.NavigateToPerson
import com.savvasdalkitsis.uhuruphotos.implementation.search.seam.SearchEffect.NavigateToSettings
import com.savvasdalkitsis.uhuruphotos.implementation.search.seam.SearchEffect.OpenPhotoDetails
import com.savvasdalkitsis.uhuruphotos.implementation.search.seam.SearchEffect.ReloadApp
import com.savvasdalkitsis.uhuruphotos.implementation.search.seam.SearchMutation.ChangeFeedDisplay
import com.savvasdalkitsis.uhuruphotos.implementation.search.seam.SearchMutation.ChangeSearchDisplay
import com.savvasdalkitsis.uhuruphotos.implementation.search.seam.SearchMutation.FocusChanged
import com.savvasdalkitsis.uhuruphotos.implementation.search.seam.SearchMutation.HideAccountOverview
import com.savvasdalkitsis.uhuruphotos.implementation.search.seam.SearchMutation.HideLogOutConfirmation
import com.savvasdalkitsis.uhuruphotos.implementation.search.seam.SearchMutation.HideSuggestions
import com.savvasdalkitsis.uhuruphotos.implementation.search.seam.SearchMutation.ShowAccountOverview
import com.savvasdalkitsis.uhuruphotos.implementation.search.seam.SearchMutation.ShowLibrary
import com.savvasdalkitsis.uhuruphotos.implementation.search.seam.SearchMutation.ShowLogOutConfirmation
import com.savvasdalkitsis.uhuruphotos.implementation.search.seam.SearchMutation.ShowPeople
import com.savvasdalkitsis.uhuruphotos.implementation.search.seam.SearchMutation.ShowSearchSuggestion
import com.savvasdalkitsis.uhuruphotos.implementation.search.seam.SearchMutation.ShowSearchSuggestions
import com.savvasdalkitsis.uhuruphotos.implementation.search.seam.SearchMutation.SwitchStateToFound
import com.savvasdalkitsis.uhuruphotos.implementation.search.seam.SearchMutation.SwitchStateToIdle
import com.savvasdalkitsis.uhuruphotos.implementation.search.seam.SearchMutation.SwitchStateToSearching
import com.savvasdalkitsis.uhuruphotos.implementation.search.seam.SearchMutation.UpdateLatestQuery
import com.savvasdalkitsis.uhuruphotos.implementation.search.seam.SearchMutation.UserBadgeStateChanged
import com.savvasdalkitsis.uhuruphotos.implementation.search.usecase.SearchUseCase
import com.savvasdalkitsis.uhuruphotos.implementation.search.view.state.SearchResults.Found
import com.savvasdalkitsis.uhuruphotos.implementation.search.view.state.SearchState
import com.savvasdalkitsis.uhuruphotos.implementation.search.view.state.SearchSuggestion
import com.savvasdalkitsis.uhuruphotos.implementation.search.view.state.SearchSuggestion.PersonSearchSuggestion
import com.savvasdalkitsis.uhuruphotos.implementation.search.view.state.SearchSuggestion.RecentSearchSuggestion
import com.savvasdalkitsis.uhuruphotos.implementation.search.view.state.SearchSuggestion.ServerSearchSuggestion
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.max
import kotlin.math.min

class SearchActionHandler @Inject constructor(
    private val searchUseCase: SearchUseCase,
    private val userBadgeUseCase: UserBadgeUseCase,
    private val accountUseCase: AccountUseCase,
    private val feedPageUseCase: FeedPageUseCase,
    private val settingsUseCase: SettingsUseCase,
    private val peopleUseCase: PeopleUseCase,
    private val photosUseCase: PhotosUseCase,
) : ActionHandler<SearchState, SearchEffect, SearchAction, SearchMutation> {

    private var lastSearch: Job? = null
    private var lastSuggestions: Job? = null
    private val queryFilter = MutableSharedFlow<String>()

    override fun handleAction(
        state: SearchState,
        action: SearchAction,
        effect: suspend (SearchEffect) -> Unit,
    ): Flow<SearchMutation> = when (action) {
        Initialise -> with(photosUseCase) {
            merge(
                showLibrary(),
                showFeedDisplay(),
                showUserBadgeState(),
                showServerSearchSuggestion(),
                showPeopleSuggestion(effect),
                showSearchSuggestions()
            )
        }
        is QueryChanged -> flow {
            queryFilter.emit(action.query)
            emit(UpdateLatestQuery(action.query))
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
            with(action) {
                effect(OpenPhotoDetails(photo.id, center, scale, photo.isVideo, state.latestQuery))
            }
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
        send(UpdateLatestQuery(action.query))
        send(SwitchStateToSearching)
        effect(HideKeyboard)
        lastSearch = launch {
            searchUseCase.addSearchToRecentSearches(action.query)
            searchUseCase.searchFor(action.query)
                .debounce(200)
                .mapNotNull { result ->
                    val albums = result.getOrNull()
                    if (albums != null)
                        when {
                            albums.isEmpty() -> SwitchStateToSearching
                            else -> SwitchStateToFound(Found(albums))
                        }
                    else {
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

    private fun showLibrary() = settingsUseCase.observeShowLibrary()
        .map(::ShowLibrary)

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
