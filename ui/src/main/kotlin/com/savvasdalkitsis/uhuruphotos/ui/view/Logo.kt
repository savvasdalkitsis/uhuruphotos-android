package com.savvasdalkitsis.uhuruphotos.ui.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.savvasdalkitsis.uhuruphotos.icons.R

@Composable
fun Logo(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
) {
    Icon(
        modifier = modifier
            .clip(CircleShape)
            .let {
                if (onClick != null) {
                    it.clickable { onClick() }
                } else {
                    it
                }
            }
            .padding(2.dp),
        painter = painterResource(id = R.drawable.ic_logo),
        contentDescription = null
    )
}