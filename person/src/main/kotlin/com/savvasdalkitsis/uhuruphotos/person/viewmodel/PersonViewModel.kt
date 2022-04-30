package com.savvasdalkitsis.uhuruphotos.person.viewmodel

import androidx.lifecycle.ViewModel
import com.savvasdalkitsis.uhuruphotos.person.view.state.PersonState
import com.savvasdalkitsis.uhuruphotos.viewmodel.ActionReceiver
import com.savvasdalkitsis.uhuruphotos.viewmodel.ActionReceiverHost
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PersonViewModel @Inject constructor(
    personHandler: PersonHandler,
    reducer: PersonReducer,
) : ViewModel(),
    ActionReceiverHost<PersonState, PersonEffect, PersonAction, PersonMutation> {

    override val actionReceiver = ActionReceiver(
        personHandler,
        reducer,
        PersonState()
    )

}