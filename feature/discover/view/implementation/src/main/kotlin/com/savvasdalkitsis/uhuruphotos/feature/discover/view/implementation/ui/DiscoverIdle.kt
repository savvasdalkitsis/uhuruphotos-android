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

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.savvasdalkitsis.uhuruphotos.feature.discover.view.implementation.seam.actions.DiscoverAction
import com.savvasdalkitsis.uhuruphotos.feature.discover.view.implementation.seam.actions.MediaWithoutDateCategorySelected
import com.savvasdalkitsis.uhuruphotos.feature.discover.view.implementation.seam.actions.VideoCategorySelected
import com.savvasdalkitsis.uhuruphotos.feature.discover.view.implementation.ui.state.DiscoverState
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.R.drawable
import com.savvasdalkitsis.uhuruphotos.foundation.strings.api.R.string
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.theme.PreviewAppTheme
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.ActionRowWithIcon
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.SectionHeader

@Composable
fun DiscoverIdle(
    state: DiscoverState,
    action: (DiscoverAction) -> Unit,
    contentPadding: PaddingValues
) {
    Column(
        modifier = Modifier.verticalScroll(rememberScrollState()),
        verticalArrangement = spacedBy(16.dp),
    ) {
        val suggestion = state.suggestion
        AnimatedVisibility(
            visible = suggestion != null,
            enter = slideInVertically() + expandVertically(),
            exit = slideOutHorizontally() + shrinkVertically(),
        ) {
            if (suggestion != null) {
                SearchSuggestion(suggestion, action)
            }
        }
        AnimatedVisibility(
            visible = state.people.isNotEmpty(),
            enter = slideInVertically() + expandVertically(),
            exit = slideOutHorizontally() + shrinkVertically(),
        ) {
            SearchPeopleSuggestions(state.people, action)
        }
        DiscoverLocations(state, action)
        DiscoverCategories(action)
        Spacer(modifier = Modifier.height(contentPadding.calculateBottomPadding()))
    }
}

@Composable
fun DiscoverCategories(
    action: (DiscoverAction) -> Unit = {},
) {
    Column(modifier = Modifier
        .padding(
            start = 12.dp,
            end = 12.dp,
            bottom = 12.dp,
        ),
        verticalArrangement = spacedBy(8.dp),
    ) {
        SectionHeader(
            title = stringResource(string.categories),
        ) {}
        ActionRowWithIcon(
            icon = drawable.ic_play_filled,
            text = stringResource(string.videos),
        ) {
            action(VideoCategorySelected)
        }
        Divider(startIndent = 48.dp)
        ActionRowWithIcon(
            icon = drawable.ic_calendar_remove,
            text = stringResource(string.media_without_date),
        ) {
            action(MediaWithoutDateCategorySelected)
        }
    }
}

@Preview
@Composable
private fun DiscoverCategoriesPreview() {
    PreviewAppTheme {
        DiscoverCategories()
    }
}