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
package com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.actions.LightboxAction
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.actions.SearchCaptionSelected
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.ui.info.LightboxCaptionIcons
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.ui.state.SingleMediaItemState
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaId
import com.savvasdalkitsis.uhuruphotos.foundation.strings.api.R.string
import com.savvasdalkitsis.uhuruphotos.foundation.theme.api.PreviewAppTheme
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.SectionHeader
import kotlinx.collections.immutable.toPersistentList

@Composable
fun LightboxSearchCaptions(
    mediaItem: SingleMediaItemState,
    action: (LightboxAction) -> Unit,
) {
    if (mediaItem.searchCaptions.isNotEmpty()) {
        Column(
            verticalArrangement = spacedBy(16.dp)
        ) {
            SectionHeader(
                title = stringResource(string.search_captions)
            ) {}
            FlowRow(
                horizontalArrangement = spacedBy(8.dp),
                verticalArrangement = spacedBy(8.dp),
            ) {
                for ((caption, icon) in mediaItem.searchCaptions) {
                    LightboxSearchCaption(caption, icon, action)
                }
            }
        }
    }
}

@Composable
private fun LightboxSearchCaption(
    caption: String,
    icon: Int?,
    action: (LightboxAction) -> Unit
) {
    Row(
        modifier = Modifier
            .clip(MaterialTheme.shapes.large)
            .background(color = MaterialTheme.colors.onBackground)
            .defaultMinSize(minHeight = 48.dp)
            .padding(horizontal = 8.dp, vertical = 2.dp)
            .clickable { action(SearchCaptionSelected(caption)) },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = spacedBy(4.dp),
    ) {
        if (icon != null) {
            Icon(
                painter = painterResource(icon),
                tint = MaterialTheme.colors.background,
                contentDescription = null,
            )
        }
        Text(
            modifier = Modifier
                .defaultMinSize(minWidth = 24.dp)
                .background(color = MaterialTheme.colors.onBackground)
                .padding(6.dp),
            color = MaterialTheme.colors.background,
            text = caption,
            textAlign = TextAlign.Center
        )
    }
}

@Preview
@Composable
private fun CaptionsPreview() {
    PreviewAppTheme {
        CaptionsPreviewContent()
    }
}

@Preview
@Composable
private fun CaptionsPreviewDark() {
    PreviewAppTheme(darkTheme = true) {
        CaptionsPreviewContent()
    }
}

@Composable
private fun CaptionsPreviewContent() {
    LightboxSearchCaptions(
        SingleMediaItemState(
            id = MediaId.Remote("", false),
            searchCaptions = LightboxCaptionIcons.icons.toList().take(13).toPersistentList(),
        )
    ) {}
}