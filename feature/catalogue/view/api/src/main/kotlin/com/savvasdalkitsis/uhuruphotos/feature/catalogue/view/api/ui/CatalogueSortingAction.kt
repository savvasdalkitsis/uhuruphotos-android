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
package com.savvasdalkitsis.uhuruphotos.feature.catalogue.view.api.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.savvasdalkitsis.uhuruphotos.feature.catalogue.view.api.ui.state.CatalogueSortingState
import com.savvasdalkitsis.uhuruphotos.feature.catalogue.view.api.ui.state.CatalogueSortingState.ALPHABETICAL_ASC
import com.savvasdalkitsis.uhuruphotos.feature.catalogue.view.api.ui.state.CatalogueSortingState.ALPHABETICAL_DESC
import com.savvasdalkitsis.uhuruphotos.feature.catalogue.view.api.ui.state.CatalogueSortingState.DATE_ASC
import com.savvasdalkitsis.uhuruphotos.feature.catalogue.view.api.ui.state.CatalogueSortingState.DATE_DESC
import com.savvasdalkitsis.uhuruphotos.foundation.strings.api.R.string
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.icon.DropDownActionIcon

@Composable
internal fun CatalogueSortingAction(
    sorting: CatalogueSortingState,
    sortingChanged: (CatalogueSortingState) -> Unit,
) {
    DropDownActionIcon(
        icon = sorting.icon,
        contentDescription = stringResource(string.sorting),
    ) {
        item("Date descending") {
            sortingChanged(DATE_DESC)
        }
        item("Date ascending") {
            sortingChanged(DATE_ASC)
        }
        item("Title descending") {
            sortingChanged(ALPHABETICAL_DESC)
        }
        item("Title ascending") {
            sortingChanged(ALPHABETICAL_ASC)
        }
    }
}