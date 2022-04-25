package com.savvasdalkitsis.uhuruphotos.settings.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SettingsTextRow(
    text: String,
    onClick: (() -> Unit)? = null,
) {
    Box(
        modifier = Modifier
            .height(64.dp)
            .fillMaxWidth()
            .apply {
                if (onClick != null) {
                    clickable { onClick() }
                }
            },
    ) {
        Text(
            modifier = Modifier
                .padding(8.dp)
                .align(Alignment.CenterStart),
            text = text,
        )
    }
}