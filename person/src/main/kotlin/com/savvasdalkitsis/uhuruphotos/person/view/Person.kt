package com.savvasdalkitsis.uhuruphotos.person.view

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.savvasdalkitsis.uhuruphotos.feed.view.Feed
import com.savvasdalkitsis.uhuruphotos.image.api.view.Image
import com.savvasdalkitsis.uhuruphotos.person.view.state.PersonState
import com.savvasdalkitsis.uhuruphotos.person.viewmodel.PersonAction
import com.savvasdalkitsis.uhuruphotos.person.viewmodel.PersonAction.*
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
    ) { contentPadding ->
        if (state.feedState.isLoading && state.feedState.albums.isEmpty()) {
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