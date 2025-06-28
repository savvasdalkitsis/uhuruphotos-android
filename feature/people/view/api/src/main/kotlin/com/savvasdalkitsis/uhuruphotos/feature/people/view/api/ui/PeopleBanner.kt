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
@file:OptIn(ExperimentalSharedTransitionApi::class)

package com.savvasdalkitsis.uhuruphotos.feature.people.view.api.ui

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.savvasdalkitsis.uhuruphotos.feature.people.view.api.ui.state.PersonState
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.SectionHeader
import kotlinx.collections.immutable.ImmutableList
import org.jetbrains.compose.resources.stringResource
import uhuruphotos_android.foundation.strings.api.generated.resources.Res.string
import uhuruphotos_android.foundation.strings.api.generated.resources.people
import uhuruphotos_android.foundation.strings.api.generated.resources.view_all

@Composable
fun SharedTransitionScope.PeopleBanner(
    modifier: Modifier = Modifier,
    people: ImmutableList<PersonState>,
    headerPadding: PaddingValues = PaddingValues(),
    onViewAllClicked: (() -> Unit)? = null,
    onPersonSelected: (PersonState) -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        SectionHeader(
            modifier = Modifier.padding(headerPadding),
            title = stringResource(string.people),
        ) {
            if (onViewAllClicked != null) {
                TextButton(onClick = onViewAllClicked) {
                    Text(stringResource(string.view_all))
                }
            }
        }
        LazyRow(horizontalArrangement = Arrangement.spacedBy(0.dp)) {
            for (person in people) {
                item(contentType = "spacer") { Spacer(modifier = Modifier.width(4.dp)) }
                item(person.id) {
                    PersonThumbnail(
                        modifier = Modifier
                            .width(94.dp),
                        personState = person,
                        onPersonSelected = { onPersonSelected(person) },
                    )
                }
                item(contentType = "spacer") { Spacer(modifier = Modifier.width(4.dp)) }
            }
        }
    }
}