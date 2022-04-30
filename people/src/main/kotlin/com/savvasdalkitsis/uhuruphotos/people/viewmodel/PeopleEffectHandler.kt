package com.savvasdalkitsis.uhuruphotos.people.viewmodel

import com.savvasdalkitsis.uhuruphotos.navigation.ControllersProvider
import com.savvasdalkitsis.uhuruphotos.people.viewmodel.PeopleEffect.*
import com.savvasdalkitsis.uhuruphotos.person.api.navigation.PersonNavigationTarget
import com.savvasdalkitsis.uhuruphotos.toaster.Toaster
import com.savvasdalkitsis.uhuruphotos.viewmodel.EffectHandler
import javax.inject.Inject

class PeopleEffectHandler @Inject constructor(
    private val controllersProvider: ControllersProvider,
    private val toaster: Toaster,
) : EffectHandler<PeopleEffect> {

    override suspend fun invoke(effect: PeopleEffect) {
        when (effect) {
            ErrorLoadingPeople -> toaster.show("There was an error refreshing people")
            NavigateBack -> controllersProvider.navController!!.popBackStack()
            is NavigateToPerson -> controllersProvider.navController!!.navigate(
                PersonNavigationTarget.name(effect.person.id)
            )
        }
    }

}
