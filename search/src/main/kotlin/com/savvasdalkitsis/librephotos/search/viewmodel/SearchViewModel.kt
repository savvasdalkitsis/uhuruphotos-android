package com.savvasdalkitsis.librephotos.search.viewmodel

import androidx.lifecycle.ViewModel
import com.savvasdalkitsis.librephotos.search.mvflow.SearchAction
import com.savvasdalkitsis.librephotos.search.mvflow.SearchEffect
import com.savvasdalkitsis.librephotos.search.mvflow.SearchMutation
import com.savvasdalkitsis.librephotos.search.view.state.SearchState
import com.savvasdalkitsis.librephotos.viewmodel.ActionReceiver
import com.savvasdalkitsis.librephotos.viewmodel.ActionReceiverHost
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    searchHandler: SearchHandler,
) : ViewModel(),
    ActionReceiverHost<SearchState, SearchEffect, SearchAction, SearchMutation> {

    override val actionReceiver = ActionReceiver(
        searchHandler,
        searchReducer(),
        SearchState(),
    )
}