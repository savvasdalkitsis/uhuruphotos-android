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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.actions.LightboxAction
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.actions.SearchCaptionSelected
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.ui.info.LightboxCaptionIcons
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.ui.info.LightboxCaptionIcons.icon
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.ui.state.LightboxDetailsState
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.ui.state.SingleMediaItemState
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaIdModel
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItemHashModel
import com.savvasdalkitsis.uhuruphotos.foundation.theme.api.PreviewAppTheme
import com.savvasdalkitsis.uhuruphotos.foundation.theme.api.themes.ThemeMode
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.SectionHeader
import kotlinx.collections.immutable.toImmutableSet
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import uhuruphotos_android.foundation.strings.api.generated.resources.Res.string
import uhuruphotos_android.foundation.strings.api.generated.resources.search_captions

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun LightboxSearchCaptions(
    mediaItem: SingleMediaItemState,
    action: (LightboxAction) -> Unit,
) {
    if (mediaItem.details.searchCaptions.isNotEmpty()) {
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
                for (caption in mediaItem.details.searchCaptions) {
                    LightboxSearchCaption(caption, icon(caption), action)
                }
            }
        }
    }
}

@Composable
private fun LightboxSearchCaption(
    caption: String,
    icon: DrawableResource?,
    action: (LightboxAction) -> Unit
) {
    Row(
        modifier = Modifier
            .clip(MaterialTheme.shapes.large)
            .background(color = MaterialTheme.colorScheme.onBackground)
            .defaultMinSize(minHeight = 48.dp)
            .padding(horizontal = 8.dp, vertical = 2.dp)
            .clickable { action(SearchCaptionSelected(caption)) },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = spacedBy(4.dp),
    ) {
        if (icon != null) {
            Icon(
                modifier = Modifier.size(24.dp),
                painter = painterResource(icon),
                tint = MaterialTheme.colorScheme.background,
                contentDescription = null,
            )
        }
        Text(
            modifier = Modifier
                .defaultMinSize(minWidth = 24.dp)
                .background(color = MaterialTheme.colorScheme.onBackground)
                .padding(6.dp),
            color = MaterialTheme.colorScheme.background,
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
    PreviewAppTheme(themeMode = ThemeMode.DARK_MODE) {
        CaptionsPreviewContent()
    }
}

@Composable
private fun CaptionsPreviewContent() {
    Box(Modifier.verticalScroll(rememberScrollState())) {

        LightboxSearchCaptions(
            SingleMediaItemState(
                id = MediaIdModel.RemoteIdModel("", false, MediaItemHashModel.fromRemoteMediaHash("", 0)),
                details = LightboxDetailsState(
                    searchCaptions = LightboxCaptionIcons.icons.keys.toList().toImmutableSet()
                )
            )
        ) {}
    }
}