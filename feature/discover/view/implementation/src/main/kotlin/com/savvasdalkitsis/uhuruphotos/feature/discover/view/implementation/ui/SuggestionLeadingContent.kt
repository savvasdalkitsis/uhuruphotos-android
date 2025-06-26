/*
Copyright 2024 Savvas Dalkitsis

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

package com.savvasdalkitsis.uhuruphotos.feature.discover.view.implementation.ui

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.savvasdalkitsis.uhuruphotos.feature.catalogue.auto.view.api.ui.AutoAlbumItem
import com.savvasdalkitsis.uhuruphotos.feature.catalogue.user.view.api.state.toUserAlbumState
import com.savvasdalkitsis.uhuruphotos.feature.catalogue.user.view.api.ui.UserAlbumItem
import com.savvasdalkitsis.uhuruphotos.feature.discover.view.implementation.ui.state.AutoAlbumSearchSuggestionState
import com.savvasdalkitsis.uhuruphotos.feature.discover.view.implementation.ui.state.PersonSearchSuggestionState
import com.savvasdalkitsis.uhuruphotos.feature.discover.view.implementation.ui.state.RecentSearchSuggestionState
import com.savvasdalkitsis.uhuruphotos.feature.discover.view.implementation.ui.state.ServerSearchSuggestionState
import com.savvasdalkitsis.uhuruphotos.feature.discover.view.implementation.ui.state.UserAlbumSearchSuggestionState
import com.savvasdalkitsis.uhuruphotos.feature.people.view.api.ui.PersonImage
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.icon.UhuruIcon
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.search.SearchSuggestion
import kotlinx.collections.immutable.persistentMapOf
import uhuruphotos_android.foundation.icons.api.generated.resources.Res.drawable
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_assistant
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_creation
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_history

val suggestionLeadingContent = persistentMapOf<String, @Composable SharedTransitionScope.(SearchSuggestion) -> Unit>(
    RecentSearchSuggestionState.TYPE to {
        UhuruIcon(
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp),
            icon = drawable.ic_history,
        )
    },
    ServerSearchSuggestionState.TYPE to {
        UhuruIcon(
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp),
            icon = drawable.ic_assistant,
        )
    },
    PersonSearchSuggestionState.TYPE to {
        (it as? PersonSearchSuggestionState)?.personState?.let { person ->
            PersonImage(
                modifier = Modifier.fillMaxSize(),
                shape = CircleShape,
                personState = person
            )
        }
    },
    UserAlbumSearchSuggestionState.TYPE to {
        (it as? UserAlbumSearchSuggestionState)?.userAlbums?.let { album ->
            UserAlbumItem(
                album = album.toUserAlbumState(),
                shape = RoundedCornerShape(8.dp),
                miniIcons = true,
                onAlbumSelected = {},
            )
        }
    },
    AutoAlbumSearchSuggestionState.TYPE to {
        (it as? AutoAlbumSearchSuggestionState)?.autoAlbum?.let { album ->
            Box {
                AutoAlbumItem(
                    album = album,
                    shape = RoundedCornerShape(8.dp),
                    miniIcons = true,
                    onAlbumSelected = {},
                )
                Box(
                    modifier = Modifier
                        .padding(2.dp)
                        .align(Alignment.TopEnd)
                ) {
                    UhuruIcon(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceContainerLow)
                            .padding(2.dp),
                        icon = drawable.ic_creation,
                    )
                }
            }
        }
    },
)
