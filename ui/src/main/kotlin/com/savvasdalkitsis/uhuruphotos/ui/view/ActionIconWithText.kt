package com.savvasdalkitsis.uhuruphotos.ui.view

import androidx.annotation.DrawableRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ActionIconWithText(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    @DrawableRes icon: Int,
    text: String,
) {
    Column(
        modifier = modifier.clickable { onClick() },
    ) {
        ActionIcon(
            modifier = Modifier.align(CenterHorizontally),
            onClick = onClick,
            icon = icon,
            contentDescription = text,
        )
        Text(
            modifier = Modifier
                .align(CenterHorizontally)
                .padding(bottom = 8.dp),
            text = text,
        )
    }
}