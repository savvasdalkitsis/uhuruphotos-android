package com.savvasdalkitsis.uhuruphotos.ui.view

import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable

@Composable
fun BackNavButton(
    onBackPressed: () -> Unit,
) {
    IconButton(onClick = onBackPressed) {
        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "back")
    }
}