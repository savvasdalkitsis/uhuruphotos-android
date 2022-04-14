package com.savvasdalkitsis.librephotos.search.viewmodel

import androidx.lifecycle.ViewModel
import coil.annotation.ExperimentalCoilApi
import com.savvasdalkitsis.librephotos.search.mvflow.SearchAction
import com.savvasdalkitsis.librephotos.search.mvflow.SearchEffect
import com.savvasdalkitsis.librephotos.search.mvflow.SearchMutation
import com.savvasdalkitsis.librephotos.search.view.state.SearchState
import com.savvasdalkitsis.librephotos.viewmodel.ActionReceiverHost
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@FlowPreview
@ExperimentalCoilApi
@ExperimentalCoroutinesApi
@HiltViewModel
class SearchViewModel @Inject constructor(
    searchHandler: SearchHandler,
) : ViewModel(),
    ActionReceiverHost<SearchState, SearchEffect, SearchAction, SearchMutation> {

    override val initialState = SearchState()

    override val actionReceiver = com.savvasdalkitsis.librephotos.viewmodel.ActionReceiver(
        searchHandler,
        searchReducer(),
        container(initialState)
    )
}