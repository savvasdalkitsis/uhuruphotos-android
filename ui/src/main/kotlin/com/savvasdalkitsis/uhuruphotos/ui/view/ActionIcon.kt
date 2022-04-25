package com.savvasdalkitsis.uhuruphotos.ui.view

import androidx.annotation.DrawableRes
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource

@Composable
fun ActionIcon(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    @DrawableRes icon: Int,
    contentDescription: String? = null
) {
    IconButton(
        modifier = modifier,
        onClick = onClick,
    ) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = contentDescription,
            tint = MaterialTheme.colors.onBackground
        )
    }
}