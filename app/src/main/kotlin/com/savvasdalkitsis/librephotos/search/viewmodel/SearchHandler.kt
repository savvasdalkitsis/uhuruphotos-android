package com.savvasdalkitsis.librephotos.search.viewmodel

import com.savvasdalkitsis.librephotos.account.usecase.AccountUseCase
import com.savvasdalkitsis.librephotos.search.mvflow.SearchAction
import com.savvasdalkitsis.librephotos.search.mvflow.SearchEffect
import com.savvasdalkitsis.librephotos.search.mvflow.SearchEffect.FocusSearchBar
import com.savvasdalkitsis.librephotos.search.mvflow.SearchEffect.HideKeyboard
import com.savvasdalkitsis.librephotos.search.mvflow.SearchMutation
import com.savvasdalkitsis.librephotos.search.mvflow.SearchMutation.*
import com.savvasdalkitsis.librephotos.search.usecase.SearchUseCase
import com.savvasdalkitsis.librephotos.search.view.state.SearchState
import com.savvasdalkitsis.librephotos.userbadge.usecase.UserBadgeUseCase
import com.savvasdalkitsis.librephotos.viewmodel.Handler
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@FlowPreview
class SearchHandler @Inject constructor(
    private val searchUseCase: SearchUseCase,
    private val userBadgeUseCase: UserBadgeUseCase,
    private val accountUseCase: AccountUseCase,
): Handler<SearchState, SearchEffect, SearchAction, SearchMutation> {

    private var lastSearch: Job? = null

    override fun invoke(
        state: SearchState,
        action: SearchAction,
        effect: suspend (SearchEffect) -> Unit,
    ): Flow<SearchMutation> = when (action) {
        SearchAction.Initialise -> userBadgeUseCase.getUserBadgeState()
            .map(::UserBadgeStateChanged)
            .onStart {
                effect(FocusSearchBar)
            }
        is SearchAction.ChangeQuery -> flowOf(QueryChanged(action.query))
        is SearchAction.SearchFor -> channelFlow {
            lastSearch?.cancel()
            send(SearchStarted)
            effect(HideKeyboard)
            lastSearch = CoroutineScope(currentCoroutineContext()).launch {
                searchUseCase.searchFor(state.query)
                    .debounce(200)
                    .map { albums ->
                        when {
                            albums.isEmpty() -> SearchStarted
                            else -> SearchResultsUpdated(albums)
                        }
                    }
                    .cancellable()
                    .collect { send(it) }
            }
        }
        is SearchAction.ChangeFocus -> flowOf(FocusChanged(action.focused))
        SearchAction.ClearSearch -> flowOf(SearchCleared)
        SearchAction.UserBadgePressed -> flowOf(ShowAccountOverview)
        SearchAction.DismissAccountOverview -> flowOf(HideAccountOverview)
        SearchAction.LogOut -> flow {
            accountUseCase.logOut()
            effect(SearchEffect.ReloadApp)
        }
    }

}
