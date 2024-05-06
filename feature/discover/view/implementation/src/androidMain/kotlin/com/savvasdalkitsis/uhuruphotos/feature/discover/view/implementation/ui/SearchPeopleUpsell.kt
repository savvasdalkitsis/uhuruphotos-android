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
import dev.icerock.moko.resources.compose.stringResource
import androidx.compose.ui.unit.dp
import com.savvasdalkitsis.uhuruphotos.feature.discover.view.implementation.seam.actions.DiscoverAction
import com.savvasdalkitsis.uhuruphotos.feature.discover.view.implementation.seam.actions.UpsellLoginFromPeople
import com.savvasdalkitsis.uhuruphotos.feature.people.view.api.ui.PeopleBanner
import com.savvasdalkitsis.uhuruphotos.feature.people.view.api.ui.state.Person
import com.savvasdalkitsis.uhuruphotos.foundation.strings.api.Res.strings
import kotlinx.collections.immutable.toPersistentList

@Composable
fun SearchPeopleUpsell(
    action: (DiscoverAction) -> Unit,
) {
    val name = stringResource(strings.person)
    PeopleBanner(
        people = remember {
            List(5) {
                Person(
                    name = name,
                    imageUrl = null,
                    photos = 0,
                    id = it,
                )
            }.toPersistentList()
        },
        headerPadding = PaddingValues(horizontal = 12.dp),
        onViewAllClicked = { action(UpsellLoginFromPeople) },
        onPersonSelected = { action(UpsellLoginFromPeople) },
    )
}