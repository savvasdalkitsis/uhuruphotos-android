package com.savvasdalkitsis.librephotos.search.viewmodel

import com.savvasdalkitsis.librephotos.search.mvflow.SearchAction
import com.savvasdalkitsis.librephotos.search.mvflow.SearchEffect
import com.savvasdalkitsis.librephotos.search.mvflow.SearchEffect.FocusSearchBar
import com.savvasdalkitsis.librephotos.search.mvflow.SearchEffect.HideKeyboard
import com.savvasdalkitsis.librephotos.search.mvflow.SearchMutation
import com.savvasdalkitsis.librephotos.search.mvflow.SearchMutation.*
import com.savvasdalkitsis.librephotos.search.usecase.SearchUseCase
import com.savvasdalkitsis.librephotos.search.view.state.SearchState
import com.savvasdalkitsis.librephotos.userbadge.usecase.UserBadgeUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import net.pedroloureiro.mvflow.EffectSender
import net.pedroloureiro.mvflow.HandlerWithEffects
import javax.inject.Inject

class SearchHandler @Inject constructor(
    private val searchUseCase: SearchUseCase,
    private val userBadgeUseCase: UserBadgeUseCase,
): HandlerWithEffects<SearchState, SearchAction, SearchMutation, SearchEffect> {

    private var lastSearch: Job? = null

    override fun invoke(
        state: SearchState,
        action: SearchAction,
        effect: EffectSender<SearchEffect>
    ): Flow<SearchMutation> = when (action) {
        SearchAction.Initialise -> flow {
            effect.send(FocusSearchBar)
            emitAll(userBadgeUseCase.getUserBadgeState().map(::UserBadgeStateChanged))
        }
        is SearchAction.ChangeQuery -> flowOf(QueryChanged(action.query))
        is SearchAction.SearchFor -> channelFlow {
            lastSearch?.cancel()
            send(SearchStarted)
            effect.send(HideKeyboard)
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
    }

}
