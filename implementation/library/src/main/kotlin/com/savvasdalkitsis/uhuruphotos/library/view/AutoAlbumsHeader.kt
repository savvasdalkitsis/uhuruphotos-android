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
package com.savvasdalkitsis.uhuruphotos.library.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.savvasdalkitsis.uhuruphotos.library.seam.LibraryAction
import com.savvasdalkitsis.uhuruphotos.library.seam.LibraryAction.ChangeSorting
import com.savvasdalkitsis.uhuruphotos.library.view.state.AutoAlbumSorting.ALPHABETICAL_ASC
import com.savvasdalkitsis.uhuruphotos.library.view.state.AutoAlbumSorting.ALPHABETICAL_DESC
import com.savvasdalkitsis.uhuruphotos.library.view.state.AutoAlbumSorting.DATE_ASC
import com.savvasdalkitsis.uhuruphotos.library.view.state.AutoAlbumSorting.DATE_DESC
import com.savvasdalkitsis.uhuruphotos.library.view.state.LibraryState
import com.savvasdalkitsis.uhuruphotos.api.strings.R
import com.savvasdalkitsis.uhuruphotos.ui.view.DropDownActionIcon
import com.savvasdalkitsis.uhuruphotos.api.icons.R as Icons

@Composable
internal fun AutoAlbumsHeader(
    state: LibraryState,
    action: (LibraryAction) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            modifier = Modifier
                .weight(1f)
                .padding(8.dp),
            text = stringResource(R.string.auto_generated_albums),
            style = MaterialTheme.typography.h6,
        )
        DropDownActionIcon(
            icon = when (state.sorting) {
                DATE_DESC -> Icons.drawable.ic_sort_date_descending
                DATE_ASC -> Icons.drawable.ic_sort_date_ascending
                ALPHABETICAL_ASC -> Icons.drawable.ic_sort_az_ascending
                ALPHABETICAL_DESC -> Icons.drawable.ic_sort_az_descending
            },
            contentDescription = stringResource(R.string.sorting),
        ) {
            item("Date descending") {
                action(ChangeSorting(DATE_DESC))
            }
            item("Date ascending") {
                action(ChangeSorting(DATE_ASC))
            }
            item("Title descending") {
                action(ChangeSorting(ALPHABETICAL_DESC))
            }
            item("Title ascending") {
                action(ChangeSorting(ALPHABETICAL_ASC))
            }
        }
    }
}