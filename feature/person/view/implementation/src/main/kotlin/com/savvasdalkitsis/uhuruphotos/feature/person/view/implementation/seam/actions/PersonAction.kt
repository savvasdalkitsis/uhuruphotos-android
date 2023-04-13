package com.savvasdalkitsis.uhuruphotos.feature.person.view.implementation.seam.actions

import com.savvasdalkitsis.uhuruphotos.feature.person.view.implementation.seam.PersonActionsContext
import com.savvasdalkitsis.uhuruphotos.feature.person.view.implementation.seam.PersonEffect
import com.savvasdalkitsis.uhuruphotos.feature.person.view.implementation.seam.PersonMutation
import com.savvasdalkitsis.uhuruphotos.feature.person.view.implementation.ui.state.PersonState
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.Action

sealed class PersonAction : Action<PersonState, PersonEffect, PersonMutation, PersonActionsContext>