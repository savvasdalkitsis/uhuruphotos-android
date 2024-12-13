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
package com.savvasdalkitsis.uhuruphotos.feature.library.view.implementation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.savvasdalkitsis.uhuruphotos.feature.library.view.implementation.seam.actions.LibraryAction
import com.savvasdalkitsis.uhuruphotos.feature.library.view.implementation.seam.actions.LocalBucketSelected
import com.savvasdalkitsis.uhuruphotos.feature.library.view.implementation.seam.actions.StartScanningOtherFolders
import com.savvasdalkitsis.uhuruphotos.feature.library.view.implementation.seam.actions.ViewAllLocalFolders
import com.savvasdalkitsis.uhuruphotos.feature.library.view.implementation.ui.state.LibraryLocalMediaState
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaIdModel
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItemHashModel
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItemInstanceModel
import com.savvasdalkitsis.uhuruphotos.feature.media.common.view.api.ui.NamedVitrine
import com.savvasdalkitsis.uhuruphotos.feature.media.common.view.api.ui.state.CelState
import com.savvasdalkitsis.uhuruphotos.feature.media.common.view.api.ui.state.VitrineState
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.model.LocalMediaFolder
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.R.drawable
import com.savvasdalkitsis.uhuruphotos.foundation.strings.api.R.string
import com.savvasdalkitsis.uhuruphotos.foundation.theme.api.PreviewAppTheme
import com.savvasdalkitsis.uhuruphotos.foundation.theme.api.themes.ThemeMode
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.SectionHeader
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.button.IconOutlineButton
import kotlinx.collections.immutable.persistentListOf

@Composable
internal fun LocalFolders(
    modifier: Modifier = Modifier,
    title: String,
    media: LibraryLocalMediaState.FoundState,
    action: (LibraryAction) -> Unit,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        SectionHeader(
            modifier = Modifier.padding(8.dp),
            title = title,
            endContent = {
                TextButton(onClick = { action(ViewAllLocalFolders) }) {
                    Text(stringResource(string.view_all))
                }
            },
        )
        LazyRow(
            modifier = Modifier.heightIn(min = 120.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            for ((bucket, vitrineState) in media.buckets) {
                item(bucket.id) {
                    NamedVitrine(
                        modifier = Modifier.animateItem(),
                        state = vitrineState,
                        photoGridModifier = Modifier.width(120.dp),
                        title = bucket.displayName,
                        iconFallback = -1,
                    ) {
                        action(LocalBucketSelected(bucket))
                    }
                }
            }
            if (!media.scanningOtherFolders) {
                item("other") {
                    IconOutlineButton(
                        modifier = Modifier
                            .widthIn(min = 216.dp)
                            .padding(bottom = 38.dp)
                            .heightIn(min = 72.dp)
                        ,
                        icon = drawable.ic_folder,
                        text = "Scan other device folders",
                    ) {
                        action(StartScanningOtherFolders)
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun LocalFoldersPreview() {
    PreviewAppTheme {
        LocalFoldersAll()
    }
}

@Preview
@Composable
private fun LocalFoldersPreviewDark() {
    PreviewAppTheme(ThemeMode.DARK_MODE) {
        LocalFoldersAll()
    }
}

@Composable
private fun LocalFoldersAll() {
    LocalFolders(title = stringResource(string.local_albums), media = LibraryLocalMediaState.FoundState(persistentListOf(
        LocalMediaFolder(0, "Folder 1") to VitrineState(
            celState("#ff0000"),
            celState("#00ff00"),
            celState("#0000ff"),
            celState("#00ffff"),
        ),
        LocalMediaFolder(1, "Folder 2") to VitrineState(
            celState("#00ff00"),
            celState("#00ffff"),
            celState("#ff0000"),
            celState("#0000ff"),
        ),
        LocalMediaFolder(2, "Folder 3") to VitrineState(
            celState("#00ffff"),
            celState("#00ff00"),
            celState("#0000ff"),
            celState("#ff0000"),
        ),
        LocalMediaFolder(3, "Folder 4") to VitrineState(
            celState("#00ff00"),
            celState("#0000ff"),
            celState("#00ffff"),
            celState("#ff0000"),
        ),
    ), true)) {

    }
}

@Preview
@Composable
private fun LocalFoldersPreviewWithoutOther() {
    PreviewAppTheme {
        LocalFoldersWithoutOther()
    }
}

@Preview
@Composable
private fun LocalFoldersPreviewWithoutOtherDark() {
    PreviewAppTheme(ThemeMode.DARK_MODE) {
        LocalFoldersWithoutOther()
    }
}

@Composable
private fun LocalFoldersWithoutOther() {
    LocalFolders(title = stringResource(string.local_albums), media = LibraryLocalMediaState.FoundState(persistentListOf(
        LocalMediaFolder(0, "Folder 1") to VitrineState(
            celState("#ff0000"),
            celState("#00ff00"),
            celState("#0000ff"),
            celState("#00ffff"),
        ),
    ), false)) {

    }
}

@Composable
private fun celState(color: String) = CelState(
    MediaItemInstanceModel(
        MediaIdModel.LocalIdModel(0, 0, false, "", ""),
        MediaItemHashModel.fromRemoteMediaHash("", 0),
        fallbackColor = color
    )
)
