package com.savvasdalkitsis.uhuruphotos.ui.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

@Composable
fun ColumnScope.SheetHandle() {
    Box(
        modifier = Modifier
            .padding(8.dp)
            .background(MaterialTheme.colors.onBackground)
            .clip(RoundedCornerShape(4.dp))
            .align(Alignment.CenterHorizontally)
    ) {
        Box(
            modifier = Modifier
                .background(MaterialTheme.colors.onBackground)
                .width(24.dp)
                .height(4.dp)
        )
    }
}