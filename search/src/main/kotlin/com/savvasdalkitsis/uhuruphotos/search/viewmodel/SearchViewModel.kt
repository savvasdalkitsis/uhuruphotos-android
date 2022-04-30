package com.savvasdalkitsis.uhuruphotos.search.viewmodel

import androidx.lifecycle.ViewModel
import com.savvasdalkitsis.uhuruphotos.search.mvflow.SearchAction
import com.savvasdalkitsis.uhuruphotos.search.mvflow.SearchEffect
import com.savvasdalkitsis.uhuruphotos.search.mvflow.SearchMutation
import com.savvasdalkitsis.uhuruphotos.search.view.state.SearchState
import com.savvasdalkitsis.uhuruphotos.viewmodel.ActionReceiver
import com.savvasdalkitsis.uhuruphotos.viewmodel.ActionReceiverHost
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    searchHandler: SearchHandler,
    searchReducer: SearchReducer,
) : ViewModel(),
    ActionReceiverHost<SearchState, SearchEffect, SearchAction, SearchMutation> {

    override val actionReceiver = ActionReceiver(
        searchHandler,
        searchReducer,
        SearchState(),
    )
}