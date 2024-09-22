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
package com.savvasdalkitsis.uhuruphotos.feature.person.view.implementation.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.savvasdalkitsis.uhuruphotos.feature.collage.view.api.ui.Collage
import com.savvasdalkitsis.uhuruphotos.feature.collage.view.api.ui.CollageDisplayActionButton
import com.savvasdalkitsis.uhuruphotos.feature.person.view.implementation.seam.actions.ChangeDisplay
import com.savvasdalkitsis.uhuruphotos.feature.person.view.implementation.seam.actions.PersonAction
import com.savvasdalkitsis.uhuruphotos.feature.person.view.implementation.seam.actions.SelectedCel
import com.savvasdalkitsis.uhuruphotos.feature.person.view.implementation.ui.state.PersonState
import com.savvasdalkitsis.uhuruphotos.foundation.image.api.ui.Thumbnail
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.FullLoading
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.scaffold.CommonScaffold
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.scaffold.UpNavButton

@Composable
fun Person(
    state: PersonState,
    action: (PersonAction) -> Unit,
) {
    CommonScaffold(
        title = {
            val person = state.personState
            when {
                person != null -> Text(text = person.name)
                else -> Text("Loading person")
            }
        },
        navigationIcon = {
            UpNavButton(furtherContent = {
                AnimatedVisibility(visible = state.personState != null) {
                    Thumbnail(
                        modifier = Modifier
                            .width(32.dp)
                            .aspectRatio(1f)
                            .clip(CircleShape),
                        url = state.personState?.imageUrl,
                        contentScale = ContentScale.Crop,
                        contentDescription = null
                    )
                }
            })
        },
        actionBarContent = {
            AnimatedVisibility(
                visible = state.collageState.clusterStates.isNotEmpty() && state.collageState.isLoading
            ) {
                CircularProgressIndicator()
            }
            AnimatedVisibility(state.collageState.clusterStates.isNotEmpty()) {
                CollageDisplayActionButton(
                    onChange = { action(ChangeDisplay(it)) },
                    currentCollageDisplayState = state.collageState.collageDisplayState
                )
            }
        }
    ) { contentPadding ->
        if (state.collageState.clusterStates.isEmpty()) {
            FullLoading()
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