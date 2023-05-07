/*
Copyright 2023 Savvas Dalkitsis

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
package com.savvasdalkitsis.uhuruphotos.feature.people.view.implementation.seam.actions

import com.savvasdalkitsis.uhuruphotos.feature.people.view.implementation.seam.PeopleActionsContext
import com.savvasdalkitsis.uhuruphotos.feature.people.view.implementation.seam.effects.PeopleEffect
import com.savvasdalkitsis.uhuruphotos.feature.people.view.implementation.seam.PeopleMutation
import com.savvasdalkitsis.uhuruphotos.feature.people.view.implementation.seam.effects.NavigateBack
import com.savvasdalkitsis.uhuruphotos.feature.people.view.implementation.ui.state.PeopleState
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.EffectHandler
import kotlinx.coroutines.flow.flow

object NavigateBack : PeopleAction() {
    context(PeopleActionsContext) override fun handle(
        state: PeopleState,
        effect: EffectHandler<PeopleEffect>
    ) = flow<PeopleMutation> {
        effect.handleEffect(NavigateBack)
    }
}
