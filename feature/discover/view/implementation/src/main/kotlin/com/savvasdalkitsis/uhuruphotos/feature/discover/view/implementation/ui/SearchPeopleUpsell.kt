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
package com.savvasdalkitsis.uhuruphotos.feature.discover.view.implementation.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.dp
import com.savvasdalkitsis.uhuruphotos.feature.discover.view.implementation.seam.actions.DiscoverAction
import com.savvasdalkitsis.uhuruphotos.feature.discover.view.implementation.seam.actions.UpsellLoginFromPeople
import com.savvasdalkitsis.uhuruphotos.feature.people.view.api.ui.PeopleBanner
import com.savvasdalkitsis.uhuruphotos.feature.people.view.api.ui.state.PersonState
import kotlinx.collections.immutable.toImmutableList
import org.jetbrains.compose.resources.stringResource
import uhuruphotos_android.foundation.strings.api.generated.resources.Res.string
import uhuruphotos_android.foundation.strings.api.generated.resources.person

@Composable
fun SearchPeopleUpsell(
    action: (DiscoverAction) -> Unit,
) {
    val name = stringResource(string.person)
    PeopleBanner(
        people = remember {
            List(5) {
                PersonState(
                    name = name,
                    imageUrl = null,
                    photos = 0,
                    id = it,
                )
            }.toImmutableList()
        },
        headerPadding = PaddingValues(horizontal = 12.dp),
        onViewAllClicked = { action(UpsellLoginFromPeople) },
        onPersonSelected = { action(UpsellLoginFromPeople) },
    )
}