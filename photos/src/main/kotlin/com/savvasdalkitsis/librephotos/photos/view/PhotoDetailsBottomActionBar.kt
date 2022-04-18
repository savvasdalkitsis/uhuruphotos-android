package com.savvasdalkitsis.librephotos.photos.view

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.savvasdalkitsis.librephotos.icons.R
import com.savvasdalkitsis.librephotos.photos.mvflow.PhotoAction
import com.savvasdalkitsis.librephotos.photos.mvflow.PhotoAction.AskForPhotoDeletion
import com.savvasdalkitsis.librephotos.ui.view.ActionIcon
import com.savvasdalkitsis.librephotos.ui.view.ActionIconWithText

@Composable
fun PhotoDetailsBottomActionBar(
    action: (PhotoAction) -> Unit,
) {
    Row {
        ActionIconWithText(
            onClick = { action(AskForPhotoDeletion) },
            modifier = Modifier
                .weight(1f),
            icon = R.drawable.ic_delete,
            text = "Delete",
        )
    }
}