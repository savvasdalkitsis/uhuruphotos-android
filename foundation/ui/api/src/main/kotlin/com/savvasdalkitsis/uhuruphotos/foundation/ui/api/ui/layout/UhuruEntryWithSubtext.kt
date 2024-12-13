package com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.layout

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp

@Composable
fun UhuruEntryWithSubtext(
    @StringRes subtext: Int?,
    entry: @Composable () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth(),
    ) {
        entry()
        if (subtext != null) {
            Text(
                modifier = Modifier
                    .padding(horizontal = 12.dp),
                text = stringResource(subtext),
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}