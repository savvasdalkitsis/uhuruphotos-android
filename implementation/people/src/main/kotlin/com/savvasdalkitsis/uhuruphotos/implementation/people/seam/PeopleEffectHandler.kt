/*
Copyright 2022 Savvas Dalkitsis

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
package com.savvasdalkitsis.uhuruphotos.implementation.people.seam

import com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.Navigator
import com.savvasdalkitsis.uhuruphotos.api.person.navigation.PersonNavigationTarget
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.EffectHandler
import com.savvasdalkitsis.uhuruphotos.foundation.strings.api.R.string
import com.savvasdalkitsis.uhuruphotos.foundation.toaster.api.Toaster
import com.savvasdalkitsis.uhuruphotos.implementation.people.seam.PeopleEffect.ErrorLoadingPeople
import com.savvasdalkitsis.uhuruphotos.implementation.people.seam.PeopleEffect.NavigateBack
import com.savvasdalkitsis.uhuruphotos.implementation.people.seam.PeopleEffect.NavigateToPerson
import javax.inject.Inject

class PeopleEffectHandler @Inject constructor(
    private val navigator: Navigator,
    private val toaster: Toaster,
) : EffectHandler<PeopleEffect> {

    override suspend fun handleEffect(effect: PeopleEffect) {
        when (effect) {
            ErrorLoadingPeople -> toaster.show(string.error_refreshing_people)
            NavigateBack -> navigator.navigateBack()
            is NavigateToPerson -> navigator.navigateTo(
                PersonNavigationTarget.name(effect.person.id)
            )
        }
    }

}
