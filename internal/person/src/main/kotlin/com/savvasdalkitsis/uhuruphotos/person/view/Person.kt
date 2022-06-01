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
package com.savvasdalkitsis.uhuruphotos.person.view

import androidx.compose.animation.AnimatedContent
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
import com.savvasdalkitsis.uhuruphotos.feed.view.Feed
import com.savvasdalkitsis.uhuruphotos.feed.view.FeedDisplayActionButton
import com.savvasdalkitsis.uhuruphotos.image.api.view.Image
import com.savvasdalkitsis.uhuruphotos.person.view.state.PersonState
import com.savvasdalkitsis.uhuruphotos.person.seam.PersonAction
import com.savvasdalkitsis.uhuruphotos.person.seam.PersonAction.ChangeDisplay
import com.savvasdalkitsis.uhuruphotos.person.seam.PersonAction.NavigateBack
import com.savvasdalkitsis.uhuruphotos.person.seam.PersonAction.SelectedPhoto
import com.savvasdalkitsis.uhuruphotos.ui.view.BackNavButton
import com.savvasdalkitsis.uhuruphotos.ui.view.CommonScaffold
import com.savvasdalkitsis.uhuruphotos.ui.view.FullProgressBar

@Composable
fun Person(
    state: PersonState,
    action: (PersonAction) -> Unit,
) {
    CommonScaffold(
        title = {
            AnimatedContent(targetState = state.person) { person ->
                when {
                    person != null -> Text(text = person.name)
                    else -> Text("Loading person")
                }
            }
        },
        navigationIcon = {
            BackNavButton(furtherContent = {
                AnimatedVisibility(visible = state.person != null) {
                    Image(
                        modifier = Modifier
                            .width(32.dp)
                            .aspectRatio(1f)
                            .clip(CircleShape),
                        url = state.person?.imageUrl,
                        contentScale = ContentScale.Crop,
                        contentDescription = null
                    )
                }
            }) {
                action(NavigateBack)
            }
        },
        actionBarContent = {
            AnimatedVisibility(
                visible = state.feedState.albums.isNotEmpty() && state.feedState.isLoading
            ) {
                CircularProgressIndicator()
            }
            AnimatedVisibility(state.feedState.albums.isNotEmpty()) {
                FeedDisplayActionButton(
                    onChange = { action(ChangeDisplay(it)) },
                    currentFeedDisplay = state.feedState.feedDisplay
                )
            }
        }
    ) { contentPadding ->
        if (state.feedState.albums.isEmpty()) {
            FullProgressBar()
        } else {
            Feed(
                contentPadding = contentPadding,
                state = state.feedState,
                onPhotoSelected = { photo, center, scale ->
                    action(SelectedPhoto(photo, center, scale))
                },
                onChangeDisplay = { action(ChangeDisplay(it)) },
            )
        }
    }
}