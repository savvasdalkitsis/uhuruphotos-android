package com.savvasdalkitsis.librephotos.search.viewmodel

import com.savvasdalkitsis.librephotos.search.mvflow.SearchAction
import com.savvasdalkitsis.librephotos.search.mvflow.SearchEffect
import com.savvasdalkitsis.librephotos.search.mvflow.SearchEffect.FocusSearchBar
import com.savvasdalkitsis.librephotos.search.mvflow.SearchEffect.HideKeyboard
import com.savvasdalkitsis.librephotos.search.mvflow.SearchMutation
import com.savvasdalkitsis.librephotos.search.mvflow.SearchMutation.*
import com.savvasdalkitsis.librephotos.search.view.SearchState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import net.pedroloureiro.mvflow.EffectSender
import net.pedroloureiro.mvflow.HandlerWithEffects
import javax.inject.Inject

class SearchHandler @Inject constructor(

): HandlerWithEffects<SearchState, SearchAction, SearchMutation, SearchEffect> {

    override fun invoke(
        state: SearchState,
        action: SearchAction,
        effect: EffectSender<SearchEffect>
    ): Flow<SearchMutation> = when (action) {
        SearchAction.Initialise -> flow {
            effect.send(FocusSearchBar)
        }
        is SearchAction.ChangeQuery -> flowOf(QueryChanged(action.query))
        is SearchAction.SearchFor -> flow {
            effect.send(HideKeyboard)
        }
        is SearchAction.ChangeFocus -> flowOf(FocusChanged(action.focused))
        SearchAction.ClearSearch -> flowOf(SearchCleared)
    }

}
