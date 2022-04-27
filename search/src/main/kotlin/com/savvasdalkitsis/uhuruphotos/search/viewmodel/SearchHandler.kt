package com.savvasdalkitsis.uhuruphotos.search.viewmodel

import com.savvasdalkitsis.uhuruphotos.account.usecase.AccountUseCase
import com.savvasdalkitsis.uhuruphotos.feedpage.usecase.FeedPageUseCase
import com.savvasdalkitsis.uhuruphotos.search.mvflow.SearchAction
import com.savvasdalkitsis.uhuruphotos.search.mvflow.SearchAction.*
import com.savvasdalkitsis.uhuruphotos.search.mvflow.SearchAction.ChangeDisplay
import com.savvasdalkitsis.uhuruphotos.search.mvflow.SearchEffect
import com.savvasdalkitsis.uhuruphotos.search.mvflow.SearchEffect.*
import com.savvasdalkitsis.uhuruphotos.search.mvflow.SearchMutation
import com.savvasdalkitsis.uhuruphotos.search.mvflow.SearchMutation.*
import com.savvasdalkitsis.uhuruphotos.search.usecase.SearchUseCase
import com.savvasdalkitsis.uhuruphotos.search.view.state.SearchState
import com.savvasdalkitsis.uhuruphotos.userbadge.api.UserBadgeUseCase
import com.savvasdalkitsis.uhuruphotos.viewmodel.Handler
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject

class SearchHandler @Inject constructor(
    private val searchUseCase: SearchUseCase,
    private val userBadgeUseCase: UserBadgeUseCase,
    private val accountUseCase: AccountUseCase,
    private val feedPageUseCase: FeedPageUseCase,
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
    }
}
