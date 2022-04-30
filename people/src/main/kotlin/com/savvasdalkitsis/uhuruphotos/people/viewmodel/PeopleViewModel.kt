package com.savvasdalkitsis.uhuruphotos.people.viewmodel

import androidx.lifecycle.ViewModel
import com.savvasdalkitsis.uhuruphotos.people.view.state.PeopleState
import com.savvasdalkitsis.uhuruphotos.viewmodel.ActionReceiver
import com.savvasdalkitsis.uhuruphotos.viewmodel.ActionReceiverHost
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PeopleViewModel @Inject constructor(
    peopleHandler: PeopleHandler,
    reducer: PeopleReducer,
) : ViewModel(),
    ActionReceiverHost<PeopleState, PeopleEffect, PeopleAction, PeopleMutation> {

    override val actionReceiver = ActionReceiver(
        peopleHandler,
        reducer,
        PeopleState()
    )

}