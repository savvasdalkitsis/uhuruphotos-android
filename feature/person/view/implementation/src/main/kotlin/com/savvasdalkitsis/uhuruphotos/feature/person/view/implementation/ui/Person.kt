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

package com.savvasdalkitsis.uhuruphotos.feature.person.view.implementation.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.savvasdalkitsis.uhuruphotos.feature.collage.view.api.ui.Collage
import com.savvasdalkitsis.uhuruphotos.feature.collage.view.api.ui.CollageDisplayActionButton
import com.savvasdalkitsis.uhuruphotos.feature.people.view.api.ui.PersonImage
import com.savvasdalkitsis.uhuruphotos.feature.person.view.implementation.seam.actions.ChangeDisplay
import com.savvasdalkitsis.uhuruphotos.feature.person.view.implementation.seam.actions.PersonAction
import com.savvasdalkitsis.uhuruphotos.feature.person.view.implementation.seam.actions.SelectedCel
import com.savvasdalkitsis.uhuruphotos.feature.person.view.implementation.ui.state.PersonCollageState
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.UhuruFullLoading
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.scaffold.UhuruScaffold
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.scaffold.UhuruUpNavButton

@Composable
fun SharedTransitionScope.Person(
    state: PersonCollageState,
    action: (PersonAction) -> Unit,
) {
    UhuruScaffold(
        title = {
            val person = state.personState
            when {
                person != null -> {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = spacedBy(8.dp),
                    ) {
                        PersonImage(
                            modifier = Modifier
                                .width(48.dp),
                            shape = CircleShape,
                            personState = person,
                        )
                        Text(text = person.name)
                    }
                }
                else -> Text("Loading person")
            }
        },
        navigationIcon = {
            UhuruUpNavButton()
        },
        actionBarContent = {
            AnimatedVisibility(
                visible = state.collageState.clusters.isNotEmpty() && state.collageState.isLoading
            ) {
                CircularProgressIndicator()
            }
            AnimatedVisibility(state.collageState.clusters.isNotEmpty()) {
                CollageDisplayActionButton(
                    onChange = { action(ChangeDisplay(it)) },
                    currentCollageDisplayState = state.collageState.collageDisplayState
                )
            }
        }
    ) { contentPadding ->
        if (state.collageState.clusters.isEmpty()) {
            UhuruFullLoading()
        } else {
            Collage(
                contentPadding = contentPadding,
                state = state.collageState,
                showStickyHeaders = true,
                showScrollbarHint = true,
                onCelSelected = { cel ->
                    action(SelectedCel(cel))
                },
                onChangeDisplay = { action(ChangeDisplay(it)) },
            )
        }
    }
}