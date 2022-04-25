package com.savvasdalkitsis.uhuruphotos.ui.view

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

@Composable
fun TextWithIcon(
    modifier: Modifier = Modifier,
    @DrawableRes icon: Int,
    text: String,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Icon(
            modifier = Modifier
                .size(16.dp)
                .align(CenterVertically),
            painter = painterResource(id = icon),
            contentDescription = null
        )
        Text(
            modifier = Modifier
                .align(CenterVertically),
            text = text
        )
    }
}