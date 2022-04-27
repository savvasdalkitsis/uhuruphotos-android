package com.savvasdalkitsis.uhuruphotos.settings.view

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.savvasdalkitsis.uhuruphotos.icons.R

@Composable
fun SettingsGroup(
    title: String,
    content: @Composable ColumnScope.() -> Unit,
) {
    Surface(
        modifier = Modifier.padding(4.dp),
        elevation = 4.dp,
    ) {
        Column {
            val arrowAngle = remember { Animatable(180f) }
            var collapsed by remember { mutableStateOf(false) }
            Box(modifier = Modifier
                .fillMaxWidth()
                .clickable { collapsed = !collapsed }
            ) {
                Row(
                    modifier = Modifier
                        .padding(8.dp)
                        .align(Alignment.Center),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.h6,
                    )
                    Icon(
                        modifier = Modifier.rotate(arrowAngle.value),
                        painter = painterResource(id = R.drawable.ic_arrow_up),
                        contentDescription = null,
                    )
                }
            }
            AnimatedVisibility(visible = !collapsed) {
                Column {
                    content()
                }
            }
            LaunchedEffect(collapsed) {
                arrowAngle.animateTo(
                    when {
                        collapsed -> 180f
                        else -> 0f
                    }
                )
            }
        }
    }
}