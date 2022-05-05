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
import com.savvasdalkitsis.uhuruphotos.feedpage.usecase.FeedPageUseCase
import com.savvasdalkitsis.uhuruphotos.infrastructure.extensions.onErrors
import com.savvasdalkitsis.uhuruphotos.log.log
import com.savvasdalkitsis.uhuruphotos.people.api.usecase.PeopleUseCase
import com.savvasdalkitsis.uhuruphotos.search.mvflow.SearchAction
import com.savvasdalkitsis.uhuruphotos.search.mvflow.SearchAction.*
import com.savvasdalkitsis.uhuruphotos.search.mvflow.SearchEffect
import com.savvasdalkitsis.uhuruphotos.search.mvflow.SearchEffect.*
import com.savvasdalkitsis.uhuruphotos.search.mvflow.SearchMutation
import com.savvasdalkitsis.uhuruphotos.search.mvflow.SearchMutation.*
import com.savvasdalkitsis.uhuruphotos.search.usecase.SearchUseCase
import com.savvasdalkitsis.uhuruphotos.search.view.state.SearchState
import com.savvasdalkitsis.uhuruphotos.settings.usecase.SettingsUseCase
import com.savvasdalkitsis.uhuruphotos.userbadge.api.UserBadgeUseCase
import com.savvasdalkitsis.uhuruphotos.viewmodel.Handler
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

class SearchHandler @Inject constructor(
    private val searchUseCase: SearchUseCase,
    private val userBadgeUseCase: UserBadgeUseCase,
    private val accountUseCase: AccountUseCase,
    private val feedPageUseCase: FeedPageUseCase,
    private val settingsUseCase: SettingsUseCase,
    private val peopleUseCase: PeopleUseCase,
) : Handler<SearchState, SearchEffect, SearchAction, SearchMutation> {

    private var lastSearch: Job? = null

    override fun invoke(
        state: SearchState,
        action: SearchAction,
        effect: suspend (SearchEffect) -> Unit,
    ): Flow<SearchMutation> = when (action) {
        Initialise -> merge(
            feedPageUseCase
                .getFeedDisplay()
                .distinctUntilChanged()
                .map(::ChangeFeedDisplay),
            userBadgeUseCase.getUserBadgeState()
                .map(::UserBadgeStateChanged),
            settingsUseCase.observeSearchSuggestionsEnabledMode().flatMapLatest { enabled ->
                if (enabled)
                    searchUseCase.getSearchSuggestions()
                        .map(::ShowSearchSuggestion)
                else
                    flowOf(HideSuggestions)
            },
            peopleUseCase.getPeopleByPhotoCount()
                .onErrors {
                    effect(ErrorRefreshingPeople)
                }
                .map(::ShowPeople)
        )
        is ChangeQuery -> flowOf(QueryChanged(action.query))
        is SearchFor -> channelFlow {
            lastSearch?.cancel()
            send(SearchStarted)
            effect(HideKeyboard)
            lastSearch = launch {
                searchUseCase.searchFor(state.query)
                    .debounce(200)
                    .mapNotNull { result ->
                        if (result is Ok) {
                            val albums = result.value
                            when {
                                albums.isEmpty() -> SearchStarted
                                else -> SearchResultsUpdated(albums)
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
                        send(SearchStopped)
                    }
                    .collect { send(it) }
            }
        }
        is ChangeFocus -> flowOf(FocusChanged(action.focused))
        ClearSearch -> flow {
            emit(SearchCleared)
            lastSearch?.cancel()
            emit(SearchStopped)
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
        SendLogsClick -> flow {
            effect(SendFeedback)
        }
    }
}
