package com.savvasdalkitsis.uhuruphotos.api.albums.view

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.savvasdalkitsis.uhuruphotos.api.albums.view.state.AlbumSorting
import com.savvasdalkitsis.uhuruphotos.api.icons.R
import com.savvasdalkitsis.uhuruphotos.api.ui.view.DropDownActionIcon

@Composable
internal fun AlbumsSortingAction(
    sorting: AlbumSorting,
    sortingChanged: (AlbumSorting) -> Unit,
) {
    DropDownActionIcon(
        icon = when (sorting) {
            AlbumSorting.DATE_DESC -> R.drawable.ic_sort_date_descending
            AlbumSorting.DATE_ASC -> R.drawable.ic_sort_date_ascending
            AlbumSorting.ALPHABETICAL_ASC -> R.drawable.ic_sort_az_ascending
            AlbumSorting.ALPHABETICAL_DESC -> R.drawable.ic_sort_az_descending
        },
        contentDescription = stringResource(com.savvasdalkitsis.uhuruphotos.api.strings.R.string.sorting),
    ) {
        item("Date descending") {
            sortingChanged(AlbumSorting.DATE_DESC)
        }
        item("Date ascending") {
            sortingChanged(AlbumSorting.DATE_ASC)
        }
        item("Title descending") {
            sortingChanged(AlbumSorting.ALPHABETICAL_DESC)
        }
        item("Title ascending") {
            sortingChanged(AlbumSorting.ALPHABETICAL_ASC)
        }
    }
}