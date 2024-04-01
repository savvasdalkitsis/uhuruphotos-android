/*
Copyright 2023 Savvas Dalkitsis

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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.savvasdalkitsis.uhuruphotos.feature.discover.view.implementation.seam.actions.DiscoverAction
import com.savvasdalkitsis.uhuruphotos.feature.discover.view.implementation.seam.actions.MediaWithoutDateCategorySelected
import com.savvasdalkitsis.uhuruphotos.feature.discover.view.implementation.seam.actions.StatsCategorySelected
import com.savvasdalkitsis.uhuruphotos.feature.discover.view.implementation.seam.actions.VideoCategorySelected
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.R.drawable
import com.savvasdalkitsis.uhuruphotos.foundation.strings.api.R.string
import com.savvasdalkitsis.uhuruphotos.foundation.theme.api.PreviewAppTheme
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.text.ActionRowWithIcon
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.SectionHeader

@Composable
fun DiscoverCategories(
    action: (DiscoverAction) -> Unit = {},
) {
    Column(
        modifier = Modifier
            .padding(
                start = 12.dp,
                end = 12.dp,
                bottom = 12.dp,
            ),
        verticalArrangement = Arrangement.spacedBy(8.dp),
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
        Divider(startIndent = 48.dp)
        ActionRowWithIcon(
            icon = drawable.ic_chart,
            text = stringResource(string.stats),
        ) {
            action(StatsCategorySelected)
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