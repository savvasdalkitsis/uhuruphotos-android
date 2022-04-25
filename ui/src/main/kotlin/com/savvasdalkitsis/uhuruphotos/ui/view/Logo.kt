package com.savvasdalkitsis.uhuruphotos.ui.view

import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import com.savvasdalkitsis.uhuruphotos.icons.R

@Composable
fun Logo() {
    Icon(painterResource(id = R.drawable.ic_logo), contentDescription = null)
}