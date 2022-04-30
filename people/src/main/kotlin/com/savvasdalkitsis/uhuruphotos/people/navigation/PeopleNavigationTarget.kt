package com.savvasdalkitsis.uhuruphotos.people.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import com.savvasdalkitsis.uhuruphotos.navigation.NavigationTarget
import com.savvasdalkitsis.uhuruphotos.navigation.navigationTarget
import com.savvasdalkitsis.uhuruphotos.people.view.People
import com.savvasdalkitsis.uhuruphotos.people.view.state.PeopleState
import com.savvasdalkitsis.uhuruphotos.people.viewmodel.PeopleAction
import com.savvasdalkitsis.uhuruphotos.people.viewmodel.PeopleAction.LoadPeople
import com.savvasdalkitsis.uhuruphotos.people.viewmodel.PeopleEffect
import com.savvasdalkitsis.uhuruphotos.people.viewmodel.PeopleEffectHandler
import com.savvasdalkitsis.uhuruphotos.people.viewmodel.PeopleViewModel
import com.savvasdalkitsis.uhuruphotos.settings.usecase.SettingsUseCase
import com.savvasdalkitsis.uhuruphotos.viewmodel.noOp
import javax.inject.Inject

class PeopleNavigationTarget @Inject constructor(
    private val settingsUseCase: SettingsUseCase,
    private val effectHandler: PeopleEffectHandler,
) : NavigationTarget {

    override suspend fun NavGraphBuilder.create() {
        navigationTarget<PeopleState, PeopleEffect, PeopleAction, PeopleViewModel>(
            name = name,
            themeMode = settingsUseCase.observeThemeModeState(),
            effects = effectHandler,
            initializer = { _, action -> action(LoadPeople) },
            createModel = { hiltViewModel() },
        ) { state, actions ->
            People(state, actions)
        }
    }

    companion object {
        const val name = "people"
    }
}