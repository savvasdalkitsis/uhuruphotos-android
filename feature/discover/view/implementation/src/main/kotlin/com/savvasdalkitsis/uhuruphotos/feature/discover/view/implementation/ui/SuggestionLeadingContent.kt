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
package com.savvasdalkitsis.uhuruphotos.feature.discover.view.implementation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.savvasdalkitsis.uhuruphotos.feature.catalogue.auto.view.api.ui.AutoAlbumItem
import com.savvasdalkitsis.uhuruphotos.feature.catalogue.user.view.api.state.toUserAlbumState
import com.savvasdalkitsis.uhuruphotos.feature.catalogue.user.view.api.ui.UserAlbumItem
import com.savvasdalkitsis.uhuruphotos.feature.discover.view.implementation.ui.state.AutoAlbumSearchSuggestion
import com.savvasdalkitsis.uhuruphotos.feature.discover.view.implementation.ui.state.PersonSearchSuggestion
import com.savvasdalkitsis.uhuruphotos.feature.discover.view.implementation.ui.state.RecentSearchSuggestion
import com.savvasdalkitsis.uhuruphotos.feature.discover.view.implementation.ui.state.ServerSearchSuggestion
import com.savvasdalkitsis.uhuruphotos.feature.discover.view.implementation.ui.state.UserAlbumSearchSuggestion
import com.savvasdalkitsis.uhuruphotos.feature.people.view.api.ui.PersonImage
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.R
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.search.SearchSuggestion
import kotlinx.collections.immutable.persistentMapOf

val suggestionLeadingContent = persistentMapOf<String, @Composable (SearchSuggestion) -> Unit>(
    RecentSearchSuggestion.TYPE to {
        Icon(
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp),
            painter = painterResource(id = R.drawable.ic_history),
            contentDescription = null
        )
    },
    ServerSearchSuggestion.TYPE to {
        Icon(
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp),
            painter = painterResource(id = R.drawable.ic_assistant),
            contentDescription = null
        )
    },
    PersonSearchSuggestion.TYPE to {
        (it as? PersonSearchSuggestion)?.person?.let { person ->
            PersonImage(
                modifier = Modifier.fillMaxSize(),
                shape = CircleShape,
                person = person
            )
        }
    },
    UserAlbumSearchSuggestion.TYPE to {
        (it as? UserAlbumSearchSuggestion)?.userAlbums?.let { album ->
            UserAlbumItem(
                album = album.toUserAlbumState(),
                shape = RoundedCornerShape(8.dp),
                miniIcons = true,
                onAlbumSelected = {},
            )
        }
    },
    AutoAlbumSearchSuggestion.TYPE to {
        (it as? AutoAlbumSearchSuggestion)?.autoAlbum?.let { album ->
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
                    Icon(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colors.background)
                            .padding(2.dp),
                        painter = painterResource(id = R.drawable.ic_creation),
                        contentDescription = null,
                    )
                }
            }
        }
    },
)
