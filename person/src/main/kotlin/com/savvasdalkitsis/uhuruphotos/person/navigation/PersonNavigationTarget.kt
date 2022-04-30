package com.savvasdalkitsis.uhuruphotos.person.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import com.savvasdalkitsis.uhuruphotos.navigation.NavigationTarget
import com.savvasdalkitsis.uhuruphotos.navigation.navigationTarget
import com.savvasdalkitsis.uhuruphotos.person.api.navigation.PersonNavigationTarget
import com.savvasdalkitsis.uhuruphotos.person.api.navigation.PersonNavigationTarget.personId
import com.savvasdalkitsis.uhuruphotos.person.view.Person
import com.savvasdalkitsis.uhuruphotos.person.view.state.PersonState
import com.savvasdalkitsis.uhuruphotos.person.viewmodel.PersonAction
import com.savvasdalkitsis.uhuruphotos.person.viewmodel.PersonAction.LoadPerson
import com.savvasdalkitsis.uhuruphotos.person.viewmodel.PersonEffect
import com.savvasdalkitsis.uhuruphotos.person.viewmodel.PersonEffectHandler
import com.savvasdalkitsis.uhuruphotos.person.viewmodel.PersonViewModel
import com.savvasdalkitsis.uhuruphotos.settings.usecase.SettingsUseCase
import javax.inject.Inject

class PersonNavigationTarget @Inject constructor(
    private val settingsUseCase: SettingsUseCase,
    private val effectHandler: PersonEffectHandler,
) : NavigationTarget {

    override suspend fun NavGraphBuilder.create() {
        navigationTarget<PersonState, PersonEffect, PersonAction, PersonViewModel>(
            name = PersonNavigationTarget.registrationName,
            themeMode = settingsUseCase.observeThemeModeState(),
            effects = effectHandler,
            initializer = { navBackStackEntry, action -> action(LoadPerson(navBackStackEntry.personId)) },
            createModel = { hiltViewModel() },
        ) { state, actions ->
            Person(state, actions)
        }
    }
}