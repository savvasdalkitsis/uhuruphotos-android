package com.savvasdalkitsis.uhuruphotos.ui.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun BackNavButton(
    modifier: Modifier = Modifier,
    furtherContent: @Composable () -> Unit = {},
    onBackPressed: () -> Unit,
) {
    IconButton(
        modifier = modifier,
        onClick = onBackPressed,
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "back")
            furtherContent()
        }
    }
}