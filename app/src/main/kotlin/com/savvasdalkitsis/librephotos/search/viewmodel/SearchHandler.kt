package com.savvasdalkitsis.librephotos.search.viewmodel

import coil.annotation.ExperimentalCoilApi
import com.savvasdalkitsis.librephotos.account.usecase.AccountUseCase
import com.savvasdalkitsis.librephotos.feed.mvflow.FeedPageMutation
import com.savvasdalkitsis.librephotos.feed.usecase.FeedUseCase
import com.savvasdalkitsis.librephotos.search.mvflow.SearchAction
import com.savvasdalkitsis.librephotos.search.mvflow.SearchAction.*
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

@ExperimentalCoroutinesApi
@ExperimentalCoilApi
@FlowPreview
class SearchHandler @Inject constructor(
    private val searchUseCase: SearchUseCase,
    private val userBadgeUseCase: UserBadgeUseCase,
    private val accountUseCase: AccountUseCase,
    private val feedUseCase: FeedUseCase,
): Handler<SearchState, SearchEffect, SearchAction, SearchMutation> {

    private var lastSearch: Job? = null

    override fun invoke(
        state: SearchState,
        action: SearchAction,
        effect: suspend (SearchEffect) -> Unit,
    ): Flow<SearchMutation> = when (action) {
        Initialise -> merge(
            feedUseCase
                .getFeedDisplay()
                .distinctUntilChanged()
                .map(::ChangeDisplay),
            userBadgeUseCase.getUserBadgeState()
                .map(::UserBadgeStateChanged)
        ).onStart {
            effect(FocusSearchBar)
        }
        is ChangeQuery -> flowOf(QueryChanged(action.query))
        is SearchFor -> channelFlow {
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
        is ChangeFocus -> flowOf(FocusChanged(action.focused))
        ClearSearch -> flowOf(SearchCleared)
        UserBadgePressed -> flowOf(ShowAccountOverview)
        DismissAccountOverview -> flowOf(HideAccountOverview)
        LogOut -> flow {
            accountUseCase.logOut()
            effect(SearchEffect.ReloadApp)
        }
    }

}
