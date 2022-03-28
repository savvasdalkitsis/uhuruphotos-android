package com.savvasdalkitsis.librephotos.search.viewmodel

import com.savvasdalkitsis.librephotos.search.mvflow.SearchAction
import com.savvasdalkitsis.librephotos.search.mvflow.SearchEffect
import com.savvasdalkitsis.librephotos.search.mvflow.SearchMutation
import com.savvasdalkitsis.librephotos.search.view.state.SearchState
import com.savvasdalkitsis.librephotos.viewmodel.MVFlowViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    searchHandler: SearchHandler,
) : MVFlowViewModel<SearchState, SearchAction, SearchMutation, SearchEffect>(
    searchHandler,
    SearchReducer(),
    SearchState(),
    SearchAction.Initialise,
)